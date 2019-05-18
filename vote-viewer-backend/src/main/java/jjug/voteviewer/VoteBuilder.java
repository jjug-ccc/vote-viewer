package jjug.voteviewer;

public class VoteBuilder {

    int count;

    String satisfaction;

    String sessionId;

    String sessionName;

    String speakerName;

    public VoteBuilder() {
    }

    public VoteBuilder(Vote vote) {
        this.sessionId = vote.getSessionId();
        this.sessionName = vote.getSessionName();
        this.satisfaction = vote.getSatisfaction();
        this.count = vote.getCount();
    }

    public Vote createVote() {
        return new Vote(sessionId, sessionName, satisfaction, speakerName, count);
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

    public VoteBuilder withSpeakerName(String speakerName) {
        this.speakerName = speakerName;
        return this;
    }
}