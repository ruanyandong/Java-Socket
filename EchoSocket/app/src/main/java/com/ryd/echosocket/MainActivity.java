package com.ryd.echosocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * # Socket 基本用法
 * Socket 是 TCP 层的封装，通过 socket，我们就能进行 TCP 通信。
 * 在 Java 的 SDK 中，socket 的共有两个接口：用于监听客户连接的 ServerSocket 和用于通信的 Socket。使用 socket 的步骤如下：
 *
 * 创建 ServerSocket 并监听客户连接
 * 使用 Socket 连接服务端
 * 通过 Socket 获取输入输出流进行通信
 *
 * 我们通过实现一个简单的 echo 服务来学习 socket 的使用。所谓的 echo 服务，就是客户端向服务端写入任意数据，服务器都将数据原封不动地写回给客户端。
 *
 * https://juejin.cn/post/6844903630047281159#heading-5
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}