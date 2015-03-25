package com.sjtu.icarer.common.utils;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import com.sjtu.icarer.model.RunningDevice;

import android.util.Log;
import android.util.Xml;

public class XMLParse {
	public static ArrayList<String> paraseCommentInfors (InputStream inputStream){
		ArrayList<String> list = new ArrayList<String>();
		XmlPullParser parser = Xml.newPullParser();
		
		try {
			parser.setInput (inputStream, "UTF-8");  
			int eventType = parser.getEventType();
			boolean isOdd = false;
			/*String info = new String();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			StringBuffer buffer = new StringBuffer();
			String inputString = "";
			while ((inputString = in.readLine()) != null){
				buffer.append(inputString);
			}
			
			inputString = buffer.toString();*/
			
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.TEXT:
						if(!isOdd) {
							isOdd = true;
						}
						else {
							String content = parser.getText();
							Log.d("TAG",content);
							list.add(content);
							isOdd = false;
						}
						break;
					case XmlPullParser.START_TAG:
						String name = parser.getName();
						if(name.equalsIgnoreCase("string")){
/*							if(parser.next() == XmlPullParser.TEXT){
								String content = parser.getText();
								Log.d("bowen",content);
								list.add(content);
							}*/
						}
						Log.d("TAG", "get string");
						break;
					case XmlPullParser.END_TAG:
						break;
				}
				eventType = parser.next();
			}
			inputStream.close();
		}
		catch (Exception e) {
			 e.printStackTrace();
		}
		
		Log.d("TAG","XMLParse end");
		for(int j=0;j<list.size();j++){
			Log.d("TAG", "listItem="+list.get(j));
		}
		Log.d("TAG","listlength="+list.size());
		return list; 
	}
	
	public static ArrayList<RunningDevice> paraseDevicesInfo(InputStream inputStream){
		ArrayList<RunningDevice> list = new ArrayList<RunningDevice>();
		XmlPullParser parser = Xml.newPullParser();
		boolean evenOdd = false;
		
		try{
			parser.setInput (inputStream, "UTF-8");  
			int eventType = parser.getEventType();
			int timer = 0;
			RunningDevice device = null;
			
			while(eventType != XmlPullParser.END_DOCUMENT){
				switch (eventType){
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.TEXT:
						if(evenOdd == false){
							evenOdd = true;
							break;
						}
						evenOdd = false;
						switch(timer){
						case 0://…Ë±∏ID
							device = new RunningDevice();
							timer = (timer+1)%5;
							device.DeviceId=parser.getText();
							break;
						case 1:
							timer = (timer+1)%5;
							device.DeviceMac=parser.getText();
							break;
						case 2:
							timer = (timer+1)%5;
							device.ElderId=parser.getText();
							break;
						case 3:
							timer = (timer+1)%5;
							device.DeviceName=parser.getText();
							break;
						case 4:
							timer = (timer+1)%5;
							device.Type = parser.getText();
							list.add(device);
							break;
					}
				}
				eventType = parser.next();
			}
		}
		catch(Exception e){
			
		}
		
		return list;
		
	}
	
}
