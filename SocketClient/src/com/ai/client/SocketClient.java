package com.ai.client;

import java.io.*;
import java.net.Socket;

public class SocketClient {

    public static void main(String[] args) {
        SocketClient client = new SocketClient();
        client.start();
    }

    public void start(){
        // 从控制台读入信息
        BufferedReader inputReader = null;
        // 从socket中读入信息
        BufferedReader reader = null;
        //向Socket中些信息
        BufferedWriter bufferedWriter = null;
        Socket socket = null;
        try {
            //指定本机ip地址，以及和服务器端一样的端口
            socket = new Socket("127.0.0.1",9999);

            // 从socket读取服务器的消息
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));

            // 从控制台读入用户输入字符
            inputReader = new BufferedReader(new InputStreamReader(System.in));
            // 时刻监听服务器的消息
            startServerReplyListener(reader);

            String inputContent;
            while(!(inputContent = inputReader.readLine()).equals("bye")){
                System.out.println(inputContent);
                bufferedWriter.write(inputContent+"\n");
                // 作用就是清空缓冲区并完成文件写入操作的。
                bufferedWriter.flush();

                // 读取服务器返回的数据
                //String response = reader.readLine();
                //System.out.println("服务器返回数据："+response);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputReader.close();
                reader.close();
                bufferedWriter.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // 时刻监听服务器发送的消息
    public void startServerReplyListener(final BufferedReader reader){

        new Thread(new Runnable() {
            @Override
            public void run() {
                String response;
                try {
                    while((response = reader.readLine())!=null){
                      System.out.println("时刻监听服务器发送的消息:"+response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
