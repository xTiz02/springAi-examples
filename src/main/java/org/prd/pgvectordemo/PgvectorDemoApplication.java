package org.prd.pgvectordemo;

import org.prd.pgvectordemo.functWeather.config.WeatherConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

//@EnableConfigurationProperties(WeatherConfigProperties.class)
@SpringBootApplication
public class PgvectorDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PgvectorDemoApplication.class, args);
    }

}
