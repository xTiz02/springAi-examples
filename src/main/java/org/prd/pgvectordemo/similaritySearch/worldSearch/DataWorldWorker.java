package org.prd.pgvectordemo.similaritySearch.worldSearch;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DataWorldWorker {

    private final JdbcClient jdbcCliPostgres;

    private final JdbcClient jdbcCliVector;

    private final VectorStore vectorStore;

    private final OllamaChatModel ollamaChatModel;

    private String template = """
            <INST>You are helping to create a short sentence or paragraph, containing only
            the data values from the DATA section that are in key value format. <INST>
            
            <OPTIONS>
            Do not change the format of the data values of each data and write them as
            they are shown without simplifying or altering them and do not add additional
            information to the text, use only and exclusively the data values provided to
            create a meaningful text of a maximum of 300 characters.
            </OPTIONS>.
            
            <EXAMPLE>
            DATA(key-value):
            code: AFG
            country_name: Afghanistan
            continent: Asia
            region: Southern and Central Asia
            independence_year: 1919
            population: 22720000
            gnp: 5976.00
            local_name: Afganistan/Afqanestan
            form_of_government: Islamic Emirate
            head_of_state: Mohammad Omar
            code2: AF
            capital: Kabul
            capital_population: 1780000
            RESPONSE:
            Afghanistan (AFG), located in Southern and Central Asia, gained independence in 1919
            and has a population of 22720000 with a GNP of 5976.00. The capital is Kabul
            with 1780000 residents. Afghanistan is an Islamic Emirate led by Mohammad Omar,
            and its local name is Afganistan/Afqanestan (code: AF).
            </EXAMPLE>
            
            DATA(key-value):
            {data}
            """;

    private final EmbeddingModel aiClient;

    public DataWorldWorker(@Qualifier("jdbcClientPostgres") JdbcClient jdbcCliPostgres,
                           @Qualifier("jdbcClientVector") JdbcClient jdbcCliVector,
                           @Qualifier("pgTableDemo") VectorStore vectorStore,
                           OllamaChatModel ollamaChatModel,
                           @Qualifier("embeddingModel") EmbeddingModel aiClient) {
        this.jdbcCliPostgres = jdbcCliPostgres;
        this.jdbcCliVector = jdbcCliVector;
        this.vectorStore = vectorStore;
        this.ollamaChatModel = ollamaChatModel;
        this.aiClient = aiClient;
    }

    @PostConstruct
    public void init() {
        String sql = "select\n" +
                "    country.code,\n" +
                "    country.name as country_name,\n" +
                "    country.continent,\n" +
                "    country.region,\n" +
                "    country.indepyear as independence_year,\n" +
                "    country.population,\n" +
                "    country.gnp,\n" +
                "    country.localname as local_name,\n" +
                "    country.governmentform as form_of_government,\n" +
                "    country.headofstate as head_of_state,\n" +
                "    country.code2,\n" +
                "    city.name as capital,\n" +
                "    city.population as capital_population\n" +
                "from country left join city on country.capital = city.id where country.code = 'PER';";

        StatementSpec st = jdbcCliPostgres.sql(sql);
        Set<String> columns = st.query().listOfRows().stream().map(Map::keySet).findFirst().get();
        log.info("Columns: {}", columns);
        List<Document> documents = new ArrayList<>();
        Message createdMessage;
        //Recorrer las filas
        for (Map<String, Object> row : st.query().listOfRows()) {
            String data = columns.stream().map(column -> column + ": " + row.get(column)).collect(Collectors.joining(System.lineSeparator()));
            createdMessage = new SystemPromptTemplate(template).createMessage(Map.of("data", data));
            log.info("Data: {}", data);
            var prompt = new Prompt(List.of(createdMessage));
            var chatResponse = ollamaChatModel.call(prompt);
            String response = chatResponse.getResult().getOutput().getContent();
            log.info("Response: {}", response);
            Document doc = Document.builder().withContent(response).withMetadata(Map.of("columns", columns)).build();
            documents.add(doc);
        }
        this.vectorStore.add(documents);//agregar los documentos a la base de datos vector
        log.info("Vector data created for {}", "DataCountry");






        //return chatResponse.getResult().getOutput().getContent();
        //tables names
        //System.out.println(query.query().listOfRows().stream().map(Map::keySet).findFirst().get());

        /*String sql = "SELECT count(*) FROM vector_store WHERE metadata->>'file_name' = :source";

        Integer count = jdbcClient.sql(sql).
                param("source", pdfResource.getFilename()).
                query(Integer.class).
                single();
        if (count > 0) {
            log.info("Vector already exists for {}", pdfResource.getFilename());
            return;
        }*/
    }
}























//phi3 modifica los datos
    /*
    * Create a single, short, meaningful text of up to 350 characters containing only the following data, do not add additional information.
code: AFG
country_name: Afghanistan
continent: Asia
region: Southern and Central Asia
independence_year: 1919
population: 22720000
gnp: 5976.00
localname: Afganistan/Afqanestan
governmentform: Islamic Emirate
headofstate: Mohammad Omar
code2: AF
capital: Kabul
capital_population: 1780000
*/




//mistral,llama3 god
   /*
<INST>You are helping to create a short sentence or paragraph, containing only the data values from the DATA section that are in key value format. <INST>


<OPTIONS>
Do not change the format of the data values of each data and write them as they are shown without simplifying or altering them and do not add additional information to the text, use only and exclusively the data values provided to create a meaningful text of a maximum of 300 characters.
</OPTIONS>.

<EXAMPLE>
DATA(key-value):
code:PER
country_name: Peru
continent: South America
region: South America
independence_year: 1821
population: 25662000
gnp: 64140.00
local_name: Perú/Piruw
form_of_government: Republic
head_of_state: Valentin Paniagua Corazao
code2: PE
capital: Lima
capital_population: 6464693

RESPONSE:
Peru (PER), located in South America, gained independence in 1821 and has a population of 25662000 with a GNP of 6414000. The capital is Lima with 6464693 residents. Peru is a republic led by Valentin Paniagua Corazao, and its local name is Perú/Piruw (code: PE).
</EXAMPLE>

DATA(key-value):
code: AFG
country_name: Afghanistan
continent: Asia
region: Southern and Central Asia
independence_year: 1919
population: 22720000
gnp: 5976.00
localname: Afganistan/Afqanestan
governmentform: Islamic Emirate
headofstate: Mohammad Omar
code2: AF
capital: Kabul
capital_population: 1780000*/

