package in.ai.practice.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Controller
@RequestMapping("/advance")
public class PrePostRaGController {

    private final ChatClient prePostChatClient;

    public PrePostRaGController(@Qualifier("prePostRAGChatClient") ChatClient prePostChatClient) {
        this.prePostChatClient = prePostChatClient;
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat(
            @RequestParam String message,
            @RequestParam(defaultValue = "default") String conversationId) {
        String output = prePostChatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, conversationId))
                .user(message).call().content();
        return ResponseEntity.ok(output);
    }
}
