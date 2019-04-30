package jjug.voteviewer.client;

import jjug.voteviewer.Vote;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Votes {

    private final String sessionId;

    private final String sessionName;

    private final Map<String, Vote> voteMap = new ConcurrentHashMap<>();

    public Votes(String sessionId, String sessionName, Vote... votes) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        if (votes != null) {
            for (Vote vote : votes) {
                this.add(vote);
            }
        }
    }

    public Votes add(Vote vote) {
        Assert.isTrue(Objects.equals(this.sessionId, vote.getSessionId()), "Can't add a different vote");
        String satisfaction = vote.getSatisfaction();
        Vote v = this.voteMap.putIfAbsent(satisfaction, vote);
        if (v != null) {
            this.voteMap.put(satisfaction, v.merge(vote));
        }
        return this;
    }

    public long nsat() {
        return Nsat.value(this.voteMap.values());
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("sessionId", this.sessionId)
            .append("sessionName", this.sessionName)
            .append("nsat", this.nsat())
            .toString();
    }
}
