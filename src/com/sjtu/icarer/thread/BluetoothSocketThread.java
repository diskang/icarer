package com.sjtu.icarer.thread;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class BluetoothSocketThread implements Runnable{
	public BluetoothDevice mDevice = null;
	public int elderId;
	public String roomNo = "";
	public String type = "";
	public EHandler mEHandler;
	public BluetoothSocket mbtSocket = null;
	public Socket mnetSocket = null;
	
	private InputStream btSocketInput = null;
	private OutputStream btSocketOutput = null;
	private InputStream ntSocketInput = null;
	private OutputStream ntSocketOutput = null;
	
	public final String ECG_BLUETOOTH_START_COMMAND = "AT+SMTP=0\r\n";
	public final String ECG_BLUETOOTH_CONNECT_COMMAND = "AT+SMRS=1\r\nAT+SMST\r\n";
	public final String BAND_BLUETOOTH_START_COMMAND = "";
	static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	
	private Handler mHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg){
			
		}
	};
	
	
	public BluetoothSocketThread(){
		
	}
	
	
	public BluetoothSocketThread(BluetoothDevice receiverDevice){
		this.mDevice = receiverDevice;
	}
	
	
	@Override
	public void run(){
		Log.d("bowen","running bluetooth thread");
		
		try{
			mbtSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
			mbtSocket.connect();
			
			Log.d("bowen", "finish connect");
			
			Thread.currentThread().sleep(2000);
			byte[] localCommand =ECG_BLUETOOTH_START_COMMAND.getBytes();

			try{
				mbtSocket.getOutputStream().write(localCommand);
				Log.d("bowen-write","write 1 over");
				}
			catch(Exception e){
				Log.d("bowen-write", "write 1 failed");
			}
			
			Thread.currentThread().sleep(2000);
			localCommand = ECG_BLUETOOTH_CONNECT_COMMAND.getBytes();
			
			try{
				mbtSocket.getOutputStream().write(localCommand);
				Log.d("bowen-write","write 2 over");
			}
			catch(Exception e){
				Log.d("bowen-write", "write 2 failed");
			}
			
			this.btSocketInput = mbtSocket.getInputStream();
			this.btSocketOutput = mbtSocket.getOutputStream();
			
			
			try{
				Socket socket = new Socket("202.120.38.227",4700);
				ntSocketInput = socket.getInputStream();
				ntSocketOutput = socket.getOutputStream();
				
				Log.d("bowen", "create java socket over");
				try{
					byte[] buffer = new byte[1024];
					int bytes;
					
					while(true){
						try{
							bytes = btSocketInput.read(buffer);
							Log.d("bowen",bytes+"");
							ntSocketOutput.write(bytes);
						}
						catch(Exception e){
							String msg = e.getMessage().toString();
							Log.d("bowen-thread","while + " +msg);
						}
					}
				}
				catch(Exception e){
					
				}
			}
			catch(Exception e){
				Log.d("bowen", "create java socket failed");
			}
			Log.d("bowen-thread","connect over");
		}
		catch(Exception e){
			String msg = e.getMessage().toString();
			Log.d("bowen-thread","bt socket failed for "+msg);
		}
	}
	
	public void close(){
		try{
			mnetSocket.close();
			mbtSocket.close();
		}
		catch(Exception e){
			String msg = e.getMessage().toString();
			Log.d("bowenthread","close failed for "+msg);
		}
	}
	
	public void connect(){
		
	}
	
	public void begin(){
		byte[] localCommand =ECG_BLUETOOTH_START_COMMAND.getBytes();
		try{
			Log.d("bowenthread", "before write");
			mbtSocket.getOutputStream().write(localCommand);
			Log.d("bowenthread","write over");
		}
		catch(Exception e){
			
		}
	}
	
	public void write(){
		
	}
	
	public void read(){
		
	}
	
	
	class EHandler extends Handler{
		
		public EHandler(Looper looper){
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg){
			
		}
		
	}
	
	
	
}
