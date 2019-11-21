package jjug.voteviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(VoteProps.class)
public class VoteViewerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoteViewerApplication.class, args);
    }

}
