package org.prd.pgvectordemo;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Order;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Bean(name = "dsPostgres")
    public DataSource dataSourcePostgres(){
        return configureDataSource("postgres");
    }

    @Bean(name = "dsVector")
    public DataSource dataSourceVector() {
        return configureDataSource("vector");
    }


    private DataSource configureDataSource(String schema) {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .driverClassName("org.postgresql.Driver")
                .url(String.format("jdbc:postgresql://localhost:5432/%s", schema))
                .username("user")
                .password("pass")
                .build();
    }

    @Bean(name = "jdbcClientVector")
    public JdbcClient vectorJdbcClient(@Qualifier("dsVector") DataSource dataSource) {
        return JdbcClient.create(dataSource);
    }

    @Bean(name = "jdbcClientPostgres")
    public JdbcClient postgresJdbcClient(@Qualifier("dsPostgres") DataSource dataSource) {
        return JdbcClient.create(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("dsVector") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }






    @Bean(name = "pgVectorStore")
    public VectorStore  pgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return createPgVectorStore(jdbcTemplate, embeddingModel, "vector_store");
    }
    @Bean(name = "pgTableDemo")
    public VectorStore pgTableDemo(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return createPgVectorStore(jdbcTemplate, embeddingModel, "table_demo");
    }

    private VectorStore createPgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel, String tableName) {
        return new PgVectorStore(
                tableName,
                jdbcTemplate, embeddingModel,
                768, PgVectorStore.PgDistanceType.COSINE_DISTANCE,
                false, PgVectorStore.PgIndexType.HNSW,
                false);
        }

}
