package com.sjtu.icarer.ui;

import java.util.List;

import javax.inject.Inject;

import android.accounts.OperationCanceledException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sjtu.icarer.FragmentCarer;
import com.sjtu.icarer.FragmentElder;
import com.sjtu.icarer.FragmentRoom;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.common.constant.Constants;
import com.sjtu.icarer.common.utils.LogUtils;
import com.sjtu.icarer.common.utils.OpUtil;
import com.sjtu.icarer.core.LoadAreaCarerTask;
import com.sjtu.icarer.core.utils.PreferenceManager;
import com.sjtu.icarer.events.RefreshCarerEvent;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.persistence.DbManager;
import com.sjtu.icarer.service.IcarerServiceProvider;
import com.sjtu.icarer.ui.area.AreaItemFragment;
import com.squareup.otto.Subscribe;

public class HomeActivity extends IcarerFragmentActivity {
	private int current_fragment_type ;//1:room  2:elder  3:carer
	@Inject protected IcarerServiceProvider icarerServiceProvider;
	@Inject protected PreferenceManager preferenceProvider;
	@Inject protected DbManager dbManager;
	
	LinearLayout carerItemLayout; 
	@InjectView(R.id.room_number)protected TextView roomNumView;
	private TextView carerTextView ;
	private ImageView carerImageView;
	private Carer carer;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ButterKnife.inject(this);
		setContentView(R.layout.activity_home);
		carerItemLayout = (LinearLayout)findViewById(R.id.carer_item);
		carerImageView = (ImageView)carerItemLayout.findViewById(R.id.iv_avatar);
		carerTextView = (TextView)carerItemLayout.findViewById(R.id.tv_item_info);

		int frIndex = getIntent().getIntExtra(Constants.FRAGMENT_INDEX, 1);
		addFragment(frIndex);
//		roomNumView.setText("房间: \n"+ preferenceProvider.getAreaFullName());
		getCarer();

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
//			tag = FragmentRoom.class.getSimpleName();
			tag = AreaItemFragment.class.getSimpleName();
			fr = getSupportFragmentManager().findFragmentByTag(tag);
			if (fr == null) {
				fr = new AreaItemFragment();
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
			promptLayout =(RelativeLayout) RelativeLayout.inflate(this, R.drawable.bar_network_prompt, null);
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
	private void getCarer(){
		new LoadAreaCarerTask(this,dbManager,false){
    		@Override
            protected void onSuccess(List<Carer> carers) throws Exception {
                super.onSuccess(carers);
                LogUtils.d("area carer fetched");
                if(carers!=null&&!carers.isEmpty()){
                	eventBus.post(new RefreshCarerEvent(carers.get(0)));
                }
            }
    		@Override
    		protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                    finish();
                }else{
                	LogUtils.d(e.getMessage());
                	e.printStackTrace();
                }
            }
    	}.start();
	}
	
	@Subscribe
	public void updateCarer(RefreshCarerEvent refreshCarerEvent){
		LogUtils.d("receive refreshcarerevent");
		Carer carer = refreshCarerEvent.getCarer();
		if (carer==null)return;
		carerTextView.setText("今日护工："+carer.getName());
    	loadCarerImage(carer);
	}
	
	private void loadCarerImage(Carer carer){
		String imageUrl = Url.URL_BASE+Url.URL_OBJECT_DOWNLOAD+"?file_url="+carer.getPhotoUrl();
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_user) // resource or drawable
            .showImageForEmptyUri(R.drawable.default_user) // resource or drawable
            .showImageOnFail(R.drawable.default_user) // resource or drawable
            .cacheInMemory(true) 
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .displayer(new RoundedBitmapDisplayer(2000))
            .build();
		
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
