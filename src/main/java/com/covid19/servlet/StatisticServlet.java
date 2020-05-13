package com.covid19.servlet;

import com.covid19.controller.CountryController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/statistic")
public class StatisticServlet extends HttpServlet {
    CountryController countryController = CountryController.getInstance();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        try (ServletInputStream inputStream = req.getInputStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            char[] charBuffer = new char[128];
            int bytesRead = -1;
            while ((bytesRead = reader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String JSON = stringBuilder.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(JSON);
        final String criterion = jsonNode.get("criterion").asText();
        final JsonNode countriesJSON = jsonNode.get("countries");
        List<String> countries = new ArrayList<>();
        if (countriesJSON.isArray()) {
            for (JsonNode node : countriesJSON) {
                countries.add(node.asText());
            }
        }

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        try {
            writer.println(countryController.getSelectedCountries(countries, criterion));
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            writer.println("[]");
        }


    }
}
