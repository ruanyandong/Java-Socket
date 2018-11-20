package com.ai.mima;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

// Mina将网络处理和消息处理分离
public class MinaServer {

	public static void main(String[] args) {
       try {
		    // 1、第一步
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			// 2、第二步,设置handler，把消息交给handler处理
			acceptor.setHandler(new MyServerHandler());
			// 3、拦截器过滤后才能收到消息或者发送消息，getFilterChain可以获得所有拦截器,factory
			//         是对的数据进行加解码，可以自己定义，也可用系统的new TextLineCodecFactory()
			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MyTextLineFactory()));;
			// 5秒钟客户端和服务器没有进行读写，就进入空闲状态，每个5秒就会调用方法
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,5);
			// 4、第四步,添加端口号，启动服务器
			acceptor.bind(new InetSocketAddress(9999));
			
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		
	}

}
