package in.ai.practice;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;

    public ChatController(
            @Qualifier("openAiChatClient") ChatClient openAiChatClient,
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    @GetMapping("/ollama/chat")
    public String ollamaChatResponse(@RequestParam("message") String message){
        return ollamaChatClient.prompt(message).call().content();
    }

    @GetMapping("/openai/chat")
    public String opnaiChatResponse(@RequestParam("message") String message){
        return openAiChatClient.prompt(message).call().content();
    }
}
