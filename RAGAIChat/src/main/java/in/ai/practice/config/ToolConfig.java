package in.ai.practice.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ToolConfig {

    @Bean(value = "simpleChatClient")
    public ChatClient simpleChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor()))
                .build();
    }

    //it will return the exception directly to the client instead formalize by AI
    @Bean
    ToolExecutionExceptionProcessor toolExecutionExceptionProcessor(){
        return new DefaultToolExecutionExceptionProcessor(true);
    }
}
