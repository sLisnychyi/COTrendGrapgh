package com.covid19.servlet;

import com.covid19.controller.CountryController;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/country")
public class CountryServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CountryController countryController = CountryController.getInstance();
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        try {
            writer.println(countryController.getAllNameCountryInJSON());
        } catch (SQLException e) {
            e.printStackTrace();
            writer.println("[]");
        }
    }
}
