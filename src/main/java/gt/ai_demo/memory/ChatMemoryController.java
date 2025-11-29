package gt.ai_demo.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ChatMemoryController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatMemoryController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @GetMapping("/end-chat")
    public void endChat(@RequestParam String conversationId) {
        log.info("Clearing conversation Id: {}", conversationId);
        chatMemory.clear(conversationId);
    }

    @GetMapping("/chat-with-conv-id")
    public String memory(@RequestParam String message, @RequestParam String conversationId) {
        log.info("Past conversations: {}, {}", conversationId, chatMemory.get(conversationId));

        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
