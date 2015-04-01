package com.example.android.wifidirect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SendTestMessage extends IntentService{
	static{
		Log.d(WiFiDirectActivity.TAG, "进入send");
	}
	public SendTestMessage(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public SendTestMessage(){
		super("SendTestMessage");
	}
	final private int mPort = 8800;
	final private int SOCKET_TIMEOUT = 2000 ;
	final private String mIP = "192.168.49.1"; 
	private ObjectOutputStream oos = null ;
	private OutputStream os = null ;
	private Socket socket = null ;
	public void StartClient() throws IOException{
		try {
			Log.d(WiFiDirectActivity.TAG, "开始发送心跳信号");
			socket = new Socket();
			socket.setReuseAddress(true);
			socket.connect((new InetSocketAddress(mIP, mPort)), SOCKET_TIMEOUT);
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
			
			oos.writeObject(new String("BROFIST"));
			Log.d(WiFiDirectActivity.TAG, "发送成功---");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			if (oos != null)
				oos.close();
			if (os != null)
				os.close();
			socket.close();
		}
	}
	
	@Override
	public void onCreate() {
		Log.d(WiFiDirectActivity.TAG, "service被创建");
		super.onCreate();
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(WiFiDirectActivity.TAG, "service被启动");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(WiFiDirectActivity.TAG, "service被销毁");
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(WiFiDirectActivity.TAG, "发送端onHandle");
		try {
			
			StartClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
