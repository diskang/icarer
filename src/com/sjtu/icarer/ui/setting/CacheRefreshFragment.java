package com.sjtu.icarer.ui.setting;

import javax.inject.Inject;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.core.ClearElderItemRecordTask;
import com.sjtu.icarer.core.RefreshElderItemTask;
import com.sjtu.icarer.core.RefreshElderTask;
import com.sjtu.icarer.persistence.DbManager;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

public class CacheRefreshFragment extends PreferenceFragment{
	@Inject protected DbManager dbManager;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        addPreferencesFromResource(R.xml.fr_cache_preference);
	}
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		String key=preference.getKey();
		if("cache_refresh_elder".equals(key)){
			new RefreshElderTask(getActivity(), dbManager, null){
//				@Override
//    			protected void onFinally() throws RuntimeException{
//    				super.onFinally();
//				}
    		}.start();
		}else if("cache_refresh_elder_item".equals(key)){
			new RefreshElderItemTask(getActivity(), dbManager, null){
    		}.start();
		}else if("cache_clear_elder_item_record".equals(key)){
			new ClearElderItemRecordTask(getActivity(), dbManager, null){
    		}.start();
		}
		
		//TODO make the refresh take effect, refresh the screen, or redirect to homeActivity
		Toaster.showShort(getActivity(), key);
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}
