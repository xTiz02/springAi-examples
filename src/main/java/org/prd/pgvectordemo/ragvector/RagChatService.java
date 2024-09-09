package org.prd.pgvectordemo.ragvector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RagChatService {

    private static final Logger log = LoggerFactory.getLogger(RagChatService.class);
    private String template = """
            You are helping with programming questions and your answers are short and concise.
            Please use the information in the DOCUMENTS section to provide accurate answers, but act as if you know this information innately.
            If you are unsure, simply state that you do not know.
            DOCUMENTS:
            {documents}
            """;
    private final VectorStore pgVectorStore;
    private final OllamaChatModel ollamaChatModel;

    public RagChatService(@Qualifier("pgVectorStore") VectorStore pgVectorStore, OllamaChatModel ollamaChatModel) {
        this.pgVectorStore = pgVectorStore;
        this.ollamaChatModel = ollamaChatModel;
    }

    public String chat(String message) {
        List<Document> documents = this.pgVectorStore.similaritySearch(message);
        log.info("Documents: {}", documents.size());
        String collect =
                documents.stream().map(Document::getContent).collect(Collectors.joining(System.lineSeparator()));
        Message createdMessage = new SystemPromptTemplate(template).createMessage(Map.of("documents", collect));
        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(createdMessage, userMessage));
        ChatResponse chatResponse = ollamaChatModel.call(prompt);
        log.info("Response: {}", chatResponse);
        return chatResponse.getResult().getOutput().getContent();
    }
}
