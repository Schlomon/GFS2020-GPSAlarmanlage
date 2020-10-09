package com.example.smsreciever;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Runnable networkThread;

    private Socket socket;

    private PrintWriter printWriter;

    private String serverURL;
    private int port;

    private String data;

    Client(final String serverURL, final int port) {

        this.serverURL = serverURL;
        this.port = port;

        networkThread = new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(serverURL, port);
                    printWriter = new PrintWriter(socket.getOutputStream(), true);
                    printWriter.print(data);
                    printWriter.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };


    }

    void sendData(String data) {
        this.data = data;
        new Thread(networkThread).start();
    }
}
