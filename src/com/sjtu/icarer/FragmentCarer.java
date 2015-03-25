package com.sjtu.icarer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;








import com.sjtu.icarer.common.config.Prefer;
import com.sjtu.icarer.common.constant.Const;
import com.sjtu.icarer.common.utils.ClsUtils;
import com.sjtu.icarer.common.utils.DBUtil;
import com.sjtu.icarer.model.RunningDevice;
import com.sjtu.icarer.thread.BluetoothSocketThread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentCarer extends Fragment{
	private final static String TAG = "RoomService";
	public static final int INDEX = 3;
	private Context mcontext;
	private BluetoothReceiver bluetoothReceiver;
	public BluetoothAdapter mBluetoothAdapter;
	
	private String[] itemArray = {"ECG24小时测量","手环"};
	private String[] elderArray = null;
	List<String> roomList = new ArrayList<String>();
	List<RunningDevice> runningDevice = new ArrayList<RunningDevice>();	//存放当前房间已经绑定的设备对象，从数据库中取得，并非从蓝牙适配器中取得
	List<String> listingDevice = new ArrayList<String>();		//存放当前房间寻找到的设备的mac地址信息，防止多次点击搜索设备后重复添加设备配对信息
	List<String> runningDeviceAddress = new ArrayList<String>();
	
	List<HashMap<String,String>> elderInfoList = new ArrayList<HashMap<String,String>>();
	List<HashMap<String,String>> bluetoothList = new ArrayList<HashMap<String,String>>();
	private List<BluetoothDevice> bondedDeviceList = new ArrayList<BluetoothDevice>();	//存放当前pad中已经配对的设备，从蓝牙适配器中获得
	private List<BluetoothDevice> searchedDeviceList = new ArrayList<BluetoothDevice>();
	
	private DBUtil dbUtil ;
	private Prefer prefer;
	private String roomNumber;
	private int foundDeviceFlag = 0; 
	
	private View rootView;
	private GetCurrentDeviceThread mGetCurrentDeviceThread = new GetCurrentDeviceThread();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mcontext = getActivity().getApplicationContext();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectAll().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
		
		dbUtil = new DBUtil(mcontext);
		prefer = new Prefer(mcontext);
		roomNumber = prefer.getRoomNumber();
		if(roomNumber==null ){
			//jump to login
			Intent intent_setting = new Intent(mcontext, SettingActivity.class);
			this.startActivity(intent_setting);
			return;
		}
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		bluetoothReceiver=new BluetoothReceiver(); 
		mcontext.registerReceiver(bluetoothReceiver, filter); 
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
        mcontext.unregisterReceiver(bluetoothReceiver);  
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fr_carer_service, container, false);
		Button searchBtn = (Button) rootView.findViewById(R.id.searchBtn);
		searchBtn.setOnClickListener(searchOnClickListener);

