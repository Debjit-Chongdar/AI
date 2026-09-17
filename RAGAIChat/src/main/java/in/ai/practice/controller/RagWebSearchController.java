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
@RequestMapping("/web")
public class RagWebSearchController {

    private final ChatClient webSearchAdvisorChatClient;
    public RagWebSearchController(@Qualifier("webSearchRagChatClient") ChatClient webSearchAdvisorChatClient) {
        this.webSearchAdvisorChatClient = webSearchAdvisorChatClient;
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat(
            @RequestParam(defaultValue = "default") String conversationId,
            @RequestParam String message) {
        String response = webSearchAdvisorChatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, conversationId))
                .user(message).call().content();
        return ResponseEntity.ok(response);
    }
}
