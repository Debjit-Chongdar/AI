package in.ai.practice.config;

import in.ai.practice.advisor.ShowContextAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
