package com.sjtu.icarer.ui.setting;

import com.sjtu.icarer.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class AboutPreferenceFragment extends PreferenceFragment{
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.fr_about_preference);
	}
}
