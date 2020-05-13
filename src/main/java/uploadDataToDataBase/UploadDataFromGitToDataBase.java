package uploadDataToDataBase;

import com.opencsv.CSVReader;
import lombok.SneakyThrows;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.concurrent.ForkJoinTask.invokeAll;

public class UploadDataFromGitToDataBase {
    final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");

    public static void main(String[] args) throws IOException, ParseException, SQLException {
        uploadToDataBase("02-22-2020");
        System.out.println("end");
    }


    static List<String[]> getChunk(String fromDate, int numberCore) throws IOException, ParseException {
        int numberDays = (int) numberDaysBetweenDate(fromDate);
        Date from = simpleDateFormat.parse(fromDate);
        int volumeChunk = (numberDays / numberCore) + 1;
        if (volumeChunk < 1) {
            volumeChunk = 0;
        }
        List<String[]> chunks = new ArrayList<>();
        String[] chunk = new String[volumeChunk];
        for (int i = 0, j = 0; i <= numberDays + 1; i++, j++) {
            if (j >= volumeChunk || i > numberDays) {
                j = 0;
                chunks.add(chunk);
                chunk = new String[volumeChunk];
            }
            chunk[j] = simpleDateFormat.format(from);
            from.setTime(from.getTime() + 24 * 60 * 60 * 1000);
        }
        return chunks;
    }

    static long numberDaysBetweenDate(String fromDate) throws IOException, ParseException {
        Date from = simpleDateFormat.parse(fromDate);

        Date to = new Date();

        String EndDayOfResults = simpleDateFormat.format(to);

        boolean isEndDayOfResults = true;
        while (isEndDayOfResults) {
            if (checkFileByDate(EndDayOfResults)) {
                isEndDayOfResults = false;
            } else {
                to.setTime(to.getTime() - 24 * 60 * 60 * 1000);
                EndDayOfResults = simpleDateFormat.format(to);
            }
        }

        long diff = to.getTime() - from.getTime();

        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private static boolean checkFileByDate(String date) {
        try {
            String stringBuffer = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/" +
                    date +
                    ".csv";
            URL url = new URL(stringBuffer);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            return code >= 200 && code <= 399;
        }catch (IOException e){
            return false;
        }
    }


    public static void uploadToDataBase(String fromDate) throws IOException, ParseException, SQLException {
        List<String[]> chunks = getChunk(fromDate, Runtime.getRuntime().availableProcessors());
        List<UploadDataToDataBase> task = new ArrayList<>();
        InputStream input = UploadDataFromGitToDataBase.class.getClassLoader().getResourceAsStream("config.properties");
        Properties prop = new Properties();
        prop.load(input);
        String URL = prop.getProperty("db.url") + "?user=" + prop.getProperty("db.user") + "&password=" + prop.getProperty("db.password");

        for (String[] chunk : chunks) {
            task.add(new UploadDataToDataBase(chunk, DriverManager.getConnection(URL)));
        }

        invokeAll(task);
    }


    static class UploadDataToDataBase extends RecursiveAction {
        final String[] chunk;
        final Connection connection;
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        int batchSize = 20;

        public UploadDataToDataBase(String[] chunk, Connection connection) {
            this.chunk = chunk;
            this.connection = connection;
        }

        @SneakyThrows
        @Override
        protected void compute() {
            Arrays.stream(chunk).filter(Objects::nonNull).forEach(date -> {
                String stringBuffer = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/" +
                        date +
                        ".csv";
                URL uri;
                try {
                    uri = new URL(stringBuffer);
                    HttpURLConnection con = (HttpURLConnection) uri.openConnection();
                    con.setRequestMethod("GET");
                    final List<String[]> oneDay = new ArrayList<>();
                    try (CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(con.getInputStream())))) {
                        String[] line;
                        while ((line = csvReader.readNext()) != null) {
                            oneDay.add(line);
                        }
                    } finally {
                        con.disconnect();
                    }

                    String confirmed = "Confirmed";
                    String deaths = "Deaths";
                    String recovered = "Recovered";
                    String country = "Country";

                    int indexConfirmed = -1;
                    int indexDeaths = -1;
                    int indexRecovered = -1;
                    int indexCountry = -1;

                    String[] title = oneDay.get(0);
                    for (int i = 0; i < title.length; i++) {
                        if (title[i].contains(confirmed)) {
                            indexConfirmed = i;
                        }
                        if (title[i].contains(country)) {
                            indexCountry = i;
                        }
                        if (title[i].contains(deaths)) {
                            indexDeaths = i;
                        }
                        if (title[i].contains(recovered)) {
                            indexRecovered = i;
                        }
                    }

                    oneDay.remove(0);
                    int finalIndexCountry = indexCountry;
                    int finalIndexConfirmed = indexConfirmed;
                    int finalIndexDeaths = indexDeaths;
                    int finalIndexRecovered = indexRecovered;

                    Map<String, DoubleSummaryStatistics> collectConfirmed = oneDay.parallelStream().collect(Collectors.groupingBy(f -> f[finalIndexCountry], Collectors.summarizingDouble(f -> Double.parseDouble(f[finalIndexConfirmed]))));
                    Map<String, DoubleSummaryStatistics> collectDeaths = oneDay.parallelStream().collect(Collectors.groupingBy(f -> f[finalIndexCountry], Collectors.summarizingDouble(f -> Double.parseDouble(f[finalIndexDeaths]))));
                    Map<String, DoubleSummaryStatistics> collectRecovered = oneDay.parallelStream().collect(Collectors.groupingBy(f -> f[finalIndexCountry], Collectors.summarizingDouble(f -> Double.parseDouble(f[finalIndexRecovered]))));

                    PreparedStatement preparedStatement = connection.prepareStatement("insert into cases (country, confirmed,deaths,recovered, date) VALUES (?,?,?,?,?)");


                    List<String> strings = new ArrayList<>(collectConfirmed.keySet());
                    for (int i = 0, j = 0; i < strings.size(); i++, j++) {
                        if (j >= batchSize) {
                            preparedStatement.executeBatch();
                            j = 0;
                        }
                        preparedStatement.setString(1, strings.get(i));
                        preparedStatement.setDouble(2, collectConfirmed.get(strings.get(i)).getSum());
                        preparedStatement.setDouble(3, collectDeaths.get(strings.get(i)).getSum());
                        preparedStatement.setDouble(4, collectRecovered.get(strings.get(i)).getSum());
                        preparedStatement.setTimestamp(5, new Timestamp(simpleDateFormat.parse(date).getTime()));
                        preparedStatement.addBatch();
                    }

                    preparedStatement.executeBatch();
                } catch (IOException | SQLException | ParseException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
