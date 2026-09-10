package in.ai.practice.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

//it will help to keep memory of old chat, but it will use token to send those chat history
@RestController
@RequestMapping("/rag")
public class RagChatController {

    private final ChatClient dbStoreChatClient;
    private final VectorStore vectorStore;
    @Value("classpath:/system/hrpolicyTemplate.st")
    private Resource hrpolicyTemplate;

    public RagChatController(
            @Qualifier("dbStoreChatClient")  ChatClient dbStoreChatClient,
            VectorStore vectorStore
    ) {
        this.dbStoreChatClient = dbStoreChatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/chat")
    public String dbStorechat(
            @RequestParam String message,
            @RequestParam(required = false,defaultValue = "default") String conversationId
    ) {
        SearchRequest  searchRequest = SearchRequest.builder()
                .query(message)
                .topK(3)//fetch top 3 content from vector store
                .similarityThreshold(0.5)//if similarity of the content more or eq 50%
                .build();
        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);
        String similarContext = similarDocs.stream()
                .map(Document::getText).collect(Collectors.joining());

        return this.dbStoreChatClient
                .prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(hrpolicyTemplate).param("documents", similarContext))
                //Default ChatMemory work on conversation id
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, conversationId))
                .user(message)
                .call().content();
    }
}
