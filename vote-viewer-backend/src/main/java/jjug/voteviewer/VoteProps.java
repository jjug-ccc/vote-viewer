package jjug.voteviewer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties
@ConstructorBinding
public class VoteProps {

    private final String voteUrl;

    private final String seminarId;

    private final String conferenceId;

    private final String clientId;

    private final String clientSecret;

    public VoteProps(String voteUrl, String seminarId, String conferenceId, String clientId, String clientSecret) {
        this.voteUrl = voteUrl;
        this.seminarId = seminarId;
        this.conferenceId = conferenceId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getVoteUrl() {
        return voteUrl;
    }

    public String getSeminarId() {
        return seminarId;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
