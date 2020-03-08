package com.ai.sockettcp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author AI
 * 客户端
 */
public class ClientActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MESSAGE_RECEIVE_NEW_MSG = 1;
    private static final int MESSAGE_SOCKET_CONNECTION = 2;

    TextView chatTv;
    EditText inputEt;
    Button sendBtn;

    private PrintWriter printWriter;
    private Socket clientSocket;

    @SuppressWarnings("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MESSAGE_RECEIVE_NEW_MSG:
                    chatTv.setText(chatTv.getText()+"\n"+(String)msg.obj+"\n");
                    break;
                case MESSAGE_SOCKET_CONNECTION:
                    sendBtn.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatTv = findViewById(R.id.tv);
        inputEt = findViewById(R.id.et);
        sendBtn = findViewById(R.id.btn);
        sendBtn.setOnClickListener(this);
        sendBtn.setEnabled(false);

        // 启动服务端
        Intent intent = new Intent(this,TCPServerService.class);
        startService(intent);

        // 连接服务端
        new Thread(){
            @Override
            public void run() {
                connectionTCPServer();
            }
        }.start();
    }

    private void connectionTCPServer() {
        Socket socket = null;
        //  连接服务器
        while (socket == null){
            try {
                socket = new Socket("localhost",8688);
                clientSocket = socket;
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                handler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTION);
                System.out.println("connection server success");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("connect tcp server failed,retry...");
            }
        }

        // 接收服务器消息
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(!ClientActivity.this.isFinishing()){
                String msg = br.readLine();
                System.out.println("receive->"+msg);
                if (msg != null){
                    handler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG,msg).sendToTarget();
                }
            }

            printWriter.close();
            br.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        if (v == sendBtn){
            final String msg = inputEt.getText().toString();
            if (!TextUtils.isEmpty(msg) && printWriter != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        printWriter.println(msg);
                    }
                }).start();
                inputEt.setText("");
                chatTv.setText(chatTv.getText()+"\n"+msg);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (clientSocket != null){
            try {
                clientSocket.shutdownInput();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

}
