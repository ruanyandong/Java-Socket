package com.ai.mima;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

// 处理消息收发的handler
public class MyServerHandler extends IoHandlerAdapter {

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

		System.out.println("exceptionCaught: "+cause.getMessage());
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		// 收到的客户端的消息
		String string = new String( ((String)message).getBytes("GBK"),"UTF-8");
		
		System.out.println("messageReceived : "+string);
		// 向客户端发送消息
		session.write("服务器收到消息："+string);
		
	}

	// 可以发送任何对象
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println("messageSent");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("sessionClosed");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("sessionCreated");
	}

	//  客户端进入空闲状态会打印
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		
		System.out.println("sessionIdle");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("sessionOpened");
	}

	
}
