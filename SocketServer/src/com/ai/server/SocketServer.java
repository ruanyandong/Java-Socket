package com.ai.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.WritableByteChannel;
import java.util.Timer;
import java.util.TimerTask;

public class SocketServer {

	BufferedReader bufferReader = null;
	BufferedWriter bufferWriter = null;
	
	public static void main(String[] args) {

		SocketServer socketServer = new SocketServer();
		socketServer.startServer();
	
	}
	
	public  void  startServer() {
		ServerSocket serverSocket = null ;
		
		Socket  socket = null;
		try {
			// alt+shift+z����try/catch
			serverSocket =  new ServerSocket(9999);
			// ����ס���ȴ��ͻ������ӣ��ͻ��������˲Ż��������ִ��
			// �ͻ���������������᷵��һ��Socket����
		    System.out.println("server started...");
		    
		    //  Ŀǰֻ�ܽ��յ�һ���ͻ��˵�����
			//socket = serverSocket.accept();
		    
		    // �������Ӷ���ͻ��ˣ�ÿ���ͻ��˶�����һ���߳�
		    while(true) {
		    	socket = serverSocket.accept();
		    	manageConnection(socket);
		    }
		   
			/**
			// һ�������hashcodeΨһ
			System.out.println("client "+socket.hashCode() +" connected");
			
			// ��socket�ж���ͻ�����Ϣ
			bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			
			// ��socket����ͻ���д����Ϣ
			bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			
			// ��ʱ��ͻ��˷�����Ϣ���൱��������
			// schedule(bufferWriter);
			 
			String receivedMsg;
			while((receivedMsg = bufferReader.readLine()) != null) {//�� \n Ϊ����
				// ��ӡ�ͻ��˷��͵���Ϣ
				System.out.println(receivedMsg);
				// ��ͻ��˻���Ϣ
				bufferWriter.write("server reply "+ receivedMsg+"\n");
				bufferWriter.flush();
			}
			*/
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				serverSocket.close();
				//bufferReader.close();
				//bufferWriter.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// ʵ�ֿ��Զ���ͻ������ӵĹ���
	public void  manageConnection(final Socket socket) {
		
	new Thread(new Runnable() {
		
		@Override
		public void run() {
			          try {
						// һ�������hashcodeΨһ
							System.out.println("client "+socket.hashCode() +" connected");
							// ��socket�ж���ͻ�����Ϣ
							bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
							// ��socket����ͻ���д����Ϣ
							bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
							
							// ��ʱ��ͻ��˷�����Ϣ���൱��������
							// schedule(bufferWriter);
						
							String receivedMsg;
							while((receivedMsg = bufferReader.readLine()) != null) {//�� \n Ϊ����
								// ��ӡ�ͻ��˷��͵���Ϣ
								System.out.println("client "+socket.hashCode()+": "+receivedMsg);
								// ��ͻ��˻���Ϣ
								bufferWriter.write("server reply "+ receivedMsg+"\n");
								bufferWriter.flush();
							}
							
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}finally {
						try {
							bufferReader.close();
							bufferWriter.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
						
		}
	}).start();
	}
	
	// ��ʱ��ͻ��˷�����Ϣ���൱��������
	public void schedule(final BufferedWriter writer) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					System.out.println("heart beat once...");
					// ע��ӻ��з�
					writer.write("heart beat once...\n");
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 3000,3000);
	}

}
