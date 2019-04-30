import React, {Component} from 'react';
import './App.css';
import posed, {PoseGroup} from 'react-pose';
import VoteClient from './vote/VoteClient';
import Votes from './vote/Votes';

const Item = posed.li();

const sortByCount = (a, b) => {
    var count1 = a.count();
    var count2 = b.count();
    if (count1 === count2) return 0;
    return count1 < count2 ? 1 : -1;
};

const sortByNsat = (a, b) => {
    var nsat1 = a.nsat();
    var nsat2 = b.nsat();
    if (nsat1 === nsat2) return sortByCount(a, b);
    return nsat1 < nsat2 ? 1 : -1;
};

export default class App extends Component {
    state = {
        items: [],
        count: 0
    };

    componentDidMount() {
        this.voteClient = new VoteClient();
        let that = this;
        let votesMap = new Map();
        const onConect = () => this.voteClient.votes(vote => {
            let votes;
            if (votesMap.has(vote.sessionId)) {
                votes = votesMap.get(vote.sessionId);
            } else {
                votes = new Votes(vote.sessionId, vote.sessionName);
                votesMap.set(vote.sessionId, votes);
            }
            votes.add(vote);

            that.setState({
                count: that.state.count + vote.count
            });
        });
        setInterval(() => {
            let items = Array.from(votesMap.values())
                .filter(votes => votes.count() >= 2);
            items.sort(sortByNsat);
            while (items.length > 7) {
                items.pop();
            }
            that.setState({
                items: items,
            });
        }, 2000);
        this.voteClient.connect(onConect);
    }

    render() {
        return (
            <div className="App">
                <header className="App-header">
                    <p>
                        満足度ランキング<br/>
                        投票数: {this.state.count}
                    </p>
                    <ul>
                        <PoseGroup>{this.state.items
                            .map(votes =>
                                <Item key={votes.sessionId}>
                                    <strong>{votes.sessionName}</strong>: {votes.nsat()} (投票数: {votes.count()})
                                </Item>)}
                        </PoseGroup>
                    </ul>
                </header>
            </div>
        );
    }
}
