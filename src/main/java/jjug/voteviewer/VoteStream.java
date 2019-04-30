package jjug.voteviewer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

public enum VoteStream {
    INSTANCE;

    private final UnicastProcessor<Vote> processor;

    private final Flux<Vote> stream;

    VoteStream() {
        this.processor = UnicastProcessor.create();
        this.stream = this.processor.publish()
            .autoConnect()
            .share();
    }

    public void next(Vote vote) {
        this.processor.onNext(vote);
    }

    public Flux<Vote> flux() {
        return this.stream;
    }
}
