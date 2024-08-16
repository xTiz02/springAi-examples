package org.prd.pgvectordemo.vectorConsult.service;

import org.prd.pgvectordemo.vectorConsult.model.Place;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import java.util.List;

@Service
public class EmbeddingService {

    private static final float MATCH_THRESHOLD = 0.7f;
    private static final int MATCH_CNT = 3;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private EmbeddingModel aiClient;


    public EmbeddingService(JdbcClient jdbcClient, EmbeddingModel aiClient) {
        this.jdbcClient = jdbcClient;
        this.aiClient = aiClient;
    }

    public List<Place> searchPlaces(String prompt) {
        float[] promptEmbedding = aiClient.embed(prompt);

        StatementSpec query = jdbcClient.sql(
                        "SELECT name, description, price, 1 - (description_embedding <=> :user_promt::vector) as similarity " +
                                "FROM airbnb_listing WHERE 1 - (description_embedding <=> :user_promt::vector) > :match_threshold "
                                +
                                "ORDER BY description_embedding <=> :user_promt::vector LIMIT :match_cnt")
                .param("user_promt", promptEmbedding.toString())
                .param("match_threshold", MATCH_THRESHOLD)
                .param("match_cnt", MATCH_CNT);

        return query.query(Place.class).list();
    }
}
