package com.covid19.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class County {
    private String country;
    private double criterion;
    private Date date;
}
