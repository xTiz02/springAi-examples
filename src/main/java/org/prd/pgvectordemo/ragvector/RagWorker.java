package org.prd.pgvectordemo.ragvector;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RagWorker {

    @Autowired
    @Qualifier("pgVectorStoreToRag")
    private PgVectorStore vectorStore;

    @Value("classpath:/static/PrdImportsa.pdf")
    private Resource pdfResource;

    @Autowired
    private JdbcClient jdbcClient;



    @PostConstruct
    public void init() {
        String sql = "SELECT count(*) FROM vector_store WHERE metadata->>'file_name' = :source";

        Integer count = jdbcClient.sql(sql).
                param("source",pdfResource.getFilename()).
                query(Integer.class).
                single();
        if (count > 0) {
            log.info("Vector already exists for {}", pdfResource.getFilename());
            return;
        }

        /*TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(pdfResource);
        List<Document> documents = tikaDocumentReader.get();
        var tokenTextSplitter = new TokenTextSplitter();
        vectorStore.add(tokenTextSplitter.apply(documents));*/

        DocumentReader reader =new PagePdfDocumentReader(
                pdfResource,
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfBottomTextLinesToDelete(3)//eliminar las 3 ultimas lineas de texto de cada pagina  (footer)
                                .withNumberOfTopPagesToSkipBeforeDelete(1) //saltar la primera pagina
                                .build())
                        .withPagesPerDocument(1)//cada pagina es un documento
                        .build());
        var textSplitter = new TokenTextSplitter();
        //agregar metadata a los documentos
        List<Document> documents = reader.get().stream().peek(document -> {
            document.getMetadata().put("characters", document.getContent().length());
        }).toList();
        this.vectorStore.accept(textSplitter.apply(documents));
        log.info("Documents metadata: {}", documents.stream().map(Document::getMetadata).toList());
        log.info("Vector pdf data created for {}", pdfResource.getFilename());

    }
    /*olla
     * Guarda el documento en el content con las configuraciones predeterminadas.
     * Cambiar los chunks por tokens para guardar en varias filas y tener una respuesta rapida.
     * -Probar con pdfReader (https://github.com/luisjavierjn/springai-sample-rag/blob/master/src/main/java/com/brianeno/springai/rag/dataloader/DataLoadingService.java)
     * -Luego de lograr guardar el pdf en varias filas, probar hacer consultas en el proyecto vectorConsult
     * */
}
