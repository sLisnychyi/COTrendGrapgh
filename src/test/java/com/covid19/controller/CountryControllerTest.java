package com.covid19.controller;

import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;


public class CountryControllerTest {

    @Test
    public void getSelectedCountries() throws SQLException {
        CountryController countryController = CountryController.getInstance();
        countryController.getSelectedCountries(Arrays.asList("Ukraine", "Germany"), "confirmed");
    }
}