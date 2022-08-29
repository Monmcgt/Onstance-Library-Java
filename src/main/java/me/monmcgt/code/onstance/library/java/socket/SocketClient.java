package me.monmcgt.code.onstance.library.java.socket;

import me.monmcgt.code.onstance.library.java.object.OnstanceResponse;

import java.net.Socket;
import java.util.function.Consumer;

public class SocketClient extends Thread {
    private final Socket socket;
    private final String uid;
    private final Consumer<OnstanceResponse> consumer;

    public SocketClient(int port, String uid, Consumer<OnstanceResponse> consumer) {
        this("localhost", uid, port, consumer);
    }

    public SocketClient(String ip, String uid, int port, Consumer<OnstanceResponse> consumer) {
        try {
            this.socket = new Socket(ip, port);
            this.uid = uid;
            this.consumer = consumer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        SocketHandler socketHandler = new SocketHandler(this.socket, this.uid, this.consumer);
        socketHandler.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.socket.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
