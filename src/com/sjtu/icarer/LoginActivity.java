package com.sjtu.icarer;


import java.util.HashMap;
import java.util.List;

import com.sjtu.icarer.common.config.Prefer;
import com.sjtu.icarer.common.constant.Const;
import com.sjtu.icarer.common.utils.DBUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private SharedPreferences preferences = null;
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	private TextView roominfo;
	private TextView elderinfo;
	private WifiManager wifi;
	private WifiInfo info;
	
	
	private String[] roomlist;
	private String elder = "";
	private String macaddress;
	private DBUtil dbu;
	private Prefer prefer ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		info = wifi.getConnectionInfo();
		macaddress = info.getMacAddress();
		
		dbu = new DBUtil(this);
		prefer = new Prefer(this);
		List<String> list = dbu.getAllRoom();
		roomlist = new String[list.size()];
		list.toArray(roomlist);
		for(int i=0; i<list.size(); i++) {
			roomlist[i] = roomlist[i].split("-")[0];
		}
		
		
		spinner = (Spinner) findViewById(R.id.room);
		adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, roomlist);
		//adapter = ArrayAdapter.createFromResource(this, R.array.roomnumlist, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new myOnItemSelectedListener());
		
		roominfo = (TextView) findViewById(R.id.inf_roomnumber);
		elderinfo = (TextView) findViewById(R.id.inf_roompeople);
	}
	
	class myOnItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			elder = "";
			String room = roomlist[position];
			List<HashMap<String,String>> elderlist = dbu.getELder(room);
			for (int i = 0; i < elderlist.size(); i++) {
				elder += elderlist.get(i).get("elderName")+"\n";
			}
			roominfo.setText("房间号:"+room+"\n");
			elderinfo.setText(elder);
			
		}
		public void onNothingSelected(AdapterView<?> arg0){
			//
		}
	}
	
	public void click(View view) {
		//本地存储房间号与登录状态
//		preferences = getSharedPreferences("localinf",Context.MODE_PRIVATE);
//		SharedPreferences.Editor editor = preferences.edit();
//		editor.putString("roomNumber", roomlist[spinner.getSelectedItemPosition()]);
//		editor.commit();
		String roomNumber = roomlist[spinner.getSelectedItemPosition()];
		if(prefer.getRoomNumber()!=roomNumber){
			prefer.setRoomNumber(roomNumber);
		}
		
		
		//TODO 绑定MAC
		String um = dbu.uploadMAC(Const.GERO_ID, spinner.getSelectedItem().toString().substring(0, 5), macaddress);
		Log.d("upload", spinner.getSelectedItem().toString().substring(0, 5)+um);
		
		//跳转至主页面
//		Toast.makeText(this, "正在为您跳转", Toast.LENGTH_LONG).show();
//		new Handler().postDelayed(new Runnable() {
//			public void run() {
//				Intent intent = new Intent(LoginActivity.this, RoomActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				LoginActivity.this.startActivity(intent);
//				LoginActivity.this.finish();
//			}
//		}, 2000);
		Intent intent = new Intent(Const.ACTION_UPDATE_INFO);
		sendBroadcast(intent);
		this.finish();
	}
//	@Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.logout, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//    	int id = item.getItemId();
//        
//        switch(id) {
//        case R.id.chooseCarer:
//        	
//        	new AlertDialog.Builder(this)
//    		.setTitle("请选择护工")
//    		.setItems(carerNameList, new DialogInterface.OnClickListener() {  
//    			@Override
//    			public void onClick(DialogInterface dialog, int which) {
//    				dialog.dismiss();
//					
//					preferences = getSharedPreferences("localinf",Context.MODE_PRIVATE);
//			    	SharedPreferences.Editor editor = preferences.edit();
//			    	editor.putString("carerName", carerNameList[which]);
//			    	editor.putString("carerId", carerIdList[which]);
//					editor.commit();
////					refresh();
//				}  
//    		})
//    		.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
//			    @Override
//    			public void onClick(DialogInterface dialog, int id) {  
//			    	dialog.dismiss();
//    			}  
//    		})
//    		.show();
//        	break;
//        case R.id.logout_room:
//        	//确认对话框
//        	new AlertDialog.Builder(this)
//        		.setTitle("退出房间")
//        		.setMessage("确定退出房间么？")
//        		.setPositiveButton("确认", new confirmListener())
//        		.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
//    			    @Override
//        			public void onClick(DialogInterface dialog, int id) {  
//    			    	dialog.dismiss();
//        			}  
//        		})
//        		.show();	
//        	break;
//        default:
//        	break;
//        }
//    		
//        return super.onOptionsItemSelected(item);
//    }
//    
//    class confirmListener implements DialogInterface.OnClickListener {
//    	@Override
//    	public void onClick(DialogInterface dialog, int id) {
//    		//初始化本地数据
//	    	preferences = getSharedPreferences("localinf",Context.MODE_PRIVATE);
//	    	SharedPreferences.Editor editor = preferences.edit();
//	    	editor.putString("roomNumber", "");
//			
//			editor.putString("carerName", "");
//			editor.putString("carerId", "");
//			editor.commit();
			//跳转回登陆页面
//			new Handler().postDelayed(new Runnable() {
//				public void run() {
//					Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
//					LoginActivity.this.startActivity(intent);
//					LoginActivity.this.finish();
//				}
//			}, 500);
//    	}
//    }
    
}
