package com.sjtu.icarer.ui.setting;

import static com.sjtu.icarer.events.AreaUndoEvent.BUILDING_CHANGED;
import static com.sjtu.icarer.events.AreaUndoEvent.FLOOR_CHANGED;
import static com.sjtu.icarer.events.AreaUndoEvent.ROOM_CHANGED;
import static com.sjtu.icarer.events.AreaUndoEvent.UNDO_ALL;

import java.util.List;

import javax.inject.Inject;

import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.utils.view.ToastUtils;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.core.utils.Named;
import com.sjtu.icarer.core.utils.PreferenceManager;
import com.sjtu.icarer.core.utils.SafeAsyncTask;
import com.sjtu.icarer.events.AreaUndoEvent;
import com.sjtu.icarer.events.SetupSubmitEvent;
import com.sjtu.icarer.events.TaskCancelEvent;
import com.sjtu.icarer.model.Area;
import com.sjtu.icarer.service.IcarerService;
import com.sjtu.icarer.ui.HomeActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class AreaPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{
	
	private static final String AREA_FULL_NAME_KEY = "areaFullNames";
	@Inject protected Bus eventBus;
	@Inject protected PreferenceManager preferenceProvider;
//	@Inject protected AccountDataProvider accountDataProvider;
//	@Inject protected IcarerServiceProvider icarerServiceProvider;
	@Inject @Named("Auth") protected IcarerService icarerService;
	private boolean areaIdChanged = false;
	private int areaId;
	private String areaFullName;
	private ListPreference building_lp;
	private ListPreference floor_lp;
	private ListPreference room_lp;
	private SafeAsyncTask<List<Area>> areaTask;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        addPreferencesFromResource(R.xml.fr_area_preference);
        this.areaId =preferenceProvider.getAreaId();
        this.building_lp = (ListPreference) findPreference("building_setting");
		this.floor_lp    = (ListPreference) findPreference("floor_setting");
		this.room_lp     = (ListPreference) findPreference("room_setting");
		initPreference();
		getAreas(1,0,building_lp);
    }
    private void initPreference(){
    	if(areaId==0){
			floor_lp.setEnabled(false);
			room_lp.setEnabled(false);
			clearListPreference(building_lp);
			clearListPreference(floor_lp);
			clearListPreference(room_lp);
		}else{
			this.building_lp.setSummary(building_lp.getValue());
			this.floor_lp.setSummary(floor_lp.getValue());
			this.room_lp.setSummary(room_lp.getValue());
		}
    }
    @Subscribe
    public void onUndoSetup(AreaUndoEvent areaUndoEvent){
    	Toaster.showLong(getActivity(), "post success");
    	
    	switch (areaUndoEvent.undoEvent) {
    	case UNDO_ALL:
    		clearListPreference(building_lp);
    		clearListPreference(floor_lp);
    		clearListPreference(room_lp);
    		building_lp.setEnabled(true);
    		floor_lp.setEnabled(false);
    		room_lp.setEnabled(false);
    		getAreas(1,0,building_lp);
    		break;
    	case BUILDING_CHANGED:
    		building_lp.setSummary(building_lp.getEntry());
    		clearListPreference(floor_lp);
    		clearListPreference(room_lp);
    		floor_lp.setEnabled(true);
    		room_lp.setEnabled(false);
    		break;
    	case FLOOR_CHANGED:
    		floor_lp.setSummary(floor_lp.getEntry());
    		clearListPreference(room_lp);
    		room_lp.setEnabled(true);
    	    break;
        case ROOM_CHANGED:
        	room_lp.setSummary(room_lp.getEntry());
        	break;
		default:
			break;
		}
    }
 
    
    @Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
