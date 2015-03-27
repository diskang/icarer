package com.sjtu.icarer.common.utils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.common.constant.Constants;

import android.util.Log;



public class HttpConnSoap {
	public final String TIME_OUT = "操作超时";
	
	
	/**
	 * 该函数绑定蓝牙设备
	 */
	public boolean BindBluetoothDevice(String address,int elder_id,String device_name,String device_type){
		//String ServerUrl = ServerHost + "insertBluetoothDeviceInfo?address="+address+"&elder_id="+elder_id+"&device_name="+device_name+"&type="+device_type;
		
		String ServerUrl = Url.SOAP_URL + "insertBluetoothDeviceInfo";
		
		Log.d("bowen", ServerUrl);
		HttpPost httpRequest = new HttpPost(ServerUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("address",address));
		params.add(new BasicNameValuePair("elder_id",elder_id+""));
		params.add(new BasicNameValuePair("device_name",device_name));
		params.add(new BasicNameValuePair("type",device_type));
		String trueStr = "true";
		
		try{
			httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse response=new DefaultHttpClient().execute(httpRequest);
			
			if(response.getStatusLine().getStatusCode() == 200) {
				return true;
/*				String respStr = EntityUtils.toString(response.getEntity());
				Log.d("bowen-getcontent",respStr);*/
			}
		}
		catch(Exception e){
			String exceptionStr = e.getMessage();
			Log.d("bowen",exceptionStr);
		}
		return false;
	}
	
	/**
	 * 该函数解除蓝牙设备的绑定
	 */
	public boolean unBindBluetoothDevice(String address, int elder_id){
		String ServerUrl = Url.SOAP_URL + "unBindBluetoothDevice";
		
		HttpPost post = new HttpPost(ServerUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair("address",address));
		params.add(new BasicNameValuePair("elder_id",elder_id+""));
		
		try{
			post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			
			if(response.getStatusLine().getStatusCode() == 200) {
				return true;
			}
		}catch(Exception e){}
		return false;
	}
	
