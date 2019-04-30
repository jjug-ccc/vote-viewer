package jjug.voteviewer;

public class VoteBuilder {

    private int count;

    private String satisfaction;

    private String sessionId;

    private String sessionName;

    public Vote createVote() {
        return new Vote(sessionId, sessionName, satisfaction, count);
    }

    public VoteBuilder withCount(int count) {
        this.count = count;
        return this;
    }

    public VoteBuilder withSatisfaction(String satisfaction) {
        this.satisfaction = satisfaction;
        return this;
    }

    public VoteBuilder withSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public VoteBuilder withSessionName(String sessionName) {
        this.sessionName = sessionName;
        return this;
    }
}