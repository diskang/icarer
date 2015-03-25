package com.sjtu.icarer.common.utils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sjtu.icarer.common.config.Prefer;
import com.sjtu.icarer.common.constant.Const;
import com.sjtu.icarer.model.RunningDevice;

import android.content.Context;
import android.util.Log;

public class DBUtil {
	private ArrayList<String> arrayList = new ArrayList<String>();
	private ArrayList<String> brrayList = new ArrayList<String>();
	private ArrayList<String> crrayList = new ArrayList<String>();
	private ArrayList<String> drrayList = new ArrayList<String>();
	private ArrayList<String> errayList = new ArrayList<String>();
	private ArrayList<String> frrayList = new ArrayList<String>();
	private List<HashMap<String, String>> infoList = new ArrayList<HashMap<String, String>>();
	private HttpConnSoap Soap = new HttpConnSoap();
	private Prefer prefer;
	private String GERO_ID;
	
	public DBUtil(Context context){
		prefer =  new Prefer(context);
		GERO_ID = prefer.getGeroId();
	}
	
	public static Connection getConnection(){
		Connection con =null;
		try{
			//Class.forName("laobanHealthcare.sqlserver");
			//con = DriverManager.getConnection("jdbc:mysql://192.168.0.106:3306/test?useUnicode=true&characterEncoding=UTF-8","root","initial"); 
		}
		catch(Exception e){	
		}
		return con;
	}
	
	/**
	 * 传输ECG监测数据
	 */
	public String insertECG(String elder_id,String doctor_id,String date_str,String duration_str,String start_time,String data){
		String str = "";
		str = Soap.InsertData("insertECGData", elder_id, doctor_id, date_str, duration_str, start_time, data);
		
		return str;
	}
	
	/**
	 * 上传MAC
	 */
	public String uploadMAC(String gero_id, String roomNo, String padMac) {
		String str = "";
		str = Soap.InsertMac("insertMac", gero_id, roomNo, padMac);
		return str;
	}
	
