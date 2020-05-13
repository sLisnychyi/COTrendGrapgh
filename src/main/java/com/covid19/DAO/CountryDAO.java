package com.covid19.DAO;

import com.covid19.model.County;

import java.sql.SQLException;
import java.util.List;

public interface CountryDAO {
    public List<String> getAllNameCountry() throws SQLException;

    public List<List<County>> getSelectedCountries(List<String> countries, String criterion) throws SQLException;
}