//		initDevice();
		return rootView;
	}
	private Handler mHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg){
			Log.d(TAG,"begin to handle msg");
			if(msg.what==3){//选择房间事件
				Log.d("bowen","about to change elderlist");
				String selectedRoom = roomNumber;
				elderInfoList = dbUtil.getELder(selectedRoom);
			}else if(msg.what ==4){
				Toast.makeText(mcontext, "网络链接失败", 50000).show();
			}else if(msg.what == 5){
			}else if(msg.what ==12){
				Log.d("bowen","begin to handle msg 12");
				LinearLayout mLayout = (LinearLayout) rootView.findViewById(R.id.setting_layout);
				 
				int lastDeviceIndex =bluetoothList.size()-1;
				
				
				LinearLayout.LayoutParams lp_1 = new LinearLayout.LayoutParams( 
		                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				LayoutInflater inflater3 = LayoutInflater.from(getActivity());
				View view_1 = inflater3.inflate(R.layout.match_util, null);
				view_1.setTag(bluetoothList.get(lastDeviceIndex).get("deviceAddress"));
				TextView deviceTV = (TextView) view_1.findViewById(R.id.device_tv);
				deviceTV.setText(bluetoothList.get(lastDeviceIndex).get("deviceName"));
				
				Spinner elderSpinner = (Spinner) view_1.findViewById(R.id.elderspinner);
				elderArray = getElderNameArray();
				ArrayAdapter<String> _elderAdapter = new ArrayAdapter<String>(view_1.getContext(),android.R.layout.simple_spinner_item,elderArray);
				elderSpinner.setAdapter(_elderAdapter);
				
				Spinner itemSpinner = (Spinner) view_1.findViewById(R.id.itemspinner);
				ArrayAdapter<String> _itemAdapter = new ArrayAdapter<String>(view_1.getContext(),android.R.layout.simple_spinner_item,itemArray);
				itemSpinner.setAdapter(_itemAdapter);
				
				Button bindBthBtn = (Button) view_1.findViewById(R.id.bindBthBtn);
				bindBthBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						String strPwd = "1234";
						LinearLayout ply = (LinearLayout) v.getParent();
						String tagOfMac = ply.getTag().toString();
						Toast.makeText(mcontext, tagOfMac, 5000).show();
						BluetoothDevice device = getBluetoothDeviceFromBondedListByMac(tagOfMac,searchedDeviceList);
						
						Log.d("Bowen Xie",device.getAddress());			
						Log.d("Bowen Xie",device.getName());
						Log.d("Bowen Xie",String.valueOf(device.getBondState()));
//						Toast.makeText(MainActivity.this, device.getName(), 5000).show();
						if(device.getBondState() == BluetoothDevice.BOND_NONE){	
							Toast.makeText(mcontext, device.getName()+"not bonded",Toast.LENGTH_LONG).show();
							try{
								ClsUtils.setPin(device.getClass(), device, strPwd);
								ClsUtils.createBond(device.getClass(), device);
								ClsUtils.cancelPairingUserInput(device.getClass(), device);
							}
							catch(Exception e){
								e.printStackTrace();
							}
						}
					}
				});

				Button bindBtn = (Button) view_1.findViewById(R.id.bindBtn);
				bindBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						
						LinearLayout ply =(LinearLayout) v.getParent();
						Spinner elderSpinner = (Spinner) ply.findViewById(R.id.elderspinner);
						String elder_name = elderSpinner.getSelectedItem().toString();
						int elder_id = getElderIdByName(elder_name,elderInfoList);
						
						String macAddress = ply.getTag().toString();
						TextView deviceTV = (TextView) ply.findViewById(R.id.device_tv);
						String deviceName = deviceTV.getText().toString();
						
						Spinner typeSpinner = (Spinner) ply.findViewById(R.id.itemspinner);
						String type = typeSpinner.getSelectedItem().toString();
						
						boolean bindFlag = dbUtil.bindBluetoothDevice(macAddress, elder_id, deviceName, type);
						
						if(bindFlag == true){
						/***************************
						 * 
						 * 绑定成功后需要在当前状态的列表中添加刚绑定的设备的信息
						 * 
						 *****************************/
							LinearLayout rootLy = (LinearLayout) ply.getParent();
							LinearLayout mLayout = (LinearLayout) rootLy.findViewById(R.id.current_devices);
							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
							
							LayoutInflater inflater = LayoutInflater.from(getActivity());
							View view = inflater.inflate(R.layout.current_device_util,null);
							view.setLayoutParams(lp);
							
							TextView deviceNameTV = (TextView) view.findViewById(R.id.deviceName);
							deviceNameTV.setText(deviceName);
							deviceNameTV.setTag(macAddress);
							
							TextView elderNameTV = (TextView) view.findViewById(R.id.elderName);
							elderNameTV.setText(elder_name);
							elderNameTV.setTag(elder_id);
							
							TextView itemNameTV = (TextView) view.findViewById(R.id.itemName);
							itemNameTV.setText(type);
							
							Button unBindBtn = (Button) view.findViewById(R.id.unbindBtn);
							Button startBtn = (Button) view.findViewById(R.id.startBtn);
							//unBindBtn.setOnClickListener(new );
							
							unBindBtn.setOnClickListener(new OnClickListener(){
								@Override
								public void onClick(View v){
									LinearLayout ply = (LinearLayout) v.getParent();
									TextView deviceNameTV = (TextView) ply.findViewById(R.id.deviceName);
									TextView elderNameTV = (TextView) ply.findViewById(R.id.elderName);
									String deviceAddress = deviceNameTV.getTag().toString();
									String localElderId = elderNameTV.getTag().toString();
									try{
										int elder_id = Integer.parseInt(localElderId);
										if(dbUtil.unBindBluetoothDevice(deviceAddress, elder_id)){
											ply.setVisibility(View.GONE);
										}
										else{
											Toast.makeText(mcontext, "网络连接失败", 5000).show();
										}
									}catch(Exception e){
										Toast.makeText(mcontext, "操作异常", 3000).show();
									}
								}
							});
							
							startBtn.setOnClickListener(new OnClickListener(){
								@Override
								public void onClick(View v){
									LinearLayout ply =(LinearLayout) v.getParent();
									TextView deviceTV = (TextView) ply.findViewById(R.id.deviceName);
									String localAddress = deviceTV.getTag().toString();
									
									BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(localAddress);
									BluetoothSocketThread localSocketThread = new BluetoothSocketThread(device);
									
									//localSocketThread.start();
									new Thread(localSocketThread).start();
								}
							});
							
							
							mLayout.addView(view);
							
						/*****************************************
						 * 
						 * 添加信息后还在搜索到的设备列表中讲其信息去掉
						 * 
						 *****************************************/
							
							ply.setVisibility(View.GONE);
							
						}else{
							Toast.makeText(mcontext, "绑定失败", 3000).show();
						}
					}
				});

				view_1.setLayoutParams(lp_1);
				
				mLayout.addView(view_1);
			}else if(msg.what ==13){
				Log.d("bowen","begin to handle msg 13");
				LinearLayout mLayout = (LinearLayout) rootView.findViewById(R.id.setting_layout);
				
				if(foundDeviceFlag==0){
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( 
			                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					LayoutInflater inflater1 = LayoutInflater.from(getActivity());
					View view = inflater1.inflate(R.layout.match_title, null);
					view.setLayoutParams(lp);
					
					Log.d("bowen","before getting the view by id");
					
					mLayout.addView(view);
					foundDeviceFlag=1;
					
					Log.d("bowen","get the view by id success");
				}
			}
		}
	};
	
	private void initDevice(){
		runningDevice = dbUtil.getCurrentDevicesByRoom(roomNumber);
		
		mGetCurrentDeviceThread.start();
//		getRoomListThread.start();
		
		int currentDeviceAmount = runningDevice.size();
		LinearLayout currentDeviceLayout = (LinearLayout) rootView.findViewById(R.id.current_devices);
		
		for(int i = 0;i<currentDeviceAmount;i++){
			runningDeviceAddress.add(runningDevice.get(i).DeviceMac);
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( 
	                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			LayoutInflater inflater1 = LayoutInflater.from(getActivity());
			View view = inflater1.inflate(R.layout.current_device_util, null);
			view.setLayoutParams(lp);
			TextView deviceNameTV = (TextView) view.findViewById(R.id.deviceName);
			deviceNameTV.setText(runningDevice.get(i).DeviceName);
			deviceNameTV.setTag(runningDevice.get(i).DeviceMac);
			
			TextView elderNameTV = (TextView) view.findViewById(R.id.elderName);
			int localElderId = Integer.parseInt(runningDevice.get(i).ElderId);
			String elderNameStr = getElderNameById(localElderId,elderInfoList);
			elderNameTV.setTag(localElderId);
			elderNameTV.setText(elderNameStr);
			
			TextView itemNameTV = (TextView) view.findViewById(R.id.itemName);
			itemNameTV.setText(runningDevice.get(i).Type);
			
			Button unBindBtn = (Button) view.findViewById(R.id.unbindBtn);
			Button startBtn = (Button) view.findViewById(R.id.startBtn);
			
			unBindBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					LinearLayout ply = (LinearLayout) v.getParent();
					TextView deviceNameTV = (TextView) ply.findViewById(R.id.deviceName);
					TextView elderNameTV = (TextView) ply.findViewById(R.id.elderName);
					String deviceAddress = deviceNameTV.getTag().toString();
					String localElderId = elderNameTV.getTag().toString();
					try{
						int elder_id = Integer.parseInt(localElderId);
						if(dbUtil.unBindBluetoothDevice(deviceAddress, elder_id)){
							ply.setVisibility(View.GONE);
						}
						else{
							Toast.makeText(mcontext, "网络连接失败", 5000).show();
						}
					}catch(Exception e){
						Toast.makeText(mcontext, "操作异常", 3000).show();
					}
				}
			});
			

			startBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					LinearLayout ply =(LinearLayout) v.getParent();
					TextView deviceTV = (TextView) ply.findViewById(R.id.deviceName);
					String localAddress = deviceTV.getTag().toString();
					TextView elderTV = (TextView) ply.findViewById(R.id.elderName);
					int localElderId = Integer.parseInt(elderTV.getTag().toString());
					TextView itemTV = (TextView) ply.findViewById(R.id.itemName);
					String localItemName = itemTV.getText().toString();
					
					BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(localAddress);
					BluetoothSocketThread localSocketThread = new BluetoothSocketThread(device);
					localSocketThread.elderId = localElderId;
					localSocketThread.roomNo = roomNumber;
					localSocketThread.type = localItemName;
					//localSocketThread.start();
					new Thread(localSocketThread).start();
				}
			});

			currentDeviceLayout.addView(view);
		}
	}
	
	private class GetCurrentDeviceThread extends Thread{
		@Override
		public void run(){
			Message msg = new Message();
			Log.d("bowen","getCurrentDeviceThread begin");			
		}
	}
	
	
	public String[] getElderNameArray(){
		String[] localElderArray = {};
		List<String> localElderList = new ArrayList<String>();
		
		int elderAmount = elderInfoList.size();
		for(int i=0;i<elderAmount;i++){
			localElderList.add(elderInfoList.get(i).get("elderName"));
		}
		
		localElderArray = localElderList.toArray(new String[elderAmount]);		
		return localElderArray;
	}
	
	public class BluetoothReceiver extends BroadcastReceiver{
		String strPwd="1234";
		@Override		
		public void onReceive(Context context, Intent intent) {			
			// TODO Auto-generated method stub	
			Log.d("bowen","receive soom broadcast");
			String action =intent.getAction();			
			if(BluetoothDevice.ACTION_FOUND.equals(action)){				
				BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				
				HashMap<String,String> deviceInfo = new HashMap<String,String>();
				
				listingDevice.add(device.getAddress());
				deviceInfo.put("deviceAddress", device.getAddress());
				deviceInfo.put("deviceName", device.getName());
				
				if(bluetoothList.indexOf(deviceInfo)==-1 
						&& runningDeviceAddress.indexOf(deviceInfo.get("deviceAddress"))==-1) {
					bluetoothList.add(deviceInfo);
					searchedDeviceList.add(device);
					Message msg = new Message();
					msg.what = 12;//此消息通知activity在页面中添加新的设备条目
					mHandler.sendMessage(msg);
				}
				
				
				Log.d("Bowen Xie",device.getAddress());			
				Log.d("Bowen Xie",device.getName());
				Log.d("Bowen Xie",String.valueOf(device.getBondState()));
//				Toast.makeText(MainActivity.this, device.getName(), 5000).show();
				if(device.getBondState() == BluetoothDevice.BOND_NONE){	
					Toast.makeText(mcontext, device.getName()+"not bonded",5000).show();
				}
				try{
					ClsUtils.setPin(device.getClass(), device, strPwd);
					ClsUtils.createBond(device.getClass(), device);
					ClsUtils.cancelPairingUserInput(device.getClass(), device);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public View.OnClickListener searchOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v){
			BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
			BluetoothDevice device = null;
			String nameList = "";
			
			if(adapter!=null){
				adapter.cancelDiscovery();
				if(!adapter.isEnabled()){
					Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivity(intent);
					Log.d("bowen","start bluetooth activity");
				}else{
					adapter.startDiscovery();
					Log.d("Bowen Xie","正在扫描设备");
				}
				
				Message msg = new Message();
				msg.what = 13;
				mHandler.sendMessage(msg);		//添加标题�?
				
				Set<BluetoothDevice> devices=adapter.getBondedDevices();
				if(devices.size()>0){
					for(Iterator iterator = devices.iterator();iterator.hasNext();){
						device = (BluetoothDevice)iterator.next();
						bondedDeviceList.add(device);
						String name = device.getName();
						//BluetoothMsg.BlueToothAddress = device.getAddress();
						Log.d("Bowen Xie",name);
						nameList += name+"||";
//						}
					}
					nameList.substring(0,nameList.length()-2);
					Toast.makeText(mcontext, nameList,5000).show();
					
				}
			}else{
				Toast.makeText(mcontext, "当前设备没有蓝牙功能", 5000).show();
			}
		}
	};
	
	private BluetoothDevice getBluetoothDeviceFromBondedListByMac(String macAddress,List<BluetoothDevice> deviceList){
		BluetoothDevice localBth = null;
		int bondedDeviceAmount = deviceList.size();
		for(int i=0;i<bondedDeviceAmount;i++){
			if(macAddress.equals(deviceList.get(i).getAddress())){
				localBth = deviceList.get(i);
			}
		}
		
		return localBth;
	} 
	
	
	private String getElderNameById(int elder_id, List<HashMap<String,String>> elder_info_list){
		String elderName = "未知老人";
		int elderIndex = -1;
		String localElderId = ""+elder_id;
		
		for(int i=0; i<elder_info_list.size();i++){
			if(localElderId.equals(elder_info_list.get(i).get("elderId"))){
				elderIndex = i;
				break;
			}
		}
		
		if(elderIndex>-1){
			elderName = elder_info_list.get(elderIndex).get("elderName");
		}
		
		return elderName;
		
	}
	
	private int getElderIdByName(String elder_name, List<HashMap<String,String>> elder_info_list){
		int elder_id = -1;
		int elderIndex = -1;
		
		for(int i=0; i<elder_info_list.size();i++){
			if(elder_name.equals(elder_info_list.get(i).get("elderName"))){
				elderIndex = i;
				break;
			}
		}
		
		if(elderIndex>-1){
			elder_id = Integer.parseInt(elder_info_list.get(elderIndex).get("elderId"));
		}
		
		return elder_id;
	}
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.goto_login, menu);
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	int id = item.getItemId();
        
        switch(id) {
        	
        	case R.id.goto_prefer:
        		Intent intent_setting2 = new Intent(getActivity(), SettingActivity.class);
        		startActivity(intent_setting2);
	        	break;
        	case R.id.goto_refresh:
        		Intent intent = new Intent(Const.ACTION_UPDATE_INFO);
        		mcontext.sendBroadcast(intent);
        		getActivity().finish();
	        	break;
        	case R.id.goto_quit:
        		System.exit(0);
        		break;//TODO
            default:
            	break;
            }
        		
        return super.onOptionsItemSelected(item);
        
    }

}
