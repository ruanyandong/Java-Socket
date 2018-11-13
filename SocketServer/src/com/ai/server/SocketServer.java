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
			// alt+shift+z进行try/catch
			serverSocket =  new ServerSocket(9999);
			// 阻塞住，等待客户端连接，客户端连接了才会继续向下执行
			// 客户端连入这个方法会返回一个Socket对象
		    System.out.println("server started...");
		    
		    //  目前只能接收到一个客户端的连接
			//socket = serverSocket.accept();
		    
		    // 可以连接多个客户端，每个客户端都开启一个线程
		    while(true) {
		    	socket = serverSocket.accept();
		    	manageConnection(socket);
		    }
		   
			/**
			// 一个对象的hashcode唯一
			System.out.println("client "+socket.hashCode() +" connected");
			
			// 从socket中读入客户端信息
			bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			
			// 从socket中向客户端写入信息
			bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			
			// 定时向客户端发送消息，相当于心跳包
			// schedule(bufferWriter);
			 
			String receivedMsg;
			while((receivedMsg = bufferReader.readLine()) != null) {//以 \n 为结束
				// 打印客户端发送的消息
				System.out.println(receivedMsg);
				// 向客户端回消息
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
	
	// 实现可以多个客户端连接的功能
	public void  manageConnection(final Socket socket) {
		
	new Thread(new Runnable() {
		
		@Override
		public void run() {
			          try {
						// 一个对象的hashcode唯一
							System.out.println("client "+socket.hashCode() +" connected");
							// 从socket中读入客户端信息
							bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
							// 从socket中向客户端写入信息
							bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
							
							// 定时向客户端发送消息，相当于心跳包
							// schedule(bufferWriter);
						
							String receivedMsg;
							while((receivedMsg = bufferReader.readLine()) != null) {//以 \n 为结束
								// 打印客户端发送的消息
								System.out.println("client "+socket.hashCode()+": "+receivedMsg);
								// 向客户端回消息
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
	
	// 定时向客户端发送消息，相当于心跳包
	public void schedule(final BufferedWriter writer) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					System.out.println("heart beat once...");
					// 注意加换行符
					writer.write("heart beat once...\n");
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 3000,3000);
	}

}
