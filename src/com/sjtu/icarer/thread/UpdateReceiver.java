package com.sjtu.icarer.thread;

import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sjtu.icarer.HomeActivity;
import com.sjtu.icarer.common.config.Prefer;
import com.sjtu.icarer.common.constant.Const;
import com.sjtu.icarer.common.utils.DBUtil;

public class UpdateReceiver extends BroadcastReceiver{
    //private static final String TAG = "UpdateReceiver";
	private Context mcontext;
	private DBUtil dbUtil;
	private Prefer prefer;
	private String carerId;
	private String carerName;
	private String roomNumber;
	@Override
	public void onReceive(Context context, Intent intent) {
		mcontext = context;
//		Toast msg = Toast.makeText(context,intent.getAction(), Toast.LENGTH_LONG);
//        msg.show();
        dbUtil = new DBUtil(mcontext);
		prefer = new Prefer(mcontext);
		roomNumber = prefer.getRoomNumber();
		if (Const.ACTION_UPDATE_INFO.equals(intent.getAction())) {
			updateCarer();
			//update if changed
			String[] elderIdList = prefer.getElderIdList();
			List<HashMap<String,String>> elderList = dbUtil.getELder(roomNumber);
			if(elderList!=null &&!elderList.isEmpty() &&(
					//first time to get list || list changes
					elderIdList==null||!elderList.equals(elderIdList))){
				String []NameList = new String[elderList.size()];
				String []IdList = new String[elderList.size()];
				for(int i=0; i<elderList.size(); i++) {
		            NameList[i] = elderList.get(i).get("elderName");
		            IdList[i] = elderList.get(i).get("elderId");
		        }
				prefer.setElderIdList(IdList);
				prefer.setElderNameList(NameList);
			}
			//update if changed
			List<String> itemElderList = prefer.getItemElderList();
			List<String> itemlist3 = dbUtil.getItem(3);
			if(itemlist3!=null && (
					itemElderList==null||!itemlist3.equals(itemElderList))){
				
				prefer.setItemElderList(itemlist3);
			}
			
			List<String> itemRoomList = prefer.getItemRoomList();
			List<String> itemlist2 = dbUtil.getItem(2);
			if(itemlist2!=null && (
					itemRoomList==null||!itemlist2.equals(itemRoomList))){
				
				prefer.setItemRoomList(itemlist2);
			}
			
			intent = new Intent(mcontext, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mcontext.startActivity(intent);
			
		}else if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())){
			
			if (!prefer.updatedTimeStamp()){
				updateCarer();
			}
		}
		
	}
	
	private void updateCarer(){
		String[] carerInfo = dbUtil.getTodayCarer(roomNumber);
		if(carerInfo!=null){
			carerId = carerInfo[0];
			carerName = carerInfo[1];
		}else{
			carerId = prefer.getCarerId();
			carerName = prefer.getCarerName();
		}
//		carerId = String.valueOf(System.currentTimeMillis());
//		carerName =  String.valueOf(System.currentTimeMillis());
		if(carerId==null||carerId.isEmpty()){
			prefer.setCarerId("0");
			prefer.setCarerName("Œ¥…Ë÷√");
		}
		else{
			prefer.setCarerId(carerId);
			prefer.setCarerName(carerName);
		}
	}
	
}
