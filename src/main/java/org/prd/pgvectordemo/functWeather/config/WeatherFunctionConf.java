package org.prd.pgvectordemo.functWeather.config;

import org.prd.pgvectordemo.functWeather.service.WeatherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class WeatherFunctionConf {


    /*private final WeatherConfigProperties weatherProps;

    public WeatherFunctionConf(WeatherConfigProperties props) {
        this.weatherProps = props;
    }*/

    @Bean
    @Description("Get the current weather conditions for the given city.")
    public Function<WeatherService.Request,WeatherService.Response> currentWeatherFunction() {
        return new WeatherService();
    }
}
