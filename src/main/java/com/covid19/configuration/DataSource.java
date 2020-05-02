package com.covid19.configuration;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DataSource {
    private static BasicDataSource dataSource;

    public static BasicDataSource getDataSource() {
        if (dataSource == null) {
            try (InputStream input = BasicDataSource.class
                    .getClassLoader().getResourceAsStream("config.properties")) {
                Properties prop = new Properties();
                prop.load(input);
                BasicDataSource ds = new BasicDataSource();
                ds.setUrl(prop.getProperty("db.url"));
                ds.setUsername(prop.getProperty("db.user"));
                ds.setPassword(prop.getProperty("db.password"));
                ds.setMinIdle(10);
                ds.setMaxIdle(15);
                ds.setMaxOpenPreparedStatements(100);
                dataSource = ds;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return dataSource;
    }
}
