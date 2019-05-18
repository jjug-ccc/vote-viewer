package jjug.voteviewer;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class VoteController {

    private final WebClient webClient;

    private final VoteProps props;

    private final Map<String, String> speakers;

    public VoteController(WebClient.Builder builder, VoteProps props) {
        this.webClient = builder
            .baseUrl(props.getVoteUrl())
            .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth(props.getClientId(), props.getClientSecret()))
            .build();
        this.speakers = this.webClient
            .get()
            .uri("https://jjug-cfp.cfapps.io/v1/conferences/{seminarId}/submissions", props.getConferenceId())
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(n -> StreamSupport.stream(n.get("_embedded").get("submissions").spliterator(), false)
                .collect(Collectors.toMap(x -> x.get("title").asText(),
                    x -> StreamSupport.stream(x.get("speakers").spliterator(), false)
                        .map(s -> s.get("name").asText())
                        .collect(Collectors.joining(", ")))))
            .log("submission")
            .block();
        ;
        this.props = props;
    }

    @MessageMapping("votes")
    public Flux<Vote> votes() {
        Flux<VoteBuilder> currentVotes = this.webClient.get()
            .uri("/v1/seminars/{seminarId}/votes", this.props.getSeminarId())
            .retrieve()
            .bodyToFlux(JsonNode.class)
            .flatMapIterable(VoteController::toVotes);
        return currentVotes.concatWith(VoteStream.INSTANCE.flux())
            .map(builder -> builder.withSpeakerName(this.speakers.get(builder.sessionName)).createVote()).log("vote");
    }

    public static List<VoteBuilder> toVotes(JsonNode n) {
        String sessionId = n.get("session").get("sessionId").asText();
        String sessionName = n.get("session").get("sessionName").asText();
        return StreamSupport.stream(n.get("summary").get("details").spliterator(), false)
            .map(d -> new VoteBuilder()
                .withSessionId(sessionId)
                .withSessionName(sessionName)
                .withCount(d.get("count").asInt())
                .withSatisfaction(d.get("value").asText()))
            .collect(Collectors.toList());
    }
}
