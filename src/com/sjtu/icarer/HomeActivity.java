package com.sjtu.icarer;



import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sjtu.icarer.common.config.Prefer;
import com.sjtu.icarer.common.config.URLs;
import com.sjtu.icarer.common.constant.Const;
import com.sjtu.icarer.common.utils.OpUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeActivity extends FragmentActivity {
	private int current_fragment_type ;//1:room  2:elder  3:carer
	private String roomNumber;
	private String carerName;
	private String carerId;
	private Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		setContentView(R.layout.activity_home);
		Prefer prefer = new Prefer(this);
		roomNumber = prefer.getRoomNumber();
		carerName = prefer.getCarerName();
		carerId = prefer.getCarerId();
		int frIndex = getIntent().getIntExtra(Const.FRAGMENT_INDEX, 1);
		addFragment(frIndex);
		updateView();

	}

	@Override
	public void onResume(){
		super.onResume();
		check_network();
	}
	
	public void onRoomServiceClick(View view){
		if(current_fragment_type!=1){
			addFragment(1);	
		}
	}
	
	public void onElderServiceClick(View view){
		if(current_fragment_type!=2){
			addFragment(2);
		}
	}
	
	public void onCarerServiceClick(View view){
		if(current_fragment_type!=3){
			addFragment(3);
		}
	}
	
	private void addFragment(int type){
		Fragment fr;
		String tag;
		int resourceId = R.id.main_fragment;
		check_network();
		switch(type){
		
		case 1:
			tag = FragmentRoom.class.getSimpleName();
			fr = getSupportFragmentManager().findFragmentByTag(tag);
			if (fr == null) {
				fr = new FragmentRoom();
			}
			break;
		case 2:
			tag = FragmentElder.class.getSimpleName();
			fr = getSupportFragmentManager().findFragmentByTag(tag);
			if (fr == null) {
				fr = new FragmentElder();
			}
			break;
		default:
			tag = FragmentCarer.class.getSimpleName();		
			fr = getSupportFragmentManager().findFragmentByTag(tag);
			if (fr == null) {
				fr = new FragmentCarer();
			}
			break;
		}
		
		getSupportFragmentManager().beginTransaction().replace(resourceId, fr, tag).commit();	 
		current_fragment_type = type;
		
	}
	
	private void check_network(){
		if(!OpUtil.isConnected(this)){
			//Toast.makeText(this, "请检查网络连接是否正常", Toast.LENGTH_LONG).show();
			display_network_prompt();
		}else{
			hide_network_prompt();
		}
	}
	private void display_network_prompt(){
		RelativeLayout layoutContainer = (RelativeLayout)findViewById(R.id.network_prompt_container);
		RelativeLayout promptLayout = (RelativeLayout)findViewById(R.id.network_prompt);
		if(promptLayout==null){
			promptLayout =(RelativeLayout) RelativeLayout.inflate(context, R.drawable.bar_network_prompt, null);
		}
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(     
                LinearLayout.LayoutParams.MATCH_PARENT,     
                50     
        );  
		layoutContainer.removeAllViews();
		layoutContainer.addView(promptLayout, params);
	}
	
	private void hide_network_prompt(){
		RelativeLayout layoutContainer = (RelativeLayout)findViewById(R.id.network_prompt_container);
		layoutContainer.removeAllViews();
	}
	
	
	public void onRightArrowClick(View view){
		Intent  mIntent= null;
		if(android.os.Build.VERSION.SDK_INT>10){
			mIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        }else{
        	mIntent = new Intent("");
        	ComponentName comp = new ComponentName(
                    "com.android.settings",
                    "com.android.settings.WirelessSettings");
            mIntent.setComponent(comp);
            mIntent.setAction("android.intent.action.VIEW");
        }
        
        startActivity(mIntent); 
	}
	private void updateView(){

		TextView roomNumView = (TextView)findViewById(R.id.room_number);
		TextView carerView = (TextView)findViewById(R.id.carer_info);
		
		roomNumView.setText("房间: \n"+roomNumber);
		carerView.setText("今日护工: \n"+carerName);
		loadCarerImage();
	}
	
	private void loadCarerImage(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.default_carer) // resource or drawable
        .showImageForEmptyUri(R.drawable.default_carer) // resource or drawable
        .showImageOnFail(R.drawable.default_carer) // resource or drawable
        .cacheInMemory(true) 
        .cacheOnDisk(true)
        .considerExifParams(true)
        .build();
		final ImageView carerImageView = (ImageView)findViewById(R.id.carer_pic);
		ImageSize mImageSize = new ImageSize(160, 120);
		String imageUrl = URLs.IMG_URL_STAFF+"?staff_id="+carerId;
		ImageLoader.getInstance().loadImage(imageUrl, mImageSize, options, new SimpleImageLoadingListener(){
			
            @Override
            public void onLoadingComplete(String imageUri, View view,
                    Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                carerImageView.setImageBitmap(loadedImage);
            }
             
        });
	}
	@Override
	public void onBackPressed() {
		ImageLoader.getInstance().stop();
		super.onBackPressed();
	}


}
