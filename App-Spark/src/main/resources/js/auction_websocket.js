class AuctionWebSocket {
    constructor() {
        this.ws = null;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectDelay = 3000;
        this.listeners = new Map();

        this.connect();
    }

    connect() {
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${protocol}//${window.location.host}/auction-updates`;

        console.log('🔌 Conectando a WebSocket:', wsUrl);

        this.ws = new WebSocket(wsUrl);

        this.ws.onopen = () => {
            console.log('WebSocket conectado');
            this.reconnectAttempts = 0;
            this.trigger('connected');
        };

        this.ws.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                console.log('📨 Mensaje recibido:', data);

                this.trigger(data.type, data);

                this.trigger('message', data);
            } catch (error) {
                console.error('Error parseando mensaje:', error);
            }
        };

        this.ws.onclose = () => {
            console.log('WebSocket desconectado');
            this.trigger('disconnected');
            this.attemptReconnect();
        };

        this.ws.onerror = (error) => {
            console.error('Error en WebSocket:', error);
            this.trigger('error', error);
        };
    }

    attemptReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`🔄 Intentando reconectar (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);

            setTimeout(() => {
                this.connect();
            }, this.reconnectDelay);
        } else {
            console.error('Máximo de intentos de reconexión alcanzado');
        }
    }

    send(data) {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(data));
        } else {
            console.warn('WebSocket no está conectado');
        }
    }

    subscribeToItem(itemId) {
        console.log('Suscribiendo a item:', itemId);
        this.send({
            action: 'subscribe',
            itemId: itemId
        });
    }


    on(event, callback) {
        if (!this.listeners.has(event)) {
            this.listeners.set(event, []);
        }
        this.listeners.get(event).push(callback);
    }

    trigger(event, data) {
        const callbacks = this.listeners.get(event);
        if (callbacks) {
            callbacks.forEach(callback => callback(data));
        }
    }

}

window.auctionWS = new AuctionWebSocket();