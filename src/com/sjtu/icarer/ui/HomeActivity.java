package com.sjtu.icarer.ui;

import javax.inject.Inject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sjtu.icarer.FragmentCarer;
import com.sjtu.icarer.FragmentElder;
import com.sjtu.icarer.FragmentRoom;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.config.Prefer;
import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.common.constant.Constants;
import com.sjtu.icarer.common.utils.OpUtil;
import com.sjtu.icarer.core.app.PreferenceManager;

public class HomeActivity extends IcarerFragmentActivity {
	private int current_fragment_type ;//1:room  2:elder  3:carer
	private String roomNumber;
	private String carerName;
	private String carerId;
	private Context context;
	@Inject protected PreferenceManager preferenceProvider;
	
	@InjectView(R.id.carer_item)protected LinearLayout carerItemLayout; 
	@InjectView(R.id.room_number)protected TextView roomNumView;
	@InjectView(R.id.tv_item_info)protected TextView carerView ;
	
	@Inject protected PackageInfo info;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		context = this;
		
		Prefer prefer = new Prefer(this);
//		roomNumber = prefer.getRoomNumber();
		roomNumber = preferenceProvider.getAreaFullName();
		carerName = prefer.getCarerName();
		carerId = prefer.getCarerId();
		int frIndex = getIntent().getIntExtra(Constants.FRAGMENT_INDEX, 1);
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
		final ImageView carerImageView = (ImageView)carerItemLayout.findViewById(R.id.iv_avatar);
		roomNumView.setText("房间: \n"+roomNumber);
		carerView.setText("今日护工: "+carerName);
		loadCarerImage(carerImageView);
	}
	
	private void loadCarerImage(ImageView carerImageView){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.default_carer) // resource or drawable
        .showImageForEmptyUri(R.drawable.default_carer) // resource or drawable
        .showImageOnFail(R.drawable.default_carer) // resource or drawable
        .cacheInMemory(true) 
        .cacheOnDisk(true)
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .displayer(new RoundedBitmapDisplayer(2000))
        .build();
		
		
		
		String imageUrl = Url.IMG_URL_STAFF+"?staff_id="+carerId;
		ImageLoader.getInstance().displayImage(imageUrl, carerImageView, options);
//		ImageLoader.getInstance().loadImage(imageUrl, mImageSize, options, new SimpleImageLoadingListener(){
//			
//			 @Override
//	            public void onLoadingComplete(String imageUri, View view,
//	                    Bitmap loadedImage) {
//	                super.onLoadingComplete(imageUri, view, loadedImage);
//	                carerAware.setImageBitmap(loadedImage);
//	            }
//             
//        });
	}
	@Override
	public void onBackPressed() {
		ImageLoader.getInstance().stop();
		super.onBackPressed();
	}
	

}
