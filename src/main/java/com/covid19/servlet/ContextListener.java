package com.covid19.servlet;

import com.covid19.configuration.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.dbcp2.BasicDataSource;
import uploadDataToDataBase.UploadDataFromGitToDataBase;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.crypto.Data;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final Thread thread = new Thread(new Runnable() {

            private Date getLastDate() throws SQLException {
                final BasicDataSource dataSource = DataSource.getDataSource();
                final Connection connection = dataSource.getConnection();
                final Statement statement = connection.createStatement();
                final ResultSet resultSet = statement.executeQuery("select max(date) from cases");
                Date lastUpdate = null;
                while (resultSet.next()) {
                    lastUpdate = resultSet.getDate("max");
                }
                statement.close();
                connection.close();
                return lastUpdate;
            }


            @SneakyThrows
            @Override
            public void run() {
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
                while (true) {
                    Date lastDate = getLastDate();
                    lastDate.setTime(lastDate.getTime() + 24 * 60 * 60 * 1000);
                    System.out.println(lastDate.toString());
                    final String date = simpleDateFormat.format(lastDate);

                    String stringBuffer = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/" +
                            date +
                            ".csv";

                    URL url = new URL(stringBuffer);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int code = connection.getResponseCode();

                    if (code >= 200 && code <= 399) {
                        UploadDataFromGitToDataBase.uploadToDataBase(simpleDateFormat.format(lastDate));
                    }

                    Thread.sleep(21600000);
                }
            }
        });
        thread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
