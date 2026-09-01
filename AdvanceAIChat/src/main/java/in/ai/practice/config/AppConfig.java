package in.ai.practice.config;

import in.ai.practice.advisor.ShowContextAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public ChatClient chatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .build();
    }

    //using this advisor we can see the simple log in the console
    //it's recommended to add Advisor in config level/with builder
    @Bean
    public ChatClient defaultAdvisorChatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(SimpleLoggerAdvisor.builder().build())
                .build();
    }

    //Adding Chat Options
    @Bean
    public ChatClient optionsChatClient(OllamaChatModel chatModel) {
        ChatOptions.Builder chatOptionsBuilder = OllamaChatOptions.builder()
                .model("llama3.2") //select which model to use for this chat client
                .maxTokens(100) // restrict/limit total token including request & response
                .temperature(0.7); // 1 is more creative/random and 0 is focus
        return ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultOptions(chatOptionsBuilder)
                .build();
    }

    @Bean
    public ChatClient contextChatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(new ShowContextAdvisor())
                .build();
    }

    @Bean("inMemoryChatClient")
    public ChatClient getInMemoryChatClient(OllamaChatModel chatModel, ChatMemory  chatMemory) {
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), memoryAdvisor))
                .build();
    }

    //this chatMemory will also impact getInMemoryChatClient bean
    @Bean("jdbcChatMemory")
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                //max last 10 chat will be kept with a single conversationId
                //default value is 20
                .maxMessages(10)//it will increase token usage
                .build();
    }

    @Bean("dbStoreChatClient")
    public ChatClient getDBStoreChatClient(OllamaChatModel chatModel, @Qualifier("jdbcChatMemory") ChatMemory  chatMemory) {
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), memoryAdvisor))
                .build();
    }
}