	/**
	 * 该函数传输测量数据
	 */
	public String InsertData(String methodName, String elder_id, String doctor_id, 
			String date_str, String duration_str, String start_time, String data) {
		String resp = "";
		String ServerUrl = Url.SOAP_URL+methodName
				+"?elder_id="+elder_id+"&doctor_id="+doctor_id+"&date_str="+date_str
				+"&duration_str="+"&start_time="+start_time+"&data="+data;
		
		Log.d("TAG",ServerUrl);
		
		HttpGet  httpRequest  = new HttpGet(ServerUrl);
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResp = httpClient.execute(httpRequest);
			if(httpResp.getStatusLine().getStatusCode()==200) {	
				resp = EntityUtils.toString(httpResp.getEntity(),"UTF-8");
			}
			else{
				Log.d("TAG",Integer.toString(httpResp.getStatusLine().getStatusCode()));
				resp="error";
			}
		}
		catch(Exception e){
			resp="error";
		}
		return resp;
	}
	
	/**
	 * 该函数绑定pad设备与房间
	 */
	public String InsertMac(String methodName, String gero_id, String roomNo, String padMac) {
		String resp = "";
		String ServerUrl = Url.SOAP_URL + methodName;
		
		HttpPost  httpRequest  = new HttpPost(ServerUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("gero_id", gero_id));
		params.add(new BasicNameValuePair("roomNo",roomNo));
		params.add(new BasicNameValuePair("padMac",padMac));
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResp = httpClient.execute(httpRequest);
			if(httpResp.getStatusLine().getStatusCode()==200) {	
				resp = EntityUtils.toString(httpResp.getEntity(),"UTF-8");
			}
			else{
				Log.d("TAG",Integer.toString(httpResp.getStatusLine().getStatusCode()));
			}
		}
		catch(Exception e){
			resp = "error";
		}
		return resp;
	}
	
	/**
	 * 该函数提交完成项目
	 * @param itemElder  其元素个数由该房间老人数量决定
	 * @throws 针对中文字符转换异常
	 */
	public String InsertItem(String methodName, String carer_id, String roomNo, String itemFloor,
			String itemRoom, String[] itemElder) throws UnsupportedEncodingException {
		String resp = "";
		Boolean isEmpty = true;
		String ServerUrl = Url.SOAP_URL+methodName+"?carer_id="+carer_id+"&roomNo="+roomNo;
		String[] floorTemp = itemFloor.split(",");
		String[] roomTemp = itemRoom.split(",");
		String temp = "";
		for(int i=0; i<floorTemp.length; i++) {
				ServerUrl += "&itemArray_floor=" + "F";
				temp += "&itemName=" + getGBK2Utf8(floorTemp[i]);
		}
		for(int j=0; j<roomTemp.length; j++) {
				ServerUrl += "&itemArray_room=" + "R";
				temp += "&itemName=" + getGBK2Utf8(roomTemp[j]);
		}
		
		for(int m=0; m<itemElder.length; m++) {
			if(!itemElder[m].equals("")) {
				isEmpty = false;
				String[] eTemp = itemElder[m].split(",");
				for(int n=0; n<eTemp.length; n++) {
					String[] t = eTemp[n].split(":");
					ServerUrl += "&itemArray_elder=" + "E" + t[0];
					temp += "&itemName=" + getGBK2Utf8(t[1]);
				}
			}
		}
		if (isEmpty) {
			ServerUrl += "&itemArray_elder=E";
			temp += "&itemName=";
		}
		ServerUrl += temp;
		
		HttpGet  httpRequest  = new HttpGet(ServerUrl);
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResp = httpClient.execute(httpRequest);
			if(httpResp.getStatusLine().getStatusCode()==200) {	
				resp = EntityUtils.toString(httpResp.getEntity(),"UTF-8");
			}
			else{
				Log.d("wag",Integer.toString(httpResp.getStatusLine().getStatusCode()));
				resp = "error";
			}
		}
		catch(Exception e){
			resp="error";
		}
		return resp;
	}
	
	/**
	 * 该函数获取房间中设备信息
	 */
	public InputStream GetDevicesByRoom(String roomNo){
		InputStream list = null;
		String ServerUrl = Url.SOAP_URL + "selectBluetoothDeviceByRoom?roomNo=" + roomNo;
		InputStream result = null;
		HttpGet httpRequest = new HttpGet(ServerUrl);
		
		try{
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResp = httpClient.execute(httpRequest);
			
			if(httpResp.getStatusLine().getStatusCode()==200){
				list = httpResp.getEntity().getContent();
				//result = EntityUtils.toString(httpResp.getEntity(),"UTF-8");
			}
		}catch(Exception e){
		}
		
		return list;
	}
	
	public String GetBasicInfo(String methodName, ArrayList<String> Parameters, ArrayList<String> ParValues) {
		List<NameValuePair> params = null;
		List<String> list = new ArrayList<String>();
		String ServerUrl = Url.SOAP_URL+methodName;
		Log.d("TAG", ServerUrl);
		String soapAction = "http://tempuri.org/"+methodName;
		String result = null;
		
		HttpGet  httpRequest  = new HttpGet(ServerUrl);
		try {
			HttpClient httpClient = new DefaultHttpClient();
//			Log.d("TAG","before execute");
			HttpResponse httpResp = httpClient.execute(httpRequest);
//			Log.d("TAG","after execute");
			if(httpResp.getStatusLine().getStatusCode()==200) {
				result = EntityUtils.toString(httpResp.getEntity(),"UTF-8");
			}
			else{
//				Log.d("TAG",Integer.toString(httpResp.getStatusLine().getStatusCode()));
				result = "error";
			}
		}
		catch(Exception e){
			result = "error";
		}
		return result;
	}
	
	public String GetArrangementByDay(Date date, int type){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
		String ddate =format.format(date);
		String peopleType;
		if(type==0){//carer
			peopleType = "0";
			
		}
		else{
			peopleType = "1";
		}
		
		String ServerUrl = Url.SOAP_URL + "getArrangement?people_type="+peopleType+"&gero_id="+Constants.GERO_ID
				+"&date="+ddate;
		HttpGet  httpRequest  = new HttpGet(ServerUrl);
		String result = null;
		try {
//			Log.d("TAG","before execute");
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResp = httpClient.execute(httpRequest);
//			Log.d("TAG", "AF");
			
			if(httpResp.getStatusLine().getStatusCode()<300) {
//				result = httpResp.getEntity().getContent();
				result = EntityUtils.toString(httpResp.getEntity(),"UTF-8");
			}
			else{
//				Log.d("TAG",Integer.toString(httpResp.getStatusLine().getStatusCode()));
				result = "error";
			}
			return result;
			
		}
		catch(Exception e){
			e.printStackTrace();
			return "error";
		}
		
		
	}
	public	InputStream GetInfoList(String methodName,String geroId, ArrayList<String> Parameters, ArrayList<String> ParValues) {
		if(geroId==null || geroId.isEmpty()){
			return null;
		}
		String ServerUrl = Url.SOAP_URL + methodName + "?gero_id="+geroId;
		
		
		try {
//			Log.d("TAG","before execute");
			HttpGet  httpRequest  = new HttpGet(ServerUrl);
			InputStream result = null;
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResp = httpClient.execute(httpRequest);
//			Log.d("TAG", "AF");
			
			if(httpResp.getStatusLine().getStatusCode()==200) {
				result = httpResp.getEntity().getContent();
				//result = EntityUtils.toString(httpResp.getEntity(),"UTF-8");
			}
			else{
//				Log.d("TAG",Integer.toString(httpResp.getStatusLine().getStatusCode()));
				result = null;
			}
			return result;
			
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public	InputStream GetItemList(String methodName,String geroId, int type) {
		//type   1 楼面2房间3个人
		if(geroId==null || geroId.isEmpty()){
			return null;
		}
		String ServerUrl = Url.SOAP_URL + methodName + "?gero_id="+geroId+"&type="+type;

		HttpGet  httpRequest  = new HttpGet(ServerUrl);
		InputStream result = null;
		
		try {
//			Log.d("TAG","before execute");
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResp = httpClient.execute(httpRequest);
//			Log.d("TAG", "AF");
			
			if(httpResp.getStatusLine().getStatusCode()==200) {
				result = httpResp.getEntity().getContent();
				//result = EntityUtils.toString(httpResp.getEntity(),"UTF-8");
			}
			else{
//				Log.d("TAG",Integer.toString(httpResp.getStatusLine().getStatusCode()));
				result = null;
			}
			return result;
			
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 该函数将GBK字符串转化为UTF-8编码
	 */
	public static String getGBK2Utf8(String gbkStr) throws UnsupportedEncodingException {
		String out;
		String iso = new String(gbkStr.getBytes("UTF-8"),"ISO-8859-1");
		out = new String(iso.getBytes("ISO-8859-1"),"UTF-8");
		return out;
	}
	
	
}
