package gt.ai_demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@SpringBootApplication
@Slf4j
public class AiDemoApplication {

    static void main(String[] args) {
        var app = new SpringApplication(AiDemoApplication.class);
        Environment env = app.run(args).getEnvironment();

        log.info("http://localhost:{}/swagger-ui/index.html", env.getProperty("server.port"));
    }

}
