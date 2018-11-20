package com.ai.mima;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * 对传输的消息进行解密
 * @author AI
 *
 */
public class MyTextLineDecoder implements ProtocolDecoder {

	// 解码收到的数据
	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// 开始读取的位置             
		int startPosition = in.position();
		// 是否还有数据可读取
		while(in.hasRemaining()) {
			//  读取一个字节
			byte b = in.get();
			//  读取结束,遇到换行符结束
			if (b == '\n') {//  一行   如果发送过来的数据没有 '\n',则这条数据会丢失
				// 记录当前位置
				int currentPosition = in.position();
				// 记录当前的总长度
				int limit = in.limit();
				// 定位起点
				in.position(startPosition);
				// 定位终点
				in.limit(currentPosition);
				// 进行截取需要的数据
				IoBuffer buf= in.slice();
				byte[] dest = new byte[buf.limit()];
				buf.get(dest);
				String string = new String(dest);
				out.write(string);
				// 还原位置
				in.position(currentPosition);
				// 可读取的长度
				in.limit(limit);
			}
		}
		
	}

	@Override
	public void dispose(IoSession arg0) throws Exception {
		
	}

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1) throws Exception {
		
	}
	
	

}
