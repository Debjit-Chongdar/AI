package in.ai.practice.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

public class ShowContextAdvisor implements CallAdvisor {
    Logger logger = LoggerFactory.getLogger(ShowContextAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        if(chatClientRequest.context() != null){
            logger.info("Context: " + chatClientRequest.context());
        }
        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "ShowContextAdvisor";
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
