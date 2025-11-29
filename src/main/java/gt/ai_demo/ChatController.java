package gt.ai_demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder
                .build();
    }

    @PostMapping("/")
    public String chat(@RequestBody String message) {
        return chatClient.prompt(message)
                .call().content();
    }

    @PostMapping("/math/{operation}/{a}/{b}")
    public MathResponse math(@PathVariable Operation operation, @PathVariable BigDecimal a, @PathVariable BigDecimal b) {
        return chatClient.prompt("Do the math " + operation + " between " + a + " and " + b)
                .call().entity(MathResponse.class);
    }


    enum Operation {
        ADD, SUB, MUL, DIV
    }

    record MathResponse(Operation operation, BigDecimal a, BigDecimal b, BigDecimal result) {
    }
}
