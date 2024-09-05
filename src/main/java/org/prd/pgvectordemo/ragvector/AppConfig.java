package org.prd.pgvectordemo.ragvector;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class AppConfig {

    @Bean
    PgVectorStore pgVectorStoreToRag(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return new PgVectorStore.Builder(jdbcTemplate, embeddingModel)
                .withDimensions(768)
                .withDistanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .withIndexType(PgVectorStore.PgIndexType.HNSW)
                .withVectorTableName("vector_store")
                .withSchemaName("vector").build();
    }
}
