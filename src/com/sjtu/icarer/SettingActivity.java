package com.sjtu.icarer;

import java.util.HashMap;
import java.util.List;

import com.sjtu.icarer.common.config.Prefer;
import com.sjtu.icarer.common.constant.Constants;
import com.sjtu.icarer.common.utils.DBUtil;
import com.sjtu.icarer.common.utils.OpUtil;
import com.sjtu.icarer.thread.PackageUpdateThread;
import com.sjtu.icarer.thread.CrashHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class SettingActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	private static final String TAG = "SettingActivity";
	private boolean preferenceChangedFlag = false;
	private ListPreference building_lp;
	private ListPreference floor_lp;
	private ListPreference room_lp;
	private ListPreference carer_lp;
	private EditTextPreference gero_ep;
	private Prefer prefer;
	private String GERO_ID;
	
	private List<String> longroomlist;
	//element in longroomlist has four parts(BUILDING_NO - FLOOR_NO - ROOM_NO - BED_NO)
	private String longRoomNumber;
	//longRoomNumber split to following 3 parts(BUILDING_NO-FLOOR_NO-ROOM_NO)
	private String BUILDING_NO;
	private String FLOOR_NO;
	private String ROOM_NO;
	
	private String[] carerNameList;
	private String[] carerIdList;
	
	private DBUtil dbu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		addPreferencesFromResource(R.xml.preference);
		gero_ep = (EditTextPreference) findPreference("gero_setting");
		building_lp = (ListPreference) findPreference("building_setting");
		floor_lp = (ListPreference) findPreference("floor_setting");
		room_lp = (ListPreference)findPreference("room_setting");
		carer_lp = (ListPreference) findPreference("carer_setting");
		
//		set to inflate its list
//		building_lp.setOnPreferenceClickListener(this); 
		dbu = new DBUtil(this);
		
		//自己写的用于执行相应操作的类
		prefer = new Prefer(this);
		GERO_ID = prefer.getGeroId();
		
		if(GERO_ID!=null&&!GERO_ID.isEmpty()){
			//动态设置ListPreference,必须在oncreate里实现，在onclick时候实现会按照原来的array来填充，第二次onclick才生效
	           inflateCarerList();
	           inflateRoomBegin();
		}
		
	}
	@Override  
	protected void onResume() {  
	    super.onResume();  
	    getPreferenceScreen().getSharedPreferences().
	    	registerOnSharedPreferenceChangeListener( this ); 
	    gero_ep.setSummary(prefer.getGeroId());  
        carer_lp.setSummary(prefer.getCarerName());
	}

	private void inflateCarerList(){
		List<HashMap<String,String>> carerList = dbu.getAllCarer();
		if(carerList==null){
			return;
		}
    	carerNameList = new String[carerList.size()];
        carerIdList = new String[carerList.size()];
        for(int c=0; c<carerList.size(); c++) {
        	carerNameList[c] = carerList.get(c).get("carerName");
        	carerIdList[c] = carerList.get(c).get("carerId");
        }
        setListPreferenceData(carer_lp,carerNameList,carerIdList);
	}
	private void inflateRoomBegin(){
		longroomlist = dbu.getAllRoom();
		String[] buildinglist = OpUtil.filterBuilding(longroomlist, "");
		
		longRoomNumber = prefer.getRoomNumber();
		if (longRoomNumber!=null&&!longRoomNumber.isEmpty()){
			String[] slice = longRoomNumber.split("-");
			if(slice.length==3){
				BUILDING_NO = slice[0];
				FLOOR_NO = slice[1];
				ROOM_NO = slice[2];
				building_lp.setSummary("楼栋号: "+BUILDING_NO);
				floor_lp.setSummary("楼层号: "+FLOOR_NO);
				room_lp.setSummary("房间号: "+ROOM_NO);
				String[] floorlist = OpUtil.filterFloor(longroomlist, BUILDING_NO, "");
				String[] roomlist = OpUtil.filterRoom(longroomlist, BUILDING_NO, FLOOR_NO, "");
				setListPreferenceData(building_lp,buildinglist,buildinglist );
				setListPreferenceData(floor_lp,floorlist,floorlist );
				setListPreferenceData(room_lp,roomlist,roomlist );
			}else if(slice.length==1){
				String[] roomlist = OpUtil.listToArray(longroomlist);
				setListPreferenceData(room_lp,roomlist,roomlist );
				ROOM_NO = longRoomNumber;
				building_lp.setEnabled(false);
				floor_lp.setEnabled(false);
			}else{
				cancelRoomRelatedSetting();
			}
		}else{
			floor_lp.setEnabled(false);
			room_lp.setEnabled(false);
			setListPreferenceData(building_lp,buildinglist,buildinglist );
		}
	}
	protected void setListPreferenceData(ListPreference listPreference,CharSequence[] entries, CharSequence[] entryValues) {
        
        listPreference.setEntries(entries);
//        listPreference.setDefaultValue(null);
        listPreference.setEntryValues(entryValues);
        
	}
