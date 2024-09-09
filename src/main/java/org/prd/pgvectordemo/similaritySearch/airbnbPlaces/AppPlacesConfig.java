package org.prd.pgvectordemo.similaritySearch.airbnbPlaces;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppPlacesConfig {

    @Bean(name = "embeddingModel")
    EmbeddingModel embeddingModel(OllamaApi ollamaApi) {
        return new OllamaEmbeddingModel(ollamaApi, OllamaOptions.create()
                .withModel("nomic-embed-text").build());
    }
}
