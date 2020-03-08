package com.ai.sockettcp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * @author AI
 * 服务端
 */
public class TCPServerService extends Service {

    private boolean mIsServiceDestroyed = false;
    private String[] mChatMessages = new String[]{
            "Welcome to chat room!",
            "How are you?",
            "How do you do?"
    };

    @Override
    public void onCreate() {
        new Thread(new TcpServer()).start();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed = true;
        super.onDestroy();
    }

    private class TcpServer implements Runnable {
        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                // 监听本地8688端口
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                System.err.println("establish tcp server failed,port:8688");
                e.printStackTrace();
            }

            while (!mIsServiceDestroyed) {
                // 接受客户端请求放在一个线程中
                try {
                    if (serverSocket != null) {
                        final Socket client = serverSocket.accept();
                        System.out.println("连接到客户端");
                        // 响应客户端请求单独开线程，这样可以同时响应很多客户端请求
                        new Thread() {
                            @Override
                            public void run() {
                                if (client != null) {
                                    try {
                                        responseClient(client);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 响应客户端请求
     */
    private void responseClient(Socket socket) throws IOException {
        // 接收客户端消息
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // 用于向客户端发送消息
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        pw.println("欢迎来到聊天室");

        while (!mIsServiceDestroyed) {
            String msgClient = br.readLine();
            System.out.println("来自客户端的消息——>" + msgClient);
            if (msgClient == null) {
                // 客户端断开连接
                break;
            }
            int i = new Random().nextInt(mChatMessages.length);
            String msg = mChatMessages[i];
            pw.println(msg);
            System.out.println("send:"+msg);
        }
        pw.close();
        br.close();
        socket.close();
    }

}
