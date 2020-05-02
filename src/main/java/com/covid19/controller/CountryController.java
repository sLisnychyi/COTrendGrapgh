package com.covid19.controller;

import com.covid19.model.County;
import com.covid19.service.CountyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
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

    public String getSelectedCountries(List<String> countries, String criterion) throws SQLException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        List<List<County>> selectedCountries = countyService.getSelectedCountries(countries, criterion);

        return "{\"data\": ["
                + selectedCountries.parallelStream().map(s -> "{\"name\":\""
                + simpleDateFormat.format(s.get(0).getDate())
                + "\"," + s.parallelStream().map(c -> "\"" + c.getCountry()
                + "\": " + c.getCriterion()).collect(Collectors.joining(",")) + "}").collect(Collectors.joining(","))
                + "]}";
    }
}
