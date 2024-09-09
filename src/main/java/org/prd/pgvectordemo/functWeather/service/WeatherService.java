package org.prd.pgvectordemo.functWeather.service;


import org.prd.pgvectordemo.functWeather.config.WeatherConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;


import java.util.function.Function;


public class WeatherService implements Function<WeatherService.Request,WeatherService.Response> {


    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final String apiKey ="";

    private String apiUrl = "http://api.weatherapi.com/v1";

    private final RestClient restClient;


    public WeatherService() {

        this.restClient = RestClient.create(apiUrl);
    }


    @Override
    public Response apply(Request request) {
        log.info("Weather Request: {}",request);
        Response response = restClient.get()
                .uri("/current.json?key={key}&q={q}", apiKey, request.city)
                .retrieve()
                .body(Response.class);
        log.info("Weather API Response: {}", response);
        return response;
    }




    public record Request(String city) {}
    public record Response(Location location,Current current) {}
    public record Location(String name, String region, String country, Long lat, Long lon,String localtime){}
    public record Current(String temp_c, Condition condition, String wind_mph, String humidity,String last_updated) {}
    public record Condition(String text,String icon){}

}


