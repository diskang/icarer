package com.sjtu.icarer;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sjtu.icarer.common.config.Mapping;
import com.sjtu.icarer.common.config.Prefer;
import com.sjtu.icarer.common.constant.Const;
import com.sjtu.icarer.common.utils.DBUtil;
import com.sjtu.icarer.common.utils.OpUtil;
import com.sjtu.icarer.common.utils.SafeAsyncTask;
import com.sjtu.icarer.service.IcarerService;

public class FragmentRoom extends Fragment{
	Context mcontext;
	DisplayImageOptions options;
	
	private final static String TAG = "FragmentRoom"; 
	public static final int INDEX = 1;
	private List<String> roomItemList;
	private String[] finishedRoomItem;
	private String roomNumber;
	private String carerName;
	private String carerId;
	private DBUtil dbUtil ;
	private Prefer prefer;
	
	@Inject protected IcarerService icarerService;
	
	
	private GridView serviceView;
	private Button SubmitButton;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initVars();
		options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_stub)
					.showImageForEmptyUri(R.drawable.ic_empty)
					.showImageOnFail(R.drawable.ic_error)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.build();
		Injector.inject(this);
		checkRetrofit();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_room_service, container, false);
		serviceView = (GridView) rootView.findViewById(R.id.room_service_items);
		SubmitButton = (Button)rootView.findViewById(R.id.room_confirm_submit);
		((GridView) serviceView).setAdapter(new ImageAdapter());
		serviceView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBox checkbox =  (CheckBox) view.findViewById(R.id.ItemCheck);
            	//ImageView imgView = (ImageView) arg1.findViewById(R.id.ItemImage);
            	if(!finishedRoomItem[position].isEmpty()) {
            		checkbox.setChecked(false);
            		finishedRoomItem[position] = "";
            	} else {
                    String fitem = roomItemList.get(position);
                    checkbox.setChecked(true);
                    
                    finishedRoomItem[position] = fitem;
            	}
			}
		});

		initBottomBar();
		return rootView;
	}

	private void initBottomBar(){
		
		SubmitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!OpUtil.isConnected(v.getContext())){
					Toast.makeText(v.getContext(), "请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
					return;
				}
				new AlertDialog.Builder(v.getContext())
        		.setTitle("确认提交 ")
        		.setMessage("确定提交项目么？")
        		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
        			 @Override
         			public void onClick(DialogInterface dialog, int id) {  
     			    	dialog.dismiss();     
     			    		    	
     			    	if(OpUtil.dealItem(finishedRoomItem,"").equals("")) {
     			    		Toast.makeText(mcontext, "未选择项目!", Toast.LENGTH_SHORT).show();
     			    		return;
     			    	}
     			    	Toast.makeText(mcontext, "正在提交", Toast.LENGTH_SHORT).show();
     			    	Boolean success = dbUtil.uploadItem(carerId, roomNumber,
								"", OpUtil.dealItem(finishedRoomItem,""), new String[0]);
     			    	if(success) {
     			    		Toast.makeText(mcontext, "提交成功", Toast.LENGTH_SHORT).show();
     			    		Intent intent = new Intent(mcontext, HomeActivity.class);
     			    		intent.putExtra(Const.FRAGMENT_INDEX, INDEX);
     			    		startActivity(intent);
     			    	} 
     			    	else {
     			    		Toast.makeText(mcontext, "提交失败", Toast.LENGTH_SHORT).show();
     			    	}
         			} 
        		})
        		.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
    			    @Override
        			public void onClick(DialogInterface dialog, int id) {  
    			    	dialog.dismiss();
        			}  
        		})
        		.show(); 
			}
		});
	}
		
	private void initVars(){
		mcontext = getActivity().getApplicationContext();
		dbUtil = new DBUtil(mcontext);
		prefer = new Prefer(mcontext);
		roomNumber = prefer.getRoomNumber();
		carerId = prefer.getCarerId();
		carerName = prefer.getCarerName();
		
		if(roomNumber==null || carerId==null || carerName==null){
			Toast.makeText(mcontext, "请选择护工和房间", Toast.LENGTH_SHORT).show();
			Intent intent_setting = new Intent(getActivity(), SettingActivity.class);
			startActivity(intent_setting);
			return;
		}
		roomItemList = prefer.getItemRoomList();
		if(roomItemList==null||roomItemList.size()==0){
			return;
		}
		if(finishedRoomItem==null){
			finishedRoomItem = new String[roomItemList.size()];
			for(int j=0; j<roomItemList.size(); j++) {
	            finishedRoomItem[j] = "";
	        }
		}
	}
//	private void refreshCheckedItems(){
//		//refresh based on the finishedRoomItem
//		
//		Handler handler = new Handler(); 
//        handler.postDelayed(new Runnable() {            
//            public void run() {
//               
//            	for(int i=0;i<roomItemList.size();i++){
//        			View v = serviceView.getChildAt(i- serviceView.getFirstVisiblePosition());
//        			boolean checkBool = false;
//        			if(!finishedRoomItem[i].isEmpty()){
//        				checkBool=true;
//        			}
////        			Log.i(TAG,finishedRoomItem[i]+i);
//        			CheckBox checkbox = (CheckBox)v.findViewById(R.id.ItemCheck);
//        			checkbox.setChecked(checkBool);
//        		}
//            }
//        }, 1000);
//	}

	private static class ViewHolder {
		//CheckBox checkbox;
		ImageView image;
	}
	class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		ImageAdapter() {
			inflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {
			if(roomItemList==null){
				return 0;
			}
			return roomItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = inflater.inflate(R.layout.item, parent, false);
				holder = new ViewHolder();
				//holder.checkbox = (CheckBox) view.findViewById(R.id.ItemCheck);
				holder.image = (ImageView) view.findViewById(R.id.ItemImage);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

//			holder.text.setText("Item " + (position + 1));
			String itemName = roomItemList.get(position);
            String pic_uri = Mapping.RoomItemMapping.get(itemName) ;
			ImageLoader.getInstance().displayImage(pic_uri, holder.image, options, animateFirstListener);
			
			return view;
		}
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		AnimateFirstDisplayListener.displayedImages.clear();
	}
	
	private void checkRetrofit(){
	    new SafeAsyncTask<Boolean>(){

			@Override
			public Boolean call() throws Exception {
				String ssssString = icarerService.getUserKey("admin");
				Log.d(TAG, ssssString);
				return ssssString!=null;
			}
	    	
	    }.execute();
	}
}