	/**
	 * 上传完成项目
	 */
	public Boolean uploadItem(String carer_id, String roomNo, String itemFloor, String itemRoom, String[] itemElder) {
		String str = "";
		Boolean res = false;
		try {
			str = Soap.InsertItem("insertCarerItem", carer_id, roomNo, itemFloor, itemRoom, itemElder);
			Log.d("upload", str);
			if(str.equals("error")){
				res = false;
			}
			else{
				res = true;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public String getInfo(){
		String list = null;
		
		list = Soap.GetBasicInfo("selectAllDoctor",arrayList,brrayList);
		
		return list;
	}
	
	public List<String> getAllRoom(){
		List<String> list = new ArrayList<String>();
		//List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		arrayList.clear();
		brrayList.clear();
		crrayList.clear();
		infoList.clear();
		
		Log.d("TAG", "before turn to soap");
		
		InputStream allRoom = Soap.GetInfoList("selectAllRoom",GERO_ID,arrayList,brrayList);
		if(allRoom == null){
			return null;
		}
		crrayList = XMLParse.paraseCommentInfors (allRoom);
		
//		Log.d("TAG", "begin to make list");
//		Log.d("TAG", "crraylength"+crrayList.size());
		
		for(int j = 0;j < crrayList.size()-2;j += 3){
			HashMap<String, String> hashMap= new HashMap<String,String>();
			hashMap.put("elderId", crrayList.get(j));
			hashMap.put("elderName", crrayList.get(j+1));
			hashMap.put("elderRoom", crrayList.get(j+2));
//			
			String item3=crrayList.get(j+2);
			if(list.indexOf(item3)<0){
				list.add(crrayList.get(j+2));
			}
			infoList.add(hashMap);
//			Log.d("TAG","add to infoList");
		}
//		Log.d("TAG","getAllRoom end");
		
		return list;
	}
	
	public List<HashMap<String,String>> getELder(String roomNo){
		getAllRoom();
		List<HashMap<String,String>> elderList = new ArrayList<HashMap<String,String>>();
		if(infoList!=null&&infoList.size()>0){
			for(int i = 0; i <infoList.size();i++){
				String longRoomNumber = infoList.get(i).get("elderRoom");
				if(longRoomNumber.startsWith(roomNo)) {
					HashMap<String,String> hashMap = new HashMap<String,String>();
					hashMap.put("elderId", infoList.get(i).get("elderId"));
					hashMap.put("elderName",infoList.get(i).get("elderName"));
					elderList.add(hashMap);
				}
			}
		}
		
		return elderList;
	}
	
	public List<HashMap<String,String>> getAllCarer(){
		List<HashMap<String,String>> carerList = new ArrayList<HashMap<String ,String>>();

		arrayList.clear();
		brrayList.clear();
		crrayList.clear();
		
		Log.d("TAG", "before turn to soap");
		
		InputStream allCarer = Soap.GetInfoList("selectAllCarer",GERO_ID,arrayList,brrayList);
		if(allCarer == null){
			return null;
		}
		crrayList = XMLParse.paraseCommentInfors (allCarer);

		for(int j = 0;j < crrayList.size()-1;j += 2){
			HashMap<String, String> hashMap= new HashMap<String,String>();
			hashMap.put("carerId", crrayList.get(j));
			hashMap.put("carerName", crrayList.get(j+1));
			
			carerList.add(hashMap);
		}
		
		return carerList;
	}
	
	
	public List<String> getItem(int type) {
		crrayList.clear();
		
		List<List<String>> item = new ArrayList<List<String>>();
		
		InputStream allItem =  Soap.GetItemList("selectAllCarerItem",GERO_ID,type);
		if(allItem == null){
			return null;
		}
		crrayList = XMLParse.paraseCommentInfors(allItem);
		
		
		return crrayList;
	}
	
	public String[] getTodayCarer(String RoomNo){
		Date date = new java.sql.Date(new java.util.Date().getTime());
		String data  = Soap.GetArrangementByDay(date, 0);
		if(data!=null &&!data.isEmpty()&&data!="error"){
			try {
				JSONObject json = new JSONObject(data);
				//{"detail":[{"id":"20","name":"20","details":"A-6-0301,A0402,A0303,A0304,A0305,A0302"},{"id":"24","name":"24","details":"A0201,A0202,A0204"},{"id":"45","name":"45","details":"A0108"},{"id":"47","name":"47","details":"A0501,A0402"}]}
				JSONArray carerRoomList =json.getJSONArray("detail");
				
				for(int i=0;i<carerRoomList.length();i++){
					JSONObject jo = carerRoomList.getJSONObject(i);
					String RoomNumberListString;
					if(jo.has("details")){
						RoomNumberListString = jo.getString("details");
						if(RoomNumberListString==null || RoomNumberListString.isEmpty()){
							return null;
						}
					}else{
						return null;
					}
					
					if(RoomNumberListString!=null&&RoomNumberListString.contains(RoomNo)){
						String carerId = jo.getString("id");
						String carerName = jo.getString("name");
						String [] carerInfo = {carerId,carerName};
						return carerInfo;
					}
				}
				return null;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		else{
			return null;
		}
	}
	public ArrayList<RunningDevice> getCurrentDevicesByRoom(String roomNo){
		ArrayList<RunningDevice> list = new ArrayList<RunningDevice>();
		InputStream inputstream = Soap.GetDevicesByRoom(roomNo);
		
		list = XMLParse.paraseDevicesInfo(inputstream);
		return list;
	}
	
	public List<HashMap<String,String>> getAllDoctor(){
		List<HashMap<String,String>> doctorList = new ArrayList<HashMap<String ,String>>();

		arrayList.clear();
		brrayList.clear();
		crrayList.clear();
		infoList.clear();
		arrayList.clear();
		brrayList.clear();
		crrayList.clear();
		infoList.clear();
		
		arrayList.add("gero_id");
		brrayList.add(GERO_ID);
		
		Log.d("bowen", "before turn to soap");
		
		InputStream allRoom = Soap.GetInfoList("selectAllDoctor",GERO_ID,arrayList,brrayList);
		if(allRoom == null){
			return null;
		}
		crrayList = XMLParse.paraseCommentInfors (allRoom);

		for(int j = 0;j < crrayList.size();j += 2){
			HashMap<String, String> hasMap= new HashMap<String,String>();
			hasMap.put("doctorId", crrayList.get(j));
			hasMap.put("doctorName", crrayList.get(j+1));
			
			doctorList.add(hasMap);
		}
		
		return doctorList;
	}

	public boolean bindBluetoothDevice(String address,int elder_id,String device_name,String device_type){
		return Soap.BindBluetoothDevice(address, elder_id, device_name, device_type);
	}
	
	public boolean unBindBluetoothDevice(String address, int elder_id){
		return Soap.unBindBluetoothDevice(address, elder_id);
	}

	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
	    return pattern.matcher(str).matches();   
	}
	
}




