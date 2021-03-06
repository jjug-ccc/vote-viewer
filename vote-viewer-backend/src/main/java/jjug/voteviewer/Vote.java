package jjug.voteviewer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;

public class Vote implements Serializable {

    private final String sessionId;

    private final String sessionName;

    private final String satisfaction;

    private final String speakerName;

    private final int count;


    @JsonCreator
    public Vote(@JsonProperty("sessionId") String sessionId,
                @JsonProperty("sessionName") String sessionName,
                @JsonProperty("satisfaction") String satisfaction,
                @JsonProperty("speakerName") String speakerName,
                @JsonProperty("count") int count) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.satisfaction = satisfaction;
        this.speakerName = speakerName;
        this.count = count;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getSatisfaction() {
        return satisfaction;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("sessionId", sessionId)
            .append("sessionName", sessionName)
            .append("satisfaction", satisfaction)
            .append("speakerName", speakerName)
            .append("count", count)
            .toString();
    }

    public Vote merge(Vote vote) {
        Assert.isTrue(Objects.equals(this.sessionId, vote.sessionId) && Objects.equals(this.satisfaction, vote.satisfaction), "Can't merge a different vote");
        return new Vote(this.sessionId, this.sessionName, this.satisfaction, this.speakerName, this.count + vote.count);
    }
}
