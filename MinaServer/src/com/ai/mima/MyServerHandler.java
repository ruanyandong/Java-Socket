package com.ai.mima;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

// ������Ϣ�շ���handler
public class MyServerHandler extends IoHandlerAdapter {

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

		System.out.println("exceptionCaught: "+cause.getMessage());
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		// �յ��Ŀͻ��˵���Ϣ
		String string = new String( ((String)message).getBytes("GBK"),"UTF-8");
		
		System.out.println("messageReceived : "+string);
		// ��ͻ��˷�����Ϣ
		session.write("�������յ���Ϣ��"+string);
		
	}

	// ���Է����κζ���
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

	//  �ͻ��˽������״̬���ӡ
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		
		System.out.println("sessionIdle");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("sessionOpened");
	}

	
}
