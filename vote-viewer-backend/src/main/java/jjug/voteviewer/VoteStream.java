package jjug.voteviewer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

public enum VoteStream {
    INSTANCE;

    private final UnicastProcessor<VoteBuilder> processor;

    private final Flux<VoteBuilder> stream;

    VoteStream() {
        this.processor = UnicastProcessor.create();
        this.stream = this.processor.publish()
            .autoConnect()
            .share();
    }

    public void next(Vote vote) {
        this.processor.onNext(new VoteBuilder(vote));
    }

    public Flux<VoteBuilder> flux() {
        return this.stream;
    }
}
