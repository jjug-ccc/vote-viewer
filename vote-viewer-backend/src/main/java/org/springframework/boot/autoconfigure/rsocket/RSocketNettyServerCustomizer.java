/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.rsocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.server.WebsocketRouteTransport;
import jjug.voteviewer.Vote;
import jjug.voteviewer.VoteStream;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.rsocket.MessageHandlerAcceptor;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.server.HttpServer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * {@link NettyServerCustomizer} that configures an RSocket Websocket endpoint.
 *
 * @author Brian Clozel
 */
class RSocketNettyServerCustomizer implements NettyServerCustomizer {

    private final String mappingPath;

    private final MessageHandlerAcceptor messageHandlerAcceptor;

    RSocketNettyServerCustomizer(String mappingPath,
                                 MessageHandlerAcceptor messageHandlerAcceptor) {
        this.mappingPath = mappingPath;
        this.messageHandlerAcceptor = messageHandlerAcceptor;
    }

    private Mono<String> readFile(String path) {
        return Mono.fromCallable(() -> StreamUtils.copyToString(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8))
            .subscribeOn(Schedulers.elastic());
    }

    @Override
    public HttpServer apply(HttpServer httpServer) {
        ServerTransport.ConnectionAcceptor acceptor = RSocketFactory.receive()
            .acceptor(this.messageHandlerAcceptor).toConnectionAcceptor();
        // Hack
        ObjectMapper objectMapper = new ObjectMapper();
        return httpServer.route((routes) -> routes
            .get("/", (req, res) -> res.sendString(readFile("META-INF/resources/index.html")))
            .get("/static/{x}/{y}", (req, res) -> res.sendString(readFile("META-INF/resources/static/" + req.param("x") + "/" + req.param("y"))))
            .get("/manifest.json", (req, res) -> res.sendString(readFile("META-INF/resources/manifest.json")))
            .post("/webhook", (req, res) -> req.receive()
                .asByteArray()
                .map(x -> {
                    try {
                        return objectMapper.readValue(x, Vote.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .doOnNext(VoteStream.INSTANCE::next)
                .then())
            .ws(this.mappingPath,
                WebsocketRouteTransport.newHandler(acceptor)));
    }

}