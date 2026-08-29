package in.ai.practice.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/options")
public class OptionsChatController {

    private final ChatClient optionsChatClient;

    public OptionsChatController(@Qualifier("optionsChatClient") ChatClient optionsChatClient) {
        this.optionsChatClient = optionsChatClient;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return optionsChatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }
}
