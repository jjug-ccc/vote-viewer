import {RSocketClient} from 'rsocket-core';
import RSocketWebSocketClient from "rsocket-websocket-client";

export default class VoteClient {
    constructor() {
        var href = window.location.href;
        this.client = new RSocketClient({
            setup: {
                keepAlive: 60000,
                lifetime: 180000,
                dataMimeType: 'application/json',
                metadataMimeType: 'message/x.rsocket.routing.v0'
            },
            transport: new RSocketWebSocketClient({
                url: href.indexOf('.io') > 0 ? (href.substring(0, href.indexOf('.io') + 3)).replace("https", "wss") + '/rsocket' : 'ws://localhost:7777/rsocket'
            }),
        });
    }

    connect(onComplete) {
        let that = this;
        this.client.connect()
            .subscribe({
                onComplete: socket => {
                    this.socket = socket;
                    onComplete();
                },
                onError: error => {
                    console.error(error);
                    alert('onError! => ' + error.message);
                    window.location.reload();
                },
                onSubscribe: cancel => {/* call cancel() to abort */
                }
            });
    }

    votes(onNext) {
        let maxInFlight = 32;
        let current = maxInFlight;
        let subscription;
        let that = this;
        this.socket && this.socket.requestStream({
            data: JSON.stringify({}),
            metadata: this.routingMetadata('votes')
        }).subscribe({
            onSubscribe: sub => {
                subscription = sub;
                console.log('request', maxInFlight);
                subscription.request(maxInFlight);
            },
            onNext: (payload) => {
                let body = JSON.parse(payload.data);
                onNext(body);
                current--;
                if (current === 0) {
                    current = maxInFlight;
                    console.log('request', maxInFlight);
                    subscription.request(maxInFlight);
                }
            },
            onError: error => {
                console.error('onError', error);
                alert('onError! => ' + error.message);
                window.location.reload();
            }
        });
    }

    routingMetadata(route) {
        return String.fromCharCode(route.length) + route;
    }
}