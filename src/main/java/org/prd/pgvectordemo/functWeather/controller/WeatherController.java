package org.prd.pgvectordemo.functWeather.controller;

import org.prd.pgvectordemo.functWeather.service.ChatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private ChatService ollamaService;

    @GetMapping("/cities")
    public String cityQuestion(@RequestParam String query) {
        return ollamaService.functionChat(query);
    }

    @GetMapping("/chat")
    public String chatQuestion(@RequestParam String query) {
        return ollamaService.chat(query);
    }
}
