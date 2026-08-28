package in.ai.practice.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prompt-template")
public class PromptTemplateChatController {

    private final ChatClient chatClient;

    public PromptTemplateChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Value("classpath:/user/UserPromptTemplate.st")
    Resource userPromptTemplate;

    @Value("classpath:/system/HrPromptStuffing.st")
    Resource hrPromptStuffing;

    @GetMapping("/chat")
    public String chat(@RequestParam String username, @RequestParam String message) {
        return chatClient.prompt()
                .user(promptUserSpec ->
                        promptUserSpec.text(userPromptTemplate)
                                .param("username", username)
                                .param("userMsg", message)
                )
                .call().content();
    }

    @GetMapping("/hr-chat")
    public String promptStuffingChat(@RequestParam String message) {
        return chatClient.prompt()
                .system(hrPromptStuffing)
                .user(message)
                .call().content();
    }
}
