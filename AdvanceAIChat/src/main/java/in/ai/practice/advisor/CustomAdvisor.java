package in.ai.practice.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;

public class CustomAdvisor implements CallAdvisor {
    // this advisor I am creating to print token usage details
    private static final Logger logger = LoggerFactory.getLogger(CustomAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        ChatResponse chatResponse = chatClientResponse.chatResponse();
        if (chatResponse.getMetadata() != null) {
            Usage usage = chatResponse.getMetadata().getUsage();
            logger.info("Token usage details : {}", usage.toString());
        }
        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "CustomAdvisor";
    }

    //I want to execute this CustomAdvisor before SimpleLoggerAdvisor
    @Override
    public int getOrder() { //order 1 will execute first then 0 ...
        return 1;
    }
}
