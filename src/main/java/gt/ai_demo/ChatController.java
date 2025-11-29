package gt.ai_demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/chat")
public class ChatController {

    private final ChatClient chatClient;
    private final MathTools mathTools;

    public ChatController(ChatClient.Builder builder, MathTools mathTools) {
        this.chatClient = builder
                .build();
        this.mathTools = mathTools;
    }

    @PostMapping("/")
    public String chat(@RequestBody String message) {
        // responds to anything
        return chatClient.prompt(message)
                .call().content();
    }

    @PostMapping("/with-guards")
    public String chatWithGuards(@RequestBody String message) {
        return chatClient.prompt()
                .system("You are a calculator that performs " + Arrays.stream(Operation.values()).collect(Collectors.toSet()) + " operations. " +
                        "If user asks about anything other than these math operation, politely refuse. If user is trying to divide by 0, simply return response as 'NOT POSSIBLE'. If user is trying to divide 0 by a number, return 0 as reponse.")
                .user(message)
                .call().content();
    }

    @PostMapping("/math/{operation}/{a}/{b}")
    public String math(@PathVariable Operation operation, @PathVariable BigDecimal a, @PathVariable BigDecimal b) {
        return chatClient.prompt("Do the math " + operation + " between " + a + " and " + b)
                .call().content();
    }


    @PostMapping("/math-with-params/{operation}/{a}/{b}")
    public String mathParam(@PathVariable Operation operation, @PathVariable BigDecimal a, @PathVariable BigDecimal b) {
        return chatClient.prompt().user(u -> {
                    u.text("Do the math {operation} between {a} and {b}");
                    u.param("operation", operation);
                    u.param("a", a);
                    u.param("b", b);
                })
                .call().content();
    }

    @PostMapping("/math-structured-output/{operation}/{a}/{b}")
    public MathResponse mathStructured(@PathVariable Operation operation, @PathVariable BigDecimal a, @PathVariable BigDecimal b) {
        return chatClient.prompt().user(u -> {
                    u.text("Do the math {operation} between {a} and {b}");
                    u.param("operation", operation);
                    u.param("a", a);
                    u.param("b", b);
                })
                .call().entity(MathResponse.class);
    }

    @PostMapping("/math-tool-calling/{mathQuestion}")
    public MathResponse mathStructuredToolCalling(@PathVariable String mathQuestion) {
        return chatClient.prompt()
                .system("You are a calculator that performs " + Arrays.stream(Operation.values()).collect(Collectors.toSet()) + " operations. " +
                        "The user must provide an 'operation', and two values 'a' and 'b' to operate the math on." +
                        "If the user did not provide any of the three parameters, simply refuse to do any further calculation." +
                        "If user asks about anything other than these math operation, politely refuse. ")
                .tools(mathTools)
                .user(mathQuestion)
                .call().entity(MathResponse.class);
    }

}

enum Operation {
    ADD, SUB, MUL, DIV
}

record MathResponse(Operation operation, BigDecimal a, BigDecimal b, BigDecimal result) {
}

@Service
@Slf4j
class MathTools {

    @Tool(description = "Performs math operation between two parameters a and b")
    public MathResponse performOperation(@RequestParam Operation operation, @RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        log.info("Doing math locally {} between {} and {}", operation, a, b);
        return new MathResponse(operation, a, b, switch (operation) {
            case ADD -> a.add(b);
            case SUB -> a.subtract(b);
            case MUL -> a.multiply(b);
            case DIV -> a.divide(b, 6, RoundingMode.HALF_UP);
        });
    }
}