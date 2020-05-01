import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.DAYS;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        DateTimeFormatter sourceTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
        DateTimeFormatter sourceTimeFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate nowDate = LocalDate.now();
        LocalDate sourceDate = LocalDate.parse("02-22-2020", dateTimeFormatter);
        long diffDate = DAYS.between(sourceDate, nowDate);

        String urlDb = "jdbc:postgresql://127.0.0.1:5432/covid19";
        String loginDb = "vashenko";
        String passwordDb = "vashenko";

        Connection connectionDb = DriverManager.getConnection(urlDb, loginDb, passwordDb);
        String sql = "insert into public.covid19 (country_region, last_update, confirmed, deaths, recovered) values(?,?,?,?,?)";
        PreparedStatement preparedStatement = connectionDb.prepareStatement(sql);

        for (int i = 0; i < diffDate; i++) {
            URL urlSource = new URL("https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/" + sourceDate.format(dateTimeFormatter) + ".csv");
            HttpURLConnection connectionSource = (HttpURLConnection) urlSource.openConnection();
            connectionSource.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connectionSource.getInputStream()));

            sourceDate = sourceDate.plusDays(1);

            String line;
            int countLine = 1;
            String[] headLine = new String[0];

            while ((line = in.readLine()) != null) {
                if (countLine == 1) {
                    headLine = line.split(",");
                } else {
                    String country_region = null;
                    Timestamp last_update = null;
                    int confirmed = 0;
                    int deaths = 0;
                    int recovered = 0;

                    String[] splitReq = line.replaceAll("\".*?\"", "").split(",");
                    for (int j = 0; j < splitReq.length; j++) {
                        switch (headLine[j]) {
                            case ("Country/Region"):
                            case ("Country_Region"):
                                country_region = splitReq[j];
                                break;
                            case ("Last Update"):
                            case ("Last_Update"):
                                LocalDateTime finalSourceTime;
                                String sourceTime = splitReq[j];
                                if (sourceTime.contains("/")) {
                                    String date = sourceTime.split(" ")[0];
                                    String day = date.split("/")[0];
                                    String dayFinal = day.length() == 1 ? "0" + day : day;
                                    String month = date.split("/")[1];
                                    String monthFinal = month.length() == 1 ? "0" + month : month;
                                    String year = date.split("/")[2];

                                    String time = sourceTime.split(" ")[1];
                                    String hours = time.split(":")[0];
                                    String hoursFinal = hours.length() == 1 ? "0" + hours : hours;
                                    String minutes = time.split(":")[1];
                                    String minutesFinal = minutes.length() == 1 ? "0" + minutes : minutes;

                                    finalSourceTime = LocalDateTime.parse(dayFinal + "/" +
                                                    monthFinal + "/" +
                                                    year +
                                                    " " +
                                                    hoursFinal + ":" +
                                                    minutesFinal,
                                            sourceTimeFormatter);
                                } else if (sourceTime.contains("T")) {
                                    finalSourceTime = LocalDateTime.parse(sourceTime);
                                } else {
                                    finalSourceTime = LocalDateTime.parse(sourceTime, sourceTimeFormatter2);
                                }
                                last_update = Timestamp.valueOf(finalSourceTime);
                                break;
                            case ("Confirmed"):
                                confirmed = Integer.parseInt(splitReq[j]);
                                break;
                            case ("Deaths"):
                                deaths = Integer.parseInt(splitReq[j]);
                                break;
                            case ("Recovered"):
                                recovered = Integer.parseInt(splitReq[j]);
                                break;
                        }
                    }

                    preparedStatement.setString(1, country_region);
                    preparedStatement.setTimestamp(2, last_update);
                    preparedStatement.setInt(3, confirmed);
                    preparedStatement.setInt(4, deaths);
                    preparedStatement.setInt(5, recovered);
                    preparedStatement.executeUpdate();
                }
                countLine++;
            }
            in.close();
        }
    }
}
