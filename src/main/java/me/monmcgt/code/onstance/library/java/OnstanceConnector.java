package me.monmcgt.code.onstance.library.java;

import me.monmcgt.code.onstance.library.java.object.OnstanceResponse;
import me.monmcgt.code.onstance.library.java.socket.SocketClient;

import java.util.function.Consumer;

public class OnstanceConnector {
    private final int port;
    private final String uid;
    private final Consumer<OnstanceResponse> consumer;

    public OnstanceConnector(String uid, Consumer<OnstanceResponse> consumer) {
        this(56780, uid, consumer);
    }

    public OnstanceConnector(int port, String uid, Consumer<OnstanceResponse> consumer) {
        this.port = port;
        this.uid = uid;
        this.consumer = consumer;
    }

    public void start() {
        SocketClient socketClient = new SocketClient(this.port, this.uid, this.consumer);
        socketClient.start();
    }
}
