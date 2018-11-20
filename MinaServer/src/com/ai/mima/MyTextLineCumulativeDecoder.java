package com.ai.mima;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * 
 * @author AI
 *
 *  cumulative ：积累的，累加的
 *   防止客户端发送过来的数据没有换行符，数据会丢失
 */
public class MyTextLineCumulativeDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
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
						return true; // 表示当前数据已经读取完成，可以开始下一次读取
						}
					}
				in.position(startPosition);// 没有换行符的时候，下一次读取数据也是从头开始，防止数据丢失
				return false;// 防止没有换行符 "\n",读取数据丢失
	}
	

}
