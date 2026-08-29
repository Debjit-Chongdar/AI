package in.ai.practice.controller;

import in.ai.practice.bean.CountryWiseCity;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/entity")
public class EntityController {

    private ChatClient chatClient;

    public EntityController(@Qualifier("contextChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public ResponseEntity<ChatResponse, CountryWiseCity> chat(@RequestParam String message){
        return this.chatClient.prompt()
                .user(message)
                .call()
                //.entity(CountryWiseCity.class); will return CountryWiseCity
                // I also want to see context
                .responseEntity(CountryWiseCity.class);
    }

    @GetMapping("/chat-list")
    public List<String> chatList(@RequestParam String message){
        return this.chatClient
                .prompt(message)
                .call().entity(new ListOutputConverter());
    }

    @GetMapping("/chat-map")
    public Map<String, Object> chatMap(@RequestParam String message){
        return this.chatClient
                //.prompt(message)
                .prompt("provide me the city details in USA?")
                .call().entity(new MapOutputConverter());
    }

    @GetMapping("/chat-objlist")
    public List<CountryWiseCity> chatCountryCityList(@RequestParam String message){
        return this.chatClient
                .prompt(message)
                .call().entity(new ParameterizedTypeReference<List<CountryWiseCity>>() {
                });
    }
}
