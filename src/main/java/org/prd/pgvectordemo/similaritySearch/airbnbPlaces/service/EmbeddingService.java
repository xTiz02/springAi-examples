package org.prd.pgvectordemo.similaritySearch.airbnbPlaces.service;

import org.prd.pgvectordemo.similaritySearch.airbnbPlaces.model.Place;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import java.util.List;

@Service
public class EmbeddingService {

    private static final float MATCH_THRESHOLD = 0.7f;
    private static final int MATCH_CNT = 3;

    private final JdbcClient jdbcClient;

    private final EmbeddingModel aiClient;

    public EmbeddingService(@Qualifier("jdbcClientVector") JdbcClient jdbcClient,@Qualifier("embeddingModel") EmbeddingModel aiClient) {
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
    /*
    * De un tabla sql traer los datos con jdbcClient crear un Document con el content con la data relevante y en la metadata un json de otros datos
    * Al hacer el prompt transformarlo en lista float(embed) y hacer la busquedad con sql pasando como parámetro el embed, retorna Documents */
}
