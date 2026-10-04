package in.ai.mcp.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfig {
    

    //ToolCallbackProvider is used for MCP
    @Bean
    public ChatClient baseChatClient(ChatClient.Builder builder, ToolCallbackProvider toolCallbackProvider) {
        return builder.defaultTools(toolCallbackProvider).build();
    }
}
