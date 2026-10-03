package in.ai.practice.config;

import io.qdrant.client.QdrantClient;
import org.springframework.ai.chat.cache.semantic.SemanticCache;
import org.springframework.ai.chat.cache.semantic.SemanticCacheAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.vectorstore.redis.cache.semantic.DefaultSemanticCache;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import redis.clients.jedis.RedisClient;

import java.util.List;

@Configuration
public class CacheConfig {

    @Bean(name = "cacheVectorStore")
    public VectorStore cacheInVectorStore(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        return QdrantVectorStore
                .builder(qdrantClient, embeddingModel)
                .collectionName("cacheInVectorStore")
                .initializeSchema(true)
                .build();
    }

    /*@Bean
    public RedisClient redisClient(
            @Value("{spring.data.redis.host:localhost}")String host,
            @Value("{spring.data.redis.port:6379}")int port){
        return RedisClient.builder().hostAndPort(host, port).build();
    }*/

    @Bean(name = "vectorSemanticCache")
    public SemanticCache vectorSemanticCache(
            @Qualifier("cacheVectorStore") VectorStore vectorStore,
            EmbeddingModel embeddingModel
    ) {
        return DefaultSemanticCache.builder()
                .vectorStore(vectorStore)
                .embeddingModel(embeddingModel)
                .similarityThreshold(0.6)
                .build();
    }

    /*@Bean(name = "redisSemanticCache")
    public SemanticCache redisSemanticCache(
            RedisClient redisClient,
            EmbeddingModel embeddingModel
    ) {
        return DefaultSemanticCache.builder()
                .jedisClient(redisClient)
                .embeddingModel(embeddingModel)
                .similarityThreshold(0.6)
                .indexName("redis")
                .prefix("cache.")
                .build();
    }*/

    @Bean(name = "vectorSemanticCacheAdvisor")
    public SemanticCacheAdvisor vectorSemanticCacheAdvisor(@Qualifier("vectorSemanticCache") SemanticCache vectorSemanticCache) {
        return SemanticCacheAdvisor.builder()
                .cache(vectorSemanticCache)
                .build();
    }

    /*@Bean(name = "redisSemanticCacheAdvisor")
    public SemanticCacheAdvisor redisSemanticCacheAdvisor(@Qualifier("redisSemanticCache") SemanticCache redisSemanticCache) {
        return SemanticCacheAdvisor.builder()
                .cache(redisSemanticCache)
                .build();
    }*/

    @Bean(name = "vectorChatClient")
    public ChatClient vectorChatClient(OllamaChatModel chatModel,
                                       @Qualifier("jdbcChatMemory") ChatMemory chatMemory,
                                       @Qualifier("ragAdvisor") RetrievalAugmentationAdvisor retrievalAugmentationAdvisor,
                                       @Qualifier("vectorSemanticCacheAdvisor") SemanticCacheAdvisor vectorSemanticCacheAdvisor) {
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        List.of(
                                new SimpleLoggerAdvisor(),
                                memoryAdvisor,
                                retrievalAugmentationAdvisor,
                                vectorSemanticCacheAdvisor
                        )
                )
                .build();
    }

    /*@Bean(name = "redisChatClient")
    public ChatClient redisChatClient(OllamaChatModel chatModel,
                                       @Qualifier("jdbcChatMemory") ChatMemory chatMemory,
                                       @Qualifier("ragAdvisor") RetrievalAugmentationAdvisor retrievalAugmentationAdvisor,
                                       @Qualifier("redisSemanticCacheAdvisor") SemanticCacheAdvisor redisSemanticCacheAdvisor) {
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        List.of(
                                new SimpleLoggerAdvisor(),
                                memoryAdvisor,
                                retrievalAugmentationAdvisor,
                                redisSemanticCacheAdvisor
                        )
                )
                .build();
    }*/
}
