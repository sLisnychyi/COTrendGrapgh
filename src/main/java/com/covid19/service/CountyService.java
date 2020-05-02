package com.covid19.service;

import com.covid19.DAO.CountryDAO;
import com.covid19.configuration.DataSource;
import com.covid19.model.County;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.*;

import java.util.stream.Collectors;


public final class CountyService implements CountryDAO {
    private static CountyService countyService;
    private final BasicDataSource basicDataSource;

    private CountyService() {
        basicDataSource = DataSource.getDataSource();
    }

    public static CountyService getInstance() {
        if (countyService == null) {
            countyService = new CountyService();
        }
        return countyService;
    }

    @Override
    public List<String> getAllNameCountry() throws SQLException {
        List<String> countries = new ArrayList<>();
        Connection connection = basicDataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select country from cases group by country order by country");
        while (resultSet.next()) {
            countries.add(resultSet.getString("country"));
        }
        statement.close();
        connection.close();
        return countries;
    }

    @Override
    public List<List<County>> getSelectedCountries(List<String> countries, String criterion) throws SQLException {
        List<County> countiesDateWithCountried = new ArrayList<>();
        Connection connection = basicDataSource.getConnection();
        Statement statement = connection.createStatement();
        StringBuilder query = new StringBuilder("select country, date, ");
        query.append(criterion);
        query.append(" from cases where ");
        query.append(countries.parallelStream().map(s -> " country =  '" + s + "'").collect(Collectors.joining(" or ")));
        query.append(" order by date");
        ResultSet resultSet = statement.executeQuery(query.toString());

        while (resultSet.next()) {
            countiesDateWithCountried.add(County
                    .builder()
                    .country(resultSet.getString("country"))
                    .criterion(resultSet.getDouble(criterion))
                    .date(resultSet.getDate("date"))
                    .build());
        }

        statement.close();
        connection.close();

        return countiesDateWithCountried
                .parallelStream()
                .collect(Collectors.groupingByConcurrent(County::getDate))
                .values()
                .parallelStream()
                .sorted((o1, o2) -> {
                    if (o1.get(0).getDate().before(o2.get(0).getDate())) {
                        return -1;
                    } else if (o1.get(0).getDate().after(o2.get(0).getDate())) {
                        return 1;
                    } else {
                        return 0;
                    }
                })
                .collect(Collectors.toList());
    }
}
