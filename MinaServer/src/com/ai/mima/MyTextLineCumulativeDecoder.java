package com.ai.mima;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * 
 * @author AI
 *
 *  cumulative �����۵ģ��ۼӵ�
 *   ��ֹ�ͻ��˷��͹���������û�л��з������ݻᶪʧ
 */
public class MyTextLineCumulativeDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
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
						return true; // ��ʾ��ǰ�����Ѿ���ȡ��ɣ����Կ�ʼ��һ�ζ�ȡ
						}
					}
				in.position(startPosition);// û�л��з���ʱ����һ�ζ�ȡ����Ҳ�Ǵ�ͷ��ʼ����ֹ���ݶ�ʧ
				return false;// ��ֹû�л��з� "\n",��ȡ���ݶ�ʧ
	}
	

}
