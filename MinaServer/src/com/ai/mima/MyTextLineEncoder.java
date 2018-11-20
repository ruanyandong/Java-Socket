package com.ai.mima;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * 加密传输文本
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
		// 传输的必须是文本,是要发出的数据
		if (message instanceof String) {
			string = (String) message;
		}
		
		if (string != null) {
			// 先从session中获取编码字符集
			CharsetEncoder charsetEncoder =(CharsetEncoder)session.getAttribute("encoder");
			if (charsetEncoder == null) {
				// 获取系统默认的字符集
				charsetEncoder = Charset.defaultCharset().newEncoder();
				session.setAttribute("encoder", charsetEncoder);
			}	
			// 给字符串文本分配内存,大小为字符串的长度
			IoBuffer ioBuffer = IoBuffer.allocate(string.length());
			// 允许内存自动扩展
			ioBuffer.setAutoExpand(true);
			// 文本和编码方式
			ioBuffer.putString(string, charsetEncoder);
			// 必须调用，保证limit = position 
			ioBuffer.flip();
			
			out.write(ioBuffer);
		}
		
	}
	
	

}
