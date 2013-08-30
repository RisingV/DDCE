package com.bdcom.dce.datadispacher;

import com.bdcom.dce.sys.AppSession;
import com.bdcom.dce.util.log.ErrorLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalTalker {
	
	private ServerSocket localServer;
	
	private Socket localClient;
	
	private static ExecutorService exec;
	
	private static int port;

	static {
		exec = Executors.newCachedThreadPool();
		port = 9999;
	}
	
	public static void main(String[] args) {
		LocalTalker talker = new LocalTalker();
		talker.init();
		talker.sendResult(args[0]);
	}
	
	public void terminalServer() {
		if ( null != localServer ) {
			try {
				localServer.close();
			} catch (IOException e) {
				ErrorLogger.log(e.getMessage());
			}
		}
		localServer = null;
	}
	
	public void closeClient() {
		try {
			localClient.close();
		} catch (IOException e) {
		}
	}
	
	public void sendResult(String rs) {
		int status = 0;
		PrintWriter out = null;
		try {
			out = new PrintWriter(localClient.getOutputStream());
		} catch (IOException e) {
			ErrorLogger.log(e.getMessage());
			status = -1;
		} finally {
			if ( status < 0 ) {
				return;
			}
		}
		out.println(rs);
		out.flush();
	}
	
	public String[] getRemoteInfo() {
		
		BufferedReader in = null;
		String sid = null;
		String isFC = null;
		try {
			in = new BufferedReader(
					 new InputStreamReader(localClient.getInputStream()));
			sid = in.readLine();
			isFC = in.readLine();
		} catch (IOException e) {
			ErrorLogger.log(e.getMessage());
		}
		
		return new String[]{sid, isFC};
	}

	public void init() {
		if ( null == localClient ) {
			try {
				localClient = new Socket(InetAddress.getLocalHost(),port);
			} catch (UnknownHostException e) {
			} catch (IOException e) {
			}
		}
	}
	
	public void start() {
		if ( null == localServer ) {
			try {
				this.localServer = new ServerSocket(port);
			} catch (IOException e1) {
				ErrorLogger.log(e1.getMessage());
				return;
			}
			new Thread(
					new Runnable() {
						@Override
						public void run() {
							while(true) {
								if ( null == localServer ) {
									break;
								}
								Socket client = null;
								try {
									client = localServer.accept();
								} catch (IOException e) {
								}
//								portInfo();
								if ( null != client ) {
									exec.execute(getChildThread(client));
								}
							}
						}
					}
					).start();
		}
	}

	private Runnable getChildThread(final Socket client) {
		return new Runnable() {
			@Override
			public void run() {
				 try {
					BufferedReader in = new BufferedReader(
							 new InputStreamReader(client.getInputStream()));
					PrintWriter out = new PrintWriter(
							client.getOutputStream());
					
					out.println(getSessionId());
					out.println(String.valueOf(getIsFC()));
					out.flush();
					
					String rs = null;
					rs = in.readLine();
					AppSession.getScriptExecutor().setSendResult(true, rs);
					
				} catch (IOException e) {
				} finally {
					try {
						if ( null != client) {
							client.close();
						}
					} catch (IOException e) {
					}
				}
			}
			
		};
	}
	
	private String getSessionId() {
		return AppSession.getSessionId();
	}
	
	private boolean getIsFC() {
		return AppSession.isFC();
	}
	
}
