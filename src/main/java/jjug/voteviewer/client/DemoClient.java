package jjug.voteviewer.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import jjug.voteviewer.Vote;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class DemoClient {

    static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        int port = Optional.ofNullable(System.getenv("PORT")).map(Integer::parseInt)
            .orElse(7777);
        CountDownLatch latch = new CountDownLatch(1);
        RSocket rsocket = rsocket(port);

        Map<String, Votes> votesMap = new ConcurrentHashMap<>();

        Flux<Vote> vote = rsocket.requestStream(DefaultPayload.create("", "votes"))
            .map(Payload::getDataUtf8)
            .map(DemoClient::toVote)
            .log("votes")
            .doOnNext(v -> {
                Votes votes = votesMap.computeIfAbsent(v.getSessionId(), k -> new Votes(v.getSessionId(), v.getSessionName()));
                votes.add(v);
            })
            .window(Duration.ofSeconds(5))
            .doOnNext(x -> {
                Iterator<Votes> iterator = votesMap.values().iterator();
                System.out.println("=================================");
                if (iterator.hasNext()) {
                    System.out.println(iterator.next());
                }
                if (iterator.hasNext()) {
                    System.out.println(iterator.next());
                }
                if (iterator.hasNext()) {
                    System.out.println(iterator.next());
                }
                System.out.println("=================================");
            })
            .flatMap(Function.identity())
            .doOnTerminate(latch::countDown);

        vote.subscribe();
        Runtime.getRuntime().addShutdownHook(new Thread(latch::countDown));
        latch.await();
        rsocket.dispose();
    }

    static RSocket rsocket(int port) {
        ClientTransport transport = WebsocketClientTransport.create(HttpClient.from(TcpClient.create()
            .host("localhost")
            .port(port).wiretap(false)), "/rsocket");
        RSocket rsocket = RSocketFactory.connect()
            .dataMimeType("application/json")
            .metadataMimeType("text/plain")
            .transport(transport)
            .start()
            .block();
        return rsocket;
    }

    static Vote toVote(String s) {
        try {
            return objectMapper.readValue(s, Vote.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
