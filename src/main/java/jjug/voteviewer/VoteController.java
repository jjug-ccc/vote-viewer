package jjug.voteviewer;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class VoteController {

    private final WebClient webClient;

    public VoteController(WebClient.Builder builder) {
        this.webClient = builder
            .baseUrl("http://localhost:8080")
            .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth("demo", "demo"))
            .build();
    }

    @MessageMapping("votes")
    public Flux<Vote> votes() {
        String seminarId = "b8b058ac-a99b-4892-96f8-f4de3924511e";
        Flux<Vote> currentVotes = this.webClient.get()
            .uri("/v1/seminars/{seminarId}/votes", seminarId)
            .retrieve()
            .bodyToFlux(JsonNode.class)
            .flatMapIterable(this::toVotes);
        return currentVotes.concatWith(VoteStream.INSTANCE.flux());
    }

    List<Vote> toVotes(JsonNode n) {
        String sessionId = n.get("session").get("sessionId").asText();
        String sessionName = n.get("session").get("sessionName").asText();
        return StreamSupport.stream(n.get("summary").get("details").spliterator(), false)
            .map(d -> new VoteBuilder()
                .withSessionId(sessionId)
                .withSessionName(sessionName)
                .withCount(d.get("count").asInt())
                .withSatisfaction(d.get("value").asText())
                .createVote())
            .collect(Collectors.toList());
    }
}
