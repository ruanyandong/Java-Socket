package com.ryd.echokeepalive;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

/**
 * 实现Socket长链接
 * https://juejin.cn/post/6844903630047281159#heading-5
 */
public class MainActivity extends AppCompatActivity {

    private EchoServer mEchoServer;
    private EchoClient mEchoClient;

    private EditText mMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int port = 9877;
        mEchoServer = new EchoServer(port);
        mEchoServer.run();
        mEchoClient = new EchoClient("localhost", port);

        mMsg = findViewById(R.id.edit_text);
        findViewById(R.id.send).setOnClickListener((view) -> {
            String msg = mMsg.getText().toString();
            if (TextUtils.isEmpty(msg)) {
                return;
            }
            mEchoClient.send(msg);
            mMsg.setText("");
        });
    }
}