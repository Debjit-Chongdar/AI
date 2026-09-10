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
@RequestMapping("/rag/advisor")
public class RagAdvisorChatController {

    private final ChatClient advisorChatClient;

    public RagAdvisorChatController(@Qualifier("advisorChatClient") ChatClient advisorChatClient) {
        this.advisorChatClient = advisorChatClient;
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat(
            @RequestParam String message,
            @RequestParam(required = false,defaultValue = "default") String conversationId
    ) {
        String output =  advisorChatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, conversationId))
                .user(message)
                .call()
                .content();
        return ResponseEntity.ok(output);
    }
}
