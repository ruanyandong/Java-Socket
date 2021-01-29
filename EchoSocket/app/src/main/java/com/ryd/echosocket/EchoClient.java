package com.ryd.echosocket;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
// 控制台读取
public class EchoClient {

    private final Socket mSocket;


    public EchoClient(String host,int port) throws IOException {
        this.mSocket = new Socket(host,port);
    }

    public void run() throws IOException {
        Thread readerThread = new Thread(this::readResponse);
        readerThread.start();

        OutputStream out = mSocket.getOutputStream();
        byte[] buffer = new byte[1024];
        int n;
        while ((n = System.in.read(buffer)) > 0) {
            out.write(buffer, 0, n);
        }
    }

    private void readResponse() {
        try {
            InputStream in = mSocket.getInputStream();
            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) > 0) {
                System.out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
