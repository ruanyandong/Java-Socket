package com.ai.mima;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * ���ܴ����ı�
 * @author AI
 *
 */
public class MyTextLineEncoder implements ProtocolEncoder{

	@Override
	public void dispose(IoSession arg0) throws Exception {
		
	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		String string = null;
		// ����ı������ı�,��Ҫ����������
		if (message instanceof String) {
			string = (String) message;
		}
		
		if (string != null) {
			// �ȴ�session�л�ȡ�����ַ���
			CharsetEncoder charsetEncoder =(CharsetEncoder)session.getAttribute("encoder");
			if (charsetEncoder == null) {
				// ��ȡϵͳĬ�ϵ��ַ���
				charsetEncoder = Charset.defaultCharset().newEncoder();
				session.setAttribute("encoder", charsetEncoder);
			}	
			// ���ַ����ı������ڴ�,��СΪ�ַ����ĳ���
			IoBuffer ioBuffer = IoBuffer.allocate(string.length());
			// �����ڴ��Զ���չ
			ioBuffer.setAutoExpand(true);
			// �ı��ͱ��뷽ʽ
			ioBuffer.putString(string, charsetEncoder);
			// ������ã���֤limit = position 
			ioBuffer.flip();
			
			out.write(ioBuffer);
		}
		
	}
	
	

}
