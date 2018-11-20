package com.ai.minaclient;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

public class MinaClient {

    public static void main(String[] args) throws IOException {
        NioSocketConnector connector = new NioSocketConnector();
        connector.setHandler(new MyClientHandler());
        connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TextLineCodecFactory()));
        // 调用connect方法，并不会阻塞住，而是会继续往下运行
        ConnectFuture future = connector.connect(new InetSocketAddress("127.0.0.1",9999));
        // mina框架是非阻塞，但是如果要等待客户端建立连接，
        // 就可以调用这个方法，这个方法以后就是已经建立连接
        future.awaitUninterruptibly();
        IoSession session = future.getSession();
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        String inputContent;
        while(!(inputContent = inputReader.readLine()).equals("bye")){
            session.write(inputContent);
        }
    }

}
