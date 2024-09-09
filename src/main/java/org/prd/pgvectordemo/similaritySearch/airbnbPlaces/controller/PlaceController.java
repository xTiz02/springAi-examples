package org.prd.pgvectordemo.similaritySearch.airbnbPlaces.controller;

import org.prd.pgvectordemo.similaritySearch.airbnbPlaces.model.Place;
import org.prd.pgvectordemo.similaritySearch.airbnbPlaces.service.EmbeddingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @Autowired
    private EmbeddingService ems;

    @GetMapping("/search")
    public ResponseEntity<List<Place>> getEmbedding(@RequestParam String prompt) {
        return ResponseEntity.ok(ems.searchPlaces(prompt));
    }

}
