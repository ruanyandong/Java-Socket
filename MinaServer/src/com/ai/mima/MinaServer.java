package com.ai.mima;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

// Mina�����紦�����Ϣ�������
public class MinaServer {

	public static void main(String[] args) {
       try {
		    // 1����һ��
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			// 2���ڶ���,����handler������Ϣ����handler����
			acceptor.setHandler(new MyServerHandler());
			// 3�����������˺�����յ���Ϣ���߷�����Ϣ��getFilterChain���Ի������������,factory
			//         �ǶԵ����ݽ��мӽ��룬�����Լ����壬Ҳ����ϵͳ��new TextLineCodecFactory()
			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MyTextLineFactory()));;
			// 5���ӿͻ��˺ͷ�����û�н��ж�д���ͽ������״̬��ÿ��5��ͻ���÷���
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,5);
			// 4�����Ĳ�,��Ӷ˿ںţ�����������
			acceptor.bind(new InetSocketAddress(9999));
			
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		
	}

}
