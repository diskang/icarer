package com.sjtu.icarer.ui;

import javax.inject.Inject;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnLongClick;
import butterknife.Optional;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.common.constant.Constants;
import com.sjtu.icarer.common.deprecated.OpUtil;
import com.sjtu.icarer.common.utils.TimeUtils;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.core.utils.PreferenceManager;
import com.sjtu.icarer.events.RefreshCarerEvent;
import com.sjtu.icarer.events.RefreshScreenEvent;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.persistence.DbManager;
import com.sjtu.icarer.service.IcarerServiceProvider;
import com.sjtu.icarer.ui.area.AreaItemsFragment;
import com.sjtu.icarer.ui.elder.ElderItemsFragment;
import com.squareup.otto.Subscribe;

public class HomeActivity extends IcarerFragmentActivity {
	private int current_fragment_type ;//1:room  2:elder  3:carer
	@Inject protected IcarerServiceProvider icarerServiceProvider;
	@Inject protected PreferenceManager preferenceProvider;
	@Inject protected DbManager dbManager;
	
	@InjectView(R.id.carer_item)protected LinearLayout carerItemLayout; 
	@InjectView(R.id.btn_elder_service)protected RadioButton elderServiceBtn;
	@InjectView(R.id.btn_area_service)protected RadioButton areaServiceBtn;
	private TextView carerTextView ;
	private ImageView carerImageView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		carerImageView = (ImageView)carerItemLayout.findViewById(R.id.iv_avatar);
		carerTextView = (TextView)carerItemLayout.findViewById(R.id.tv_item_info);

		int frIndex = getIntent().getIntExtra(Constants.FRAGMENT_INDEX, 1);
		addFragment(frIndex);
	}

	@Override
	public void onResume(){
		super.onResume();
		check_network();
	}
	
	public void onElderServiceClick(View view){
			addFragment(1);
	}
	
	public void onAreaServiceClick(View view){
			addFragment(2);	
	}
	
	
	private void addFragment(int type){
		Fragment fr;
		String tag;
		int resourceId = R.id.main_fragment;
		check_network();
		switch(type){
		
		case 1:
//			tag = FragmentElder.class.getSimpleName();
			tag = ElderItemsFragment.class.getSimpleName();
			fr = getSupportFragmentManager().findFragmentByTag(tag);
			if (fr == null) {
				fr = new ElderItemsFragment();
			}
			//TODO wrap a radioGroup can remove this 
			elderServiceBtn.setChecked(true);
		   areaServiceBtn.setChecked(false);
			break;

		default:
			tag = AreaItemsFragment.class.getSimpleName();
			fr = getSupportFragmentManager().findFragmentByTag(tag);
			if (fr == null) {
				fr = new AreaItemsFragment();
			}
			elderServiceBtn.setChecked(false);
			areaServiceBtn.setChecked(true);
			break;
		}
		
		getSupportFragmentManager().beginTransaction().replace(resourceId, fr, tag).commit();	 
		current_fragment_type = type;
		
	}
	
	private void check_network(){
		if(!OpUtil.isConnected(this)){
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
	
    /*
     * Subscribe event, posted from AreaItemsFragment & ElderItemsFragment
     * */
	@Subscribe
	public void updateCarer(RefreshCarerEvent refreshCarerEvent){
//		LogUtils.d("receive refreshcarerevent");
		Carer carer = refreshCarerEvent.getCarer();
		loadCarerImage(carer);
		if (carer==null || carer.getWorkDate()==null){
            carerTextView.setText(R.string.text_get_carer_failed);	
		}else{
			String workDateString = carer.getWorkDate().toString();
			String carerText =
					TimeUtils.getCurrentTimeInString(TimeUtils.DATE_FORMAT_DATE)
					.equals(workDateString)?
							"今日护工\n":workDateString+"\n";
            carerTextView.setText(carerText+carer.getName());
            }
        }
	
	@Subscribe
	public void refreshScreen(RefreshScreenEvent event){
		
		addFragment(current_fragment_type);
	}
	
	private void loadCarerImage(Carer carer){
		String imageUrl = 
				carer!=null?
				Url.URL_BASE+Url.URL_OBJECT_DOWNLOAD+"?file_url="+carer.getPhotoUrl()
				:"drawable://"+R.drawable.default_user;
		
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
    @Optional
    @OnLongClick(R.id.carer_item)
    public boolean carerLongClick(){
        Toaster.showShort(this, "something happened");
        return true;
    }
	@Override
	public void onBackPressed() {
		ImageLoader.getInstance().stop();
		super.onBackPressed();
	}
	
	/*
	 *  see other part of Options in IcarerFragmentActivity
	 *   cause IcarerFragmentActivity cannot have DbManager, or MainActivity will throw an error
	 *    now I just put --R.id.goto_clear--  here, it's not clear!!
	*/
//	@Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	int id = item.getItemId();
//    	switch(id) {
//    	case R.id.goto_clear://clear all elders' data, can clear data in setting screen also
//    		new ClearElderTask(this, dbManager, null){
//    			@Override
//    			protected void onFinally() throws RuntimeException{
//    				super.onFinally();
//    				eventBus.post(new RefreshScreenEvent());
//    			}
//    		}.start();
//    		return true;
//    	}
//    	return super.onOptionsItemSelected(item);
//	}

}
