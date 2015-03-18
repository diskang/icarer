package com.sjtu.icarer.common.config;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.sjtu.icarer.common.constant.Const;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Prefer {
	
	private final static String TAG = "Prefer";
	private Context mcontext;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private String GERO_ID;
	private String ROOM_NUMBER;
	private String CARER_ID;
	private String CARER_NAME;
	
	public int UPDATE_HOUR;
	public int UPDATE_MINUTE;
	
	
	private String ELDER_ID_LIST;
	private String ELDER_NAME_LIST;
	private String ITEM_ELDER_LIST;
	private String ITEM_ROOM_LIST;
	
	
	public Prefer(Context context){
		mcontext = context.getApplicationContext();
		preferences = mcontext.getSharedPreferences("localinf",Context.MODE_PRIVATE);
		editor = preferences.edit();	
	}
	
	public String getGeroId(){
		GERO_ID = preferences.getString(Const.PREFER_GERO_ID,null);
		return GERO_ID;
	}
	public void setGeroId(String geroId){
		editor.putString(Const.PREFER_GERO_ID, geroId);
		editor.commit();
	}
	
	public String getRoomNumber(){
		ROOM_NUMBER = preferences.getString(Const.PREFER_ROOM_NUMBER,null);
		return ROOM_NUMBER;
	}
	public void setRoomNumber(String RoomNumber){
		editor.putString(Const.PREFER_ROOM_NUMBER,RoomNumber);
		editor.commit();
	}
	
	public String getCarerId(){
		CARER_ID  = preferences.getString(Const.PREFER_CARER_ID,null);
		return CARER_ID;
	}
	public void setCarerId(String carerId){
		editor.putString(Const.PREFER_CARER_ID,carerId);
		editor.commit();
	}
	
	public String getCarerName(){
		CARER_NAME = preferences.getString(Const.PREFER_CARER_NAME,null);
		return CARER_NAME;
	}
	public void setCarerName(String carerName){
		editor.putString(Const.PREFER_CARER_NAME,carerName);
		editor.commit();
	}
	
	public String[] getElderIdList(){
		ELDER_ID_LIST = preferences.getString(Const.PREFER_ELDER_ID,null);
		if(ELDER_ID_LIST!=null){
			String[] elderidlist = ELDER_ID_LIST.split(",");
			return elderidlist;
		}
		else{
			return null;
		}
	}
	public void setElderIdList(String[] elderIdList){
		if(elderIdList==null){
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<elderIdList.length;i++){
			sb.append(elderIdList[i]+",");
		}
		editor.putString(Const.PREFER_ELDER_ID,sb.toString());
		editor.commit();
	}
	
	public String[] getElderNameList(){
		ELDER_NAME_LIST = preferences.getString(Const.PREFER_ELDER_NAME,null);
		if(ELDER_NAME_LIST!=null){
			String[] elderidlist = ELDER_NAME_LIST.split(",");
			return elderidlist;
		}
		else{
			return null;
		}
	}
	public void setElderNameList(String[] elderNameList){
		if(elderNameList==null){
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<elderNameList.length;i++){
			sb.append(elderNameList[i]+",");
		}
		editor.putString(Const.PREFER_ELDER_NAME,sb.toString());
		editor.commit();
	}
	
	public List<String> getItemRoomList(){
		ITEM_ROOM_LIST= preferences.getString(Const.PREFER_ITEM_ROOM,null);
		if(ITEM_ROOM_LIST!=null){
			String[] itemroomlist = ITEM_ROOM_LIST.split(",");
			List<String> list =new ArrayList<String>();
			for(int i=0;i<itemroomlist.length;i++){
				list.add(itemroomlist[i]);
			}
			return list;
		}
		else{
			return null;
		}
	}
	public void setItemRoomList(List<String> itemRoomList){
		if(itemRoomList==null){
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<itemRoomList.size();i++){
			sb.append(itemRoomList.get(i)+",");
		}
		editor.putString(Const.PREFER_ITEM_ROOM,sb.toString());
		editor.commit();
	}
	
	public List<String> getItemElderList(){
		ITEM_ELDER_LIST= preferences.getString(Const.PREFER_ITEM_ELDER,null);
		if(ITEM_ELDER_LIST!=null){
			String[] itemelderlist = ITEM_ELDER_LIST.split(",");
			
			List<String> list =new ArrayList<String>();
			for(int i=0;i<itemelderlist.length;i++){
				list.add(itemelderlist[i]);
			}
			return list;
		}
		else{
			return null;
		}
	}
	public void setItemElderList(List<String> itemElderList){
		if(itemElderList==null){
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<itemElderList.size();i++){
			sb.append(itemElderList.get(i)+",");
		}
		editor.putString(Const.PREFER_ITEM_ELDER, sb.toString());
		editor.commit();
	}
	
	
	public boolean updatedTimeStamp(){
		/*update per day, if updated return true; if not, return false to update...*/
		String oldStamp = preferences.getString(Const.PREFER_TIMESTAMP,null);
		String newStamp = new java.sql.Date(new java.util.Date().getTime())+"";
		
		Log.d(TAG,newStamp);
		if (newStamp.equals(oldStamp)){
			return true;
		}else{
			editor.putString(Const.PREFER_TIMESTAMP,newStamp);
			editor.commit();
			return false;
		}
	}
}
