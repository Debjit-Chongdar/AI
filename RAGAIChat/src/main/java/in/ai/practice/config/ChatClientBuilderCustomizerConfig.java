package in.ai.practice.config;

import org.springframework.ai.chat.client.ChatClientBuilderCustomizer;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//If we add this config then all the bean initialized here
// will be by default applied to ChatClient Builder

// But to create chat client you have to
// use ChatClient simpleChatClient(ChatClient.Builder builder){}
// instead ChatClient simpleChatClient(ChatModel chatModel){}
@Configuration
public class ChatClientBuilderCustomizerConfig {

    @Bean
    public ChatClientBuilderCustomizer loggerCustomizer() {
        return builder -> builder.defaultAdvisors(new SimpleLoggerAdvisor());
    }

    @Bean
    public ChatClientBuilderCustomizer chatRAGCustomizer(@Qualifier("ragAdvisor") RetrievalAugmentationAdvisor ragAdvisor) {
        return builder -> builder.defaultAdvisors(ragAdvisor);
    }
}