//		String key=preference.getKey();
//		if(key.equals("download")){
//			PackageUpdateThread mPackageUpdateThread = new PackageUpdateThread(this);  
//			mPackageUpdateThread.checkUpdateInfo();
////		}
//		if(key.equals("building_setting")){
//    	}else if(key.equals("floor_setting")){
//    	}else if(key.equals("room_setting")){	
//    	}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
    
    @Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
    	if(key.equals("building_setting")){
    	    eventBus.post(new AreaUndoEvent(BUILDING_CHANGED));
    	    int buildingId = Integer.parseInt(building_lp.getValue());
    		getAreas(2, buildingId,floor_lp);
    	}else if(key.equals("floor_setting")){
    		
    		eventBus.post(new AreaUndoEvent(FLOOR_CHANGED));
    		int floorId = Integer.parseInt(floor_lp.getValue());
    		getAreas(3, floorId,room_lp);
    	}else if(key.equals("room_setting")){
    		areaIdChanged = true;
    		eventBus.post(new AreaUndoEvent(ROOM_CHANGED));
    		areaId = Integer.parseInt(room_lp.getValue());
    		areaFullName = getAreaFullName(room_lp);
    	}
    }
    
    private String getAreaFullName(ListPreference lp){
    	try{
    	    Bundle bundle = room_lp.peekExtras();
    	    CharSequence[] areaFullNames = bundle.getCharSequenceArray(AREA_FULL_NAME_KEY);
		    int location = lp.findIndexOfValue(lp.getValue());
		    return (String) areaFullNames[location];
    	}catch(Exception e){
    		return null;
    	}	
    }
    private void getAreas(final int level ,final int parentId,final ListPreference lp){
    	areaTask = new SafeAsyncTask<List<Area>>() {

			@Override
			public List<Area> call() throws Exception {
				List<Area> areas = icarerService.getAreas(level, parentId);
				return areas;
			}
			@Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                	ToastUtils.show(getActivity(), "request canceled");
                }else{
                	ToastUtils.show(getActivity(), "cannot get data");
                }
            }
			 @Override
	            protected void onFinally() throws RuntimeException {
	                hideProgress();
	            }
			@Override
            protected void onSuccess(final List<Area> areas) throws Exception {
                super.onSuccess(areas);
                inflateListPreference(lp,areas);
            }
		};
		areaTask.execute();
		showProgress();	
		
    }
    
    @Subscribe
    public void onTaskCancel(TaskCancelEvent event){
    	if (areaTask!=null){
    		areaTask.cancel(true);
    	}
    }
    
    @Subscribe
    public void onSetupSubmit(SetupSubmitEvent event){
    	if(areaIdChanged){
    		preferenceProvider.setAreaFullName(areaFullName);
        	preferenceProvider.setAreaId(areaId);
    	}else{
    		if(areaId==0){
    			ToastUtils.show(getActivity(), "设定未完成");
    			return;
    		}else{
    			ToastUtils.show(getActivity(), "信息未修改");
    		}
    	}
    	final Intent i = new Intent(getActivity(), HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        getActivity().finish();
    }
    private void inflateListPreference(ListPreference lp,List<Area> areas){
    	if(areas==null||areas.size()==0){
        	return;
        }
        int size = areas.size();
        String[] areaNames = new String[size];
        String[] areaIds = new String[size];
        String[] areaFullNames = new String[size];
        
        for(int i=0;i<size;i++){
        	areaNames[i] = areas.get(i).getName();
        	areaIds[i] = areas.get(i).getId()+"";
        	areaFullNames[i] = areas.get(i).getFullName();
        }
        lp.setEntries(areaNames);
        lp.setEntryValues(areaIds);
        Bundle bundle = lp.getExtras();
        bundle.putCharSequenceArray(AREA_FULL_NAME_KEY,areaFullNames);
    }
    
    private void clearListPreference(ListPreference lp){
    	lp.setSummary(null);
    	lp.setEntries(new String[0]);
    	lp.setEntryValues(new String[0]);
//    	lp.setKey(null);
    	lp.setDefaultValue(null);
    	
    }
    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        getActivity().dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
    	getActivity().showDialog(0);
    }
    
    @Override
	public void onResume() {
        super.onResume();
        eventBus.register(this);
        preferenceProvider.getPreferences()
            .registerOnSharedPreferenceChangeListener(this);
        
        
    }

    @Override
	public void onPause() {
        super.onPause();
        eventBus.unregister(this);
        preferenceProvider.getPreferences()
            .unregisterOnSharedPreferenceChangeListener(this);
    }
   
}
