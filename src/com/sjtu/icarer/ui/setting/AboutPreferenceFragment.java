package com.sjtu.icarer.ui.setting;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.sjtu.icarer.R;
import com.sjtu.icarer.thread.PackageUpdateThread;

public class AboutPreferenceFragment extends PreferenceFragment{
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.fr_about_preference);
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		String key=preference.getKey();
		if("about_software_update".equals(key)){
			PackageUpdateThread mPackageUpdateThread = new PackageUpdateThread(getActivity());  
			mPackageUpdateThread.checkUpdateInfo();
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}
