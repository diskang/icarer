package com.sjtu.icarer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sjtu.icarer.common.config.Mapping;
import com.sjtu.icarer.common.config.Prefer;
import com.sjtu.icarer.common.config.URLs;
import com.sjtu.icarer.common.constant.Const;
import com.sjtu.icarer.common.utils.DBUtil;
import com.sjtu.icarer.common.utils.OpUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentElder extends Fragment{ 
	
	Context mcontext;
	DisplayImageOptions options;
	
	private final static String TAG = "ElderService";
	public static final int INDEX = 2;
	
	private List<String> personItemList;
	private String[] elderNameList;
	private String[] elderIdList;
	private int finishedChosenElder=0;
	private String[][] finishedElderItem ;

	private DBUtil dbUtil ;
	private Prefer prefer;
	private String roomNumber;
	private String carerName;
	private String carerId;
	
	private GridView elderView;
	private GridView ItemView;
	
	private Button SubmitButton;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
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
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_elder_service, container, false);
		elderView = (GridView) rootView.findViewById(R.id.elder_list);
	    ItemView = (GridView) rootView.findViewById(R.id.elder_service_items);
	    SubmitButton = (Button) rootView.findViewById(R.id.elder_confirm_submit);
		
	    elderView.setAdapter(new ElderImageAdapter(animateFirstListener));
		elderView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBox checkbox =  (CheckBox) view.findViewById(R.id.ItemCheck);
            	if(position!=finishedChosenElder){
            		if(position<elderNameList.length){
            			CheckBox oldCheckbox = (CheckBox)elderView.getChildAt(finishedChosenElder).findViewById(R.id.ItemCheck);
            			oldCheckbox.setChecked(false);
            			checkbox.setChecked(true);
            			//refresh elder items
            			refreshItemsByElder(position);
            			finishedChosenElder = position;
            		}else{
            			Toast.makeText(mcontext, "请选择一个老人", Toast.LENGTH_SHORT).show();
            		}
            	}
			}
		});
		ItemView.setAdapter(new ItemImageAdapter(animateFirstListener));
		ItemView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBox checkbox =  (CheckBox) view.findViewById(R.id.ItemCheck);
//            	if(elderIdList==null||elderIdList.length==0){
//			    		Toast.makeText(mcontext, "不存在老人!", Toast.LENGTH_SHORT).show();
//			    		return;
//			    }
            	if(!finishedElderItem[finishedChosenElder][position].isEmpty()) {
            		checkbox.setChecked(false);
            		finishedElderItem[finishedChosenElder][position] = "";	
            	}else {
                    String fitem = personItemList.get(position);
                    checkbox.setChecked(true);
                    finishedElderItem[finishedChosenElder][position] = fitem;
            	}
			}
		});
		initFirstCheckbox();    
	    initBottomBar();
	    if(finishedElderItem!=null &&elderIdList!=null&&elderIdList.length>0){
	    	refreshItemsAtStart();
	    }
		return rootView;
		
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
		elderNameList = prefer.getElderNameList();
		elderIdList = prefer.getElderIdList();
		personItemList = prefer.getItemElderList();
		if(elderIdList==null||elderIdList.length==0||personItemList==null||personItemList.size()==0){
			return;
		}
		finishedElderItem = new String[elderNameList.length][personItemList.size()];
		for(int i=0; i<elderNameList.length; i++) {
			for(int j=0; j<personItemList.size(); j++) {
	            finishedElderItem[i][j] = "";
	        }
    	}
	
	}
	private void initFirstCheckbox(){
		//http://www.cnblogs.com/Couch-potato/archive/2012/12/11/2813460.html
		//set first item as default
        // TODO not good, should rewrite adapter
		Handler handler = new Handler(); 
        handler.postDelayed(new Runnable() {            
            public void run() {
            	if (elderView==null||elderIdList==null||elderIdList.length==0){
            		Log.d(TAG,"no view");
            		return;
            	}
		    	View elderGrid = elderView.getChildAt(finishedChosenElder);//- elderGridView.getFirstVisiblePosition()
				if(elderGrid!=null){
					
					((CheckBox)elderGrid.findViewById(R.id.ItemCheck)).setChecked(true);
				}else{
					Log.d(TAG,"null"+finishedChosenElder);
				}
            }
        }, 500);
     
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
     			    	if(elderIdList==null||elderIdList.length==0){
     			    		Toast.makeText(mcontext, "不存在老人!", Toast.LENGTH_SHORT).show();
     			    		return;
     			    	}
     			    	if(personItemList==null||personItemList.size()==0){
     			    		Toast.makeText(mcontext, "不存在项目!", Toast.LENGTH_SHORT).show();
     			    		return;
     			    	}
     			    	String[] elderItem = new String[elderNameList.length];
     			    	for(int q=0; q<elderNameList.length; q++) {
     			    		elderItem[q] = OpUtil.dealItem(finishedElderItem[q], elderIdList[q]);
     			    	}
     			    	Toast.makeText(mcontext, "正在提交", Toast.LENGTH_SHORT).show();
     			    	Boolean success = dbUtil.uploadItem(carerId, roomNumber,"", "", elderItem);
     			    	
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
	private void refreshItemsAtStart(){
		//called at initial view
    	for(int i=0;i<personItemList.size();i++){
			String itemFinish = finishedElderItem[finishedChosenElder][i];
			if(!itemFinish.isEmpty()){
				View v = ItemView.getChildAt(i);
				CheckBox checkbox = (CheckBox)v.findViewById(R.id.ItemCheck);
				checkbox.setChecked(true);
			}
		}
		
	}
	private void refreshItemsByElder(int elderIndex){
		//must be called before finished array changed
		for(int i=0;i<personItemList.size();i++){
			String itemFinish = finishedElderItem[elderIndex][i];
			boolean checkBool = true;
			if(itemFinish.isEmpty()){
				checkBool = false;
			}
			//no need to update if same with initial view
			if(itemFinish!=finishedElderItem[finishedChosenElder][i]){
//				CheckBox checkbox = (CheckBox)serviceGridView.getChildAt(i).findViewById(R.id.ItemCheck);
				View v = ItemView.getChildAt(i);
				CheckBox checkbox = (CheckBox)v.findViewById(R.id.ItemCheck);
				checkbox.setChecked(checkBool);
			}
		}
		
	}
	

	private static class ViewHolder {
		CheckBox checkbox;
		ImageView image;
		TextView text;
	}
	class ElderImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private ImageLoadingListener animateFirstListener ;

		ElderImageAdapter(ImageLoadingListener mAnimateFirstListener) {
			inflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {
			if(elderIdList==null){
				return 0;
			}
			return elderIdList.length;
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
				view = inflater.inflate(R.drawable.item_elder, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.ItemText);
				holder.image = (ImageView) view.findViewById(R.id.ItemImage);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.text.setText(elderNameList[position]);
            String pic_uri = URLs.IMG_URL+"?elder_id="+elderIdList[position];

			ImageLoader.getInstance().displayImage(pic_uri, holder.image, options, animateFirstListener);

			return view;
		}
	}
	class ItemImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private ImageLoadingListener animateFirstListener;

		ItemImageAdapter(ImageLoadingListener mAnimateFirstListener) {
			animateFirstListener = mAnimateFirstListener;
			inflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {
			if(personItemList==null){
				return 0;
			}
			return personItemList.size();
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
				view = inflater.inflate(R.drawable.item, parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) view.findViewById(R.id.ItemImage);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

//			holder.text.setText("Item " + (position + 1));
			String itemName = personItemList.get(position);
            String pic_uri = Mapping.ElderItemMapping.get(itemName) ;
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
    
}
