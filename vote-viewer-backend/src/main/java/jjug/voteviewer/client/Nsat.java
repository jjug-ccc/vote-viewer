package jjug.voteviewer.client;

import jjug.voteviewer.Vote;

import java.util.Collection;

/**
 * Returns "Net User Satisfaction". <br>
 * http://download.microsoft.com/download/3/2/2/32269687-F181-449A-8C72-317090403C70/Determining_Net_User_Satisfaction.docx
 */
class Nsat {

    public static long value(Collection<Vote> votes) {
        int total = votes.stream().mapToInt(Vote::getCount).sum();
        double vsat = extract(votes, "EXCELLENT") * 100.0d
            / total;
        double dsat = (extract(votes, "BAD")
            + extract(votes, "TERRIBLE")) * 100.0d
            / total;
        return Math.round(vsat - dsat) + 100;
    }

    private static long extract(Collection<Vote> votes,
                                String satisfaction) {
        return votes.stream()
            .filter(v -> satisfaction.equals(v.getSatisfaction()))
            .mapToLong(Vote::getCount)
            .sum();
    }
}
