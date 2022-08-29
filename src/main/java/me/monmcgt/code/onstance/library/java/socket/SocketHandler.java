package me.monmcgt.code.onstance.library.java.socket;

import com.google.gson.JsonObject;
import me.monmcgt.code.onstance.library.java.Var;
import me.monmcgt.code.onstance.library.java.object.OnstanceResponse;

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
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", "init");
                jsonObject.addProperty("uid", this.uid);
                this.objectOutputStream.writeUTF(jsonObject.toString());
                this.objectOutputStream.flush();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            while (true) {
                try {
                    String string = this.objectInputStream.readUTF();
                    this.processResponse(string);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void processResponse(Object object) {
        if (object instanceof String) {
            String string = (String) object;
            JsonObject jsonObject = Var.JSON_PARSER.parse(string).getAsJsonObject();
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
            }
        }
    }

    public void sendKeepAlive(String verifyCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "keep-alive");
        jsonObject.addProperty("verify-code", verifyCode);
        try {
            this.objectOutputStream.writeUTF(jsonObject.toString());
            this.objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
