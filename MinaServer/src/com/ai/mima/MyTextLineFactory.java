package com.ai.mima;

import javax.print.attribute.standard.Media;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 对传输的消息进行加解密
 * @author AI
 *
 */
public class MyTextLineFactory implements ProtocolCodecFactory {

	private MyTextLineEncoder mEncoder;
	private MyTextLineCumulativeDecoder mCumulativeDecoder;
	private MyTextLineDecoder mDecoder;
	
	public MyTextLineFactory() {
		mEncoder = new MyTextLineEncoder();
		mDecoder = new MyTextLineDecoder();
		mCumulativeDecoder = new MyTextLineCumulativeDecoder();
	}
	
	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return mDecoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return mEncoder;
	}
	
	
	

}
