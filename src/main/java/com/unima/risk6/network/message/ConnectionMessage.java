package com.unima.risk6.network.message;

public class ConnectionMessage <T> extends Message{


    //Payload are things like the GameID/Name and Password
    private T payload;

    public ConnectionMessage(ConnectionActions content, T payload) {
        super(content);
        this.payload = payload;
    }

    public ConnectionMessage(ConnectionActions content, int statusCode, T payload) {
        super(content, statusCode);
        this.payload = payload;
    }
    public ConnectionMessage(ConnectionActions content) {
        super(content);
    }

    public ConnectionMessage(ConnectionActions content, int statusCode) {
        super(content, statusCode);
    }
}
