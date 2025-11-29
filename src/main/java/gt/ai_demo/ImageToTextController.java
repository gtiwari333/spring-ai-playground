package gt.ai_demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageToTextController {

    private final ChatClient chatClient;

    public ImageToTextController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostMapping(value = "/v1/image-to-text", consumes = "multipart/form-data")
    public String image(@RequestPart("file") MultipartFile file) {
        return chatClient.prompt()
                .user(u -> u
                        .text("Write a brief summary of what you see in the following image.")
                        .media(MimeType.valueOf(file.getContentType()), file.getResource())
                )
                .call()
                .content();
    }
}

