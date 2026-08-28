package in.ai.practice.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;

    @Value("classpath:/system/HRSystemMsg.st")
    Resource systemHRSystemMsg;

    public ChatController(ChatClient.Builder builder) {
        //we can add defaultUser msg, defaultSystem msg
        this.chatClient = builder.defaultUser("How can you help me?").build();
    }

    @GetMapping("/chat")
    public String chatResponse(@RequestParam("message") String message){
        String systemPrompt = """
                You are a HR agent, answer professionally which is comes under your department
                """;
        return chatClient.prompt().system(systemPrompt).user(message).call().content();
    }

    @GetMapping("/hr/chat")
    public String hrSystemChat(@RequestParam("message") String message){
        return chatClient.prompt().system(systemHRSystemMsg).user(message).call().content();
    }
}