//	@Override
//	public boolean onPreferenceClick(Preference preference){
//		return false;
//	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		preferenceChangedFlag = true;
		if(key.equals("gero_setting")){
			String gero_id = gero_ep.getText();
			gero_ep.setSummary(gero_id);
			carer_lp.setSummary(null);
			prefer.setCarerId(null);
			prefer.setCarerName(null);
			if(gero_id!=null&&!gero_id.isEmpty()){
				prefer.setGeroId(gero_id);
				
//				inflateCarerList();
			}
			cancelRoomRelatedSetting();
			//inflation will take effect only re-oncreate, so start activity again
			Intent intent = new Intent(this, SettingActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
		else if(key.equals("building_setting")){
			floor_lp.setEnabled(true);
			room_lp.setEnabled(false);
			BUILDING_NO  = building_lp.getValue();
			if(BUILDING_NO !=null&&!BUILDING_NO.isEmpty()){
				prefer.setRoomNumber(null);
				building_lp.setSummary("楼栋号: "+BUILDING_NO);
				floor_lp.setSummary(null);
				room_lp.setSummary(null);
				FLOOR_NO= null;
				ROOM_NO = null;
				String[] floorlist = OpUtil.filterFloor(longroomlist, BUILDING_NO, "");
				setListPreferenceData(floor_lp,floorlist,floorlist );
			}
		}
		else if(key.equals("floor_setting")){
			room_lp.setEnabled(true);
			FLOOR_NO = floor_lp.getValue();
			if(FLOOR_NO !=null&&!FLOOR_NO .isEmpty()){
				prefer.setRoomNumber(null);
				floor_lp.setSummary("楼层号: "+FLOOR_NO);
				room_lp.setSummary(null);
				ROOM_NO = null;
				String[] roomlist = OpUtil.filterRoom(longroomlist, BUILDING_NO, FLOOR_NO, "");
				setListPreferenceData(room_lp,roomlist,roomlist );
			}
			
		}
		else if(key.equals("room_setting")){
			ROOM_NO = room_lp.getValue();//room_no is integrated now
			if(ROOM_NO!=null&&!ROOM_NO.isEmpty()){
				longRoomNumber = BUILDING_NO+"-"+FLOOR_NO+"-"+ROOM_NO;
				room_lp.setSummary("房间号: "+ROOM_NO);
				prefer.setRoomNumber(longRoomNumber);
				
			}
			
		}
		else if(key.equals("carer_setting")){
			CharSequence carer_name = carer_lp.getEntry();
			CharSequence carer_id = carer_lp.getValue();
			carer_lp.setSummary(carer_name);
			prefer.setCarerId(carer_id.toString());
			prefer.setCarerName(carer_name.toString());
		}
        
	}  
	@Override  
    protected void onPause() {  
        super.onPause();  
        getPreferenceScreen().getSharedPreferences().
        	unregisterOnSharedPreferenceChangeListener( this );  
    }  

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		
		String key=preference.getKey();

		if(key.equals("download")){
			PackageUpdateThread mPackageUpdateThread = new PackageUpdateThread(this);  
			mPackageUpdateThread.checkUpdateInfo();
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
//	@Override
//	public boolean onPreferenceClick(Preference preference) {
//		// TODO Auto-generated method stub
//		Log.d(TAG,"d1");
//		if(preference==building_lp){
//			String tmp[] = {"A","B"};
//			setListPreferenceData(building_lp, tmp,tmp);
//		}
//		return false;
//	}
	
	
	 private void cancelRoomRelatedSetting(){
		 Editor medit = getPreferenceScreen().getSharedPreferences().edit();
//		 medit.clear();
		 medit.remove("building_setting");
		 medit.remove("floor_setting");
		 medit.remove("room_setting");
		 medit.commit();
//		 ((PreferenceGroup)findPreference("basic_setting")).removePreference(building_lp);
//		 getPreferenceScreen().
//		 ((PreferenceGroup)findPreference("basic_setting")).removePreference(floor_lp);
//		 ((PreferenceGroup)findPreference("basic_setting")).removePreference(room_lp);
		 floor_lp.setEnabled(false);
		 room_lp.setEnabled(false);
		 building_lp.setSummary(null);
		 floor_lp.setSummary(null);
		 room_lp.setSummary(null);
		 BUILDING_NO = null;
		 FLOOR_NO = null;
		 ROOM_NO =null;
		 longRoomNumber = null;
		 prefer.setRoomNumber(null);
	 }
	 private boolean preferenceFinished(){
		 String carerid = prefer.getCarerId();
		 if(ROOM_NO==null || ROOM_NO.isEmpty()
				 || carerid==null || carerid.isEmpty()){
			 return false;
		 }else{
			 return true;
		 }
	 }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	if(preferenceFinished()){
	    		if(!preferenceChangedFlag){
	    			finish();
	    			return false;
	    		}
	    		new AlertDialog.Builder(this)
				.setMessage("保存修改")
				.setPositiveButton("确认提交", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss(); 
						Intent intent = new Intent(Constants.ACTION_UPDATE_INFO);
		        		sendBroadcast(intent);
		        		finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
				    @Override
					public void onClick(DialogInterface dialog, int id) {  
				    	dialog.dismiss();  
					}  
				})
				.show();
	    	}else{
	    		new AlertDialog.Builder(this)
				.setMessage("信息仍不完整，退出可能导致信息丢失")
				.setPositiveButton("强制退出", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss(); 
						System.exit(0);
					}
				})
				.setNegativeButton("继续修改", new DialogInterface.OnClickListener() {  
				    @Override
					public void onClick(DialogInterface dialog, int id) {  
				    	dialog.dismiss();  
					}  
				})
				.show();
	    	}
	    	
	        return false;
	    }
	    return false;
	}
	 
}
