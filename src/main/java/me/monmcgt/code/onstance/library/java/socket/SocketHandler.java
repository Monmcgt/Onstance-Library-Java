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
                /*JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", "init");
                jsonObject.addProperty("uid", this.uid);
                this.objectOutputStream.writeUTF(jsonObject.toString());*/
                InitPacket initPacket = (InitPacket) InitPacket.builder().build().setUid(this.uid);
//                this.objectOutputStream.writeUTF(initPacket.serialize());
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
//                    throw new RuntimeException(e);
                    e.printStackTrace();
                }
            }
        }
    }

    public void processResponse(Object object) {
//            String string = (String) object;
            /*JsonObject jsonObject = Var.JSON_PARSER.parse(string).getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            if (type.equals("init")) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    this.consumer.accept(OnstanceResponse.builder().isAlive(true).build());
                } else {
                    String message = jsonObject.get("message").getAsString();
                    this.consumer.accept(OnstanceResponse.builder().isAlive(false).message(message).build());
                }
            } else if (type.equals("keep-alive")) {
                String verifyCode = jsonObject.get("verify-code").getAsString();
                this.sendKeepAlive(verifyCode);
                this.consumer.accept(OnstanceResponse.builder().isAlive(true).build());
            } else {
                throw new RuntimeException("Invalid type: " + type);
            }*/
//            Packet packet = Packet.deserialize(string);
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
        /*JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "keep-alive");
        jsonObject.addProperty("verify-code", verifyCode);
        try {
            this.objectOutputStream.writeUTF(jsonObject.toString());
            this.objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        KeepAlivePacket keepAlivePacket = (KeepAlivePacket) KeepAlivePacket.builder().verifyCode(verifyCode).build();
        try {
//            this.objectOutputStream.writeUTF(keepAlivePacket.serialize());
            this.objectOutputStream.writeObject(keepAlivePacket);
            this.objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
