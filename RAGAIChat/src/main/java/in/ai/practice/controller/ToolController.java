package in.ai.practice.controller;

import in.ai.practice.tool.TimeTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tool")
public class ToolController {

    private ChatClient chatClient;
    private TimeTool timeTool;
    public ToolController(
            @Qualifier("simpleChatClient") ChatClient chatClient,
            TimeTool timeTool) {
        this.chatClient = chatClient;
        this.timeTool = timeTool;
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam String userMessage) {
        String response = chatClient.prompt().user(userMessage).tools(timeTool).call().content();
        return ResponseEntity.ok(response);
    }
}
