package com.ai.mima;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * �Դ������Ϣ���н���
 * @author AI
 *
 */
public class MyTextLineDecoder implements ProtocolDecoder {

	// �����յ�������
	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// ��ʼ��ȡ��λ��             
		int startPosition = in.position();
		// �Ƿ������ݿɶ�ȡ
		while(in.hasRemaining()) {
			//  ��ȡһ���ֽ�
			byte b = in.get();
			//  ��ȡ����,�������з�����
			if (b == '\n') {//  һ��   ������͹���������û�� '\n',���������ݻᶪʧ
				// ��¼��ǰλ��
				int currentPosition = in.position();
				// ��¼��ǰ���ܳ���
				int limit = in.limit();
				// ��λ���
				in.position(startPosition);
				// ��λ�յ�
				in.limit(currentPosition);
				// ���н�ȡ��Ҫ������
				IoBuffer buf= in.slice();
				byte[] dest = new byte[buf.limit()];
				buf.get(dest);
				String string = new String(dest);
				out.write(string);
				// ��ԭλ��
				in.position(currentPosition);
				// �ɶ�ȡ�ĳ���
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
