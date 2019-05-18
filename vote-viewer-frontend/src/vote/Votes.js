import _ from 'lodash';

export default class Votes {
    constructor(sessionId, sessionName, speakerName) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.speakerName = speakerName;
        this.voteMap = new Map();
    }

    add(vote) {
        const satisfaction = vote.satisfaction;
        if (!this.voteMap.has(satisfaction)) {
            this.voteMap.set(satisfaction, vote);
        } else {
            const v = this.voteMap.get(satisfaction);
            v.count = v.count + vote.count;
        }
    }

    nsat() {
        return Votes._nsat(Array.from(this.voteMap.values()));
    }

    count() {
        return Votes._count(Array.from(this.voteMap.values()));
    }

    static _count(votes) {
        return _.chain(votes)
            .map(vote => vote.count)
            .sum()
            .value();
    }

    static _nsat(votes) {
        const total = Votes._count(votes);
        const vsat = Votes._extract(votes, "EXCELLENT") * 100.0 / total;
        const dsat = (Votes._extract(votes, "BAD") + Votes._extract(votes, "TERRIBLE")) * 100.0 / total;
        return Math.round(vsat - dsat) + 100;
    }

    static _extract(votes, satisfaction) {
        return _.chain(votes)
            .filter(vote => vote.satisfaction === satisfaction)
            .map(vote => vote.count)
            .sum()
            .value();
    }
}