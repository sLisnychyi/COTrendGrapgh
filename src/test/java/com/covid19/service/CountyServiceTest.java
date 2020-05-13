package com.covid19.service;

import com.covid19.model.County;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static org.assertj.core.api.Assertions.assertThat;


public class CountyServiceTest {
    @Test
    public void checkListWithCountryNotNull() throws SQLException {
        //given
        CountyService countyService =CountyService.getInstance();
        //when
        List<String> allNameCountry = countyService.getAllNameCountry();
        //than
        assertThat(allNameCountry).isNotNull().isNotEmpty();
    }

    @Test
    public void getSelectedCountriesNotNull() throws SQLException {
        //given
        CountyService countyService = CountyService.getInstance();
        //when
        List<List<County>> confirmed = countyService.getSelectedCountries(Arrays.asList("Ukraine", "Germany"), "confirmed");
        //than
        assertThat(confirmed).isNotNull().isNotEmpty();
    }
}