package com.covid19.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class StartDate {
    private static StartDate START_DATE_CONTROLLER;
    private final String startDate = "01-22-2020";
    private Date start;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");

    public static StartDate getInstance() throws ParseException {
        if (START_DATE_CONTROLLER == null) {
            START_DATE_CONTROLLER = new StartDate();
        }
        return START_DATE_CONTROLLER;
    }

    private StartDate() throws ParseException {
        this.start = simpleDateFormat.parse(startDate);
    }

    public Date setNewStartDate(String newStart) throws ParseException {
        start = simpleDateFormat.parse(newStart);
        return start;
    }

    public Date getStartDate() {
        return start;
    }
}
