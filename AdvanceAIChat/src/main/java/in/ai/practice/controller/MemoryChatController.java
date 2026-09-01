package in.ai.practice.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

//it will help to keep memory of old chat, but it will use token to send those chat history
@RestController
@RequestMapping("/memory")
public class MemoryChatController {

    private final ChatClient inMemoryChatClient;
    private final ChatClient dbStoreChatClient;

    public MemoryChatController(
            @Qualifier("inMemoryChatClient") ChatClient inMemoryChatClient,
            @Qualifier("dbStoreChatClient")  ChatClient dbStoreChatClient
    ) {
        this.inMemoryChatClient = inMemoryChatClient;
        this.dbStoreChatClient = dbStoreChatClient;
    }

    @GetMapping("/chat")
    public String chat(
            @RequestParam String message,
            @RequestParam(required = false,defaultValue = "default") String conversationId
    ) {
        return this.inMemoryChatClient
                .prompt()
                //Default ChatMemory work on conversation id
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, conversationId))
                .user(message).call().content();
    }

    @GetMapping("/db/chat")
    public String dbStorechat(
            @RequestParam String message,
            @RequestParam(required = false,defaultValue = "default") String conversationId
    ) {
        return this.dbStoreChatClient
                .prompt()
                //Default ChatMemory work on conversation id
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, conversationId))
                .user(message).call().content();
    }
}
