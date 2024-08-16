package org.prd.pgvectordemo.ragvector;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RagWorker {

    @Autowired
    private PgVectorStore vectorStore;
    @Value("classpath:/static/PrdImportsa.pdf")
    private Resource pdfResource;
    @Autowired
    private JdbcClient jdbcClient;



    @PostConstruct
    public void init() {
        String sql = "SELECT count(*) FROM vector_store WHERE metadata->>'source' = :source";

        Integer count = jdbcClient.sql(sql).
                param("source",pdfResource.getFilename()).
                query(Integer.class).
                single();
        if (count > 0) {
            log.info("Vector already exists for {}", pdfResource.getFilename());
            return;
        }

        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(pdfResource);
        List<Document> documents = tikaDocumentReader.get();
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        vectorStore.add(tokenTextSplitter.apply(documents));
        log.info("Vector pdf data created for {}", pdfResource.getFilename());
    }
    /*
     * Guarda el documento en el content con las configuraciones predeterminadas.
     * Cambiar los chunks por tokens para guardar en varias filas y tener una respuesta rapida.
     * -Probar con pdfReader (https://github.com/luisjavierjn/springai-sample-rag/blob/master/src/main/java/com/brianeno/springai/rag/dataloader/DataLoadingService.java)
     * -Luego de lograr guardar el pdf en varias filas, probar hacer consultas en el proyecto vectorConsult
     * */
}
