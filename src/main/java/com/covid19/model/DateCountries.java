package com.covid19.model;

import lombok.Data;

import java.util.List;


@Data
public class DateCountries {
    String day;
    List<County> counties;
}
