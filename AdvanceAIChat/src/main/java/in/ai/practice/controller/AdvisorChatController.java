package in.ai.practice.controller;

import in.ai.practice.advisor.CustomAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/advisor")
public class AdvisorChatController {

    private final ChatClient chatClient;
    private final ChatClient advisorChatClient;

    public AdvisorChatController(@Qualifier("defaultAdvisorChatClient") ChatClient advisorChatClient, ChatClient chatClient) {
        this.advisorChatClient = advisorChatClient;
        this.chatClient = chatClient;
    }

    @GetMapping("chat")
    public String chatOutput(@RequestParam String message){
        return advisorChatClient.prompt().user(message).call().content();
    }

    @GetMapping("chat-custom-advisor")
    public String chatOutput1(@RequestParam String message){
        return chatClient.prompt().advisors(List.of(new CustomAdvisor())).user(message).call().content();
    }
}
