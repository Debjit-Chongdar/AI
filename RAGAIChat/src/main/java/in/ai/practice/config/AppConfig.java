package in.ai.practice.config;

import in.ai.practice.client.TavilyApiService;
import in.ai.practice.rag.HRPolicyDataLoader;
import in.ai.practice.rag.MaskingSensitiveInfo;
import in.ai.practice.util.WebSearchRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {

    //this is H2 jdbc chat memory is to fetch last 10 message asked in same conversation id
    @Bean("jdbcChatMemory")
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                //max last 10 chat will be kept with a single conversationId
                //default value is 20
                .maxMessages(10)//it will increase token usage
                .build();
    }

    //Store chat memory into db so that it won't lose the data with application restart
    @Bean("dbStoreChatClient")
    public ChatClient getDBStoreChatClient(OllamaChatModel chatModel, @Qualifier("jdbcChatMemory") ChatMemory  chatMemory) {
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), memoryAdvisor))
                .build();
    }

    // Already define in QdrantVectorStoreAutoConfiguration.java
    /*@Bean
    public VectorStore vectorStore(QdrantClient qdrantClient, OllamaEmbeddingModel ollamaEmbeddingModel) {
        return QdrantVectorStore.builder(qdrantClient, ollamaEmbeddingModel).build();
    }*/
    // Vector store to get the related Data
    @Bean
    public HRPolicyDataLoader randomDataLoader(VectorStore vectorStore) {
        return new HRPolicyDataLoader(vectorStore);
    }

    // this advisor will handle SearchRequest within the Vector Store & concatenate the text
    @Bean(name = "ragAdvisor")
    public RetrievalAugmentationAdvisor  retrievalAugmentationAdvisor(VectorStore vectorStore) {
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(4)    // top 4 found data
                .similarityThreshold(0.5) // if similarity is 50 % matching
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
             //   .documentJoiner(new ConcatenationDocumentJoiner())  -- this is default impl, no need to mention additionally
                .build();
    }

    //PreRetrieval & Post Retrieval RAG Advisor
    @Bean(name = "prePostRAGAdvisor")
    public RetrievalAugmentationAdvisor prePostRAGAdvisor(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever
                .builder()
                .vectorStore(vectorStore)
                .topK(3).similarityThreshold(0.5)
                .build();
        QueryTransformer languageTransformerToEnglish = TranslationQueryTransformer
                .builder()
                .chatClientBuilder(chatClientBuilder)
                .targetLanguage("English")
                .build();
        DocumentPostProcessor documentPostProcessor = new MaskingSensitiveInfo();
        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(languageTransformerToEnglish) // PRE Retrieval (language transformer)
                .documentRetriever(documentRetriever)
                .documentPostProcessors(documentPostProcessor) // Post Retrieval (masking sensitive info before send to AI)
                .build();
    }

    //WebSearch RAG advisor will get the data from WeB
    @Bean("webSearchRAGAdvisor")
    public RetrievalAugmentationAdvisor  webSearchAugmentationAdvisor() {
        DocumentRetriever documentRetriever = new WebSearchRetriever(3, new TavilyApiService());
        return RetrievalAugmentationAdvisor
                .builder()
                .documentRetriever(documentRetriever)
                .build();
    }

    // Chat client with Vector store search data (RAG with stored data)
    @Bean(name = "ragChatClient")
    public ChatClient advisorChatClient(OllamaChatModel chatModel,
                                        @Qualifier("jdbcChatMemory") ChatMemory  chatMemory,
                                        @Qualifier("ragAdvisor") RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), memoryAdvisor, retrievalAugmentationAdvisor))
                .build();
    }

    //Chat Client with Pre Post RAG Advisor
    @Bean(name = "prePostRAGChatClient")
    public ChatClient prePostRAGChatClient(OllamaChatModel chatModel,
                                           @Qualifier("jdbcChatMemory") ChatMemory  chatMemory,
                                           @Qualifier("prePostRAGAdvisor") RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), memoryAdvisor, retrievalAugmentationAdvisor))
                .build();
    }

    // Chat client with Web search data (RAG with web data)
    @Bean(name = "webSearchRagChatClient")
    public ChatClient webSearchRagChatClient(OllamaChatModel chatModel,
                                        @Qualifier("jdbcChatMemory") ChatMemory  chatMemory,
                                        @Qualifier("webSearchRAGAdvisor") RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), memoryAdvisor, retrievalAugmentationAdvisor))
                .build();
    }
}
