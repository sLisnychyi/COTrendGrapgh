package com.covid19.controller;

import com.covid19.model.County;
import com.covid19.service.CountyService;
import com.covid19.utils.StartDate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class CountryController {
    private static CountryController countryController;
    private final CountyService countyService;

    private CountryController() {
        countyService = CountyService.getInstance();
    }

    public static CountryController getInstance() {
        if (countryController == null) {
            countryController = new CountryController();
        }
        return countryController;
    }


    public String getAllNameCountryInJSON() throws SQLException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(countyService.getAllNameCountry());
    }

    public String getSelectedCountries(List<String> countries, String criterion) throws SQLException, ParseException {
        List<List<County>> selectedCountries = countyService.getSelectedCountries(countries, criterion);
        Date from = StartDate.getInstance().getStartDate();
        return "{\"data\": ["
                + selectedCountries.parallelStream().map(s -> "{\"name\":\""
                + getDayBetweenDateAndNow(from, s.get(0).getDate())
                + "\"," + s.parallelStream().map(c -> "\"" + c.getCountry()
                + "\": " + c.getCriterion()).collect(Collectors.joining(",")) + "}").collect(Collectors.joining(","))
                + "]}";
    }

    private int getDayBetweenDateAndNow(Date from, Date to) {
        long diff = to.getTime() - from.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}
