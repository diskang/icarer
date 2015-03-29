package com.sjtu.icarer.core.app;

import android.content.SharedPreferences;

import static com.sjtu.icarer.common.constant.Constants.Prefer;
public class PreferenceProvider {
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
    public PreferenceProvider(SharedPreferences sharedPreferences) {
    	this.sharedPreferences = sharedPreferences;
    	this.editor = this.sharedPreferences.edit();
	}
    
    public SharedPreferences getPreferences(){
    	return sharedPreferences;
    }
    
    public Integer getAreaId(){
    	return sharedPreferences.getInt(Prefer.AREA_ID,0);
    }
    
    public String getAreaFullName(){
    	return sharedPreferences.getString(Prefer.AREA_FULL_NAME, null);
    }
    
    public void setAreaId(Integer areaId) {
		editor.putInt(Prefer.AREA_ID, areaId);
		editor.commit();
	}
    
    public void setAreaFullName(String fullName){
    	editor.putString(Prefer.AREA_FULL_NAME, fullName);
    	editor.commit();
    }
    
}
