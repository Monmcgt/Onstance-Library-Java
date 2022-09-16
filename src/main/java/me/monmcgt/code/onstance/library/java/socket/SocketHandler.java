package me.monmcgt.code.onstance.library.java.socket;

import me.monmcgt.code.onstance.library.java.object.OnstanceResponse;
import me.monmcgt.code.onstance.packet.Packet;
import me.monmcgt.code.onstance.packet.impl.onstance.InitPacket;
import me.monmcgt.code.onstance.packet.impl.onstance.KeepAlivePacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class SocketHandler extends Thread {
    private final Socket socket;
    private final String uid;
    private final Consumer<OnstanceResponse> consumer;

    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;

    public SocketHandler(Socket socket, String uid, Consumer<OnstanceResponse> consumer) {
        this.socket = socket;
        this.uid = uid;
        this.consumer = consumer;

        try {
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        if (this.socket.isConnected()) {
            try {
                this.consumer.accept(OnstanceResponse.builder().isAlive(true).build());
                InitPacket initPacket = (InitPacket) InitPacket.builder().build().setUid(this.uid);
                this.objectOutputStream.writeObject(initPacket);
                this.objectOutputStream.flush();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            while (true) {
                try {
                    Object object = this.objectInputStream.readObject();
                    this.processResponse(object);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void processResponse(Object object) {
        Packet packet = (Packet) object;
        if (packet instanceof InitPacket) {
            InitPacket initPacket = (InitPacket) packet;
            if (initPacket.isSuccess()) {
                this.consumer.accept(OnstanceResponse.builder().isAlive(true).build());
            } else {
                this.consumer.accept(OnstanceResponse.builder().isAlive(false).message(initPacket.getMessage()).build());
            }
        } else if (packet instanceof KeepAlivePacket) {
            KeepAlivePacket keepAlivePacket = (KeepAlivePacket) packet;
            this.sendKeepAlive(keepAlivePacket.getVerifyCode());
            this.consumer.accept(OnstanceResponse.builder().isAlive(true).build());
        } else {
            throw new RuntimeException("Invalid type: " + packet.getType());
        }
    }


    public void sendKeepAlive(String verifyCode) {
        KeepAlivePacket keepAlivePacket = KeepAlivePacket.builder().verifyCode(verifyCode).build();
        try {
            this.objectOutputStream.writeObject(keepAlivePacket);
            this.objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
