package com.sjtu.icarer.ui.setting;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.authenticator.AccountDataProvider;
import com.sjtu.icarer.common.utils.view.ToastUtils;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.core.app.PreferenceProvider;
import com.sjtu.icarer.core.setting.AreaTask;
import com.sjtu.icarer.core.utils.SafeAsyncTask;
import com.sjtu.icarer.events.AreaUndoEvent;
import com.sjtu.icarer.model.Area;
import com.sjtu.icarer.model.User;
import com.sjtu.icarer.service.IcarerService;
import com.sjtu.icarer.service.IcarerServiceProvider;
import com.sjtu.icarer.ui.login.LoginActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.DocumentsContract.Root;
import static com.sjtu.icarer.events.AreaUndoEvent.BUILDING_CHANGED;
import static com.sjtu.icarer.events.AreaUndoEvent.FLOOR_CHANGED;
import static com.sjtu.icarer.events.AreaUndoEvent.ROOM_CHANGED;
import static com.sjtu.icarer.events.AreaUndoEvent.UNDO_ALL;;

public class AreaPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{
	@Inject protected Bus eventBus;
	@Inject protected PreferenceProvider preferenceProvider;
	@Inject protected AccountDataProvider accountDataProvider;
	@Inject protected IcarerServiceProvider icarerServiceProvider;
	private int geroId;
	private ListPreference building_lp;
	private ListPreference floor_lp;
	private ListPreference room_lp;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        addPreferencesFromResource(R.xml.fr_area_preference);
        this.geroId =accountDataProvider.getUserData().getGeroId();
        this.building_lp = (ListPreference) findPreference("building_setting");
		this.floor_lp    = (ListPreference) findPreference("floor_setting");
		this.room_lp     = (ListPreference) findPreference("room_setting");
//		inflateListPreference(building_lp,"1-1-1-1".split("-"),"1v-1v-1v-1v".split("-"));
    }
    
    @Subscribe
    public void onUndoSetup(AreaUndoEvent areaUndoEvent){
    	Toaster.showLong(getActivity(), "post success");
    	switch (areaUndoEvent.undoEvent) {
    	case UNDO_ALL:
    		building_lp.setSummary(null);
    		floor_lp.setSummary(null);
    		room_lp.setSummary(null);
    		building_lp.setEnabled(true);
    		floor_lp.setEnabled(false);
    		room_lp.setEnabled(false);
    		break;
    	case BUILDING_CHANGED:
    		building_lp.setSummary(building_lp.getEntry());
    		floor_lp.setSummary(null);
    		room_lp.setSummary(null);
    		floor_lp.setEnabled(true);
    		room_lp.setEnabled(false);
    		break;
    	case FLOOR_CHANGED:
    		floor_lp.setSummary(floor_lp.getEntry());
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
    
    private void inflateListPreference(ListPreference lp, CharSequence[] entries, CharSequence[] entryValues){
    	lp.setEntries(entries);
    	lp.setEntryValues(entryValues);
    }
    
    
    @Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		
		String key=preference.getKey();

//		if(key.equals("download")){
//			PackageUpdateThread mPackageUpdateThread = new PackageUpdateThread(this);  
//			mPackageUpdateThread.checkUpdateInfo();
//		}
		if(key.equals("building_setting")){
			getAreas(1,0,building_lp);
			//inflateListPreference(floor_lp,new String [] {"A Man", "A Plan"},new String [] {"a_man", "a_plan"});
    	}else if(key.equals("floor_setting")){
    		int buildingId = Integer.parseInt(building_lp.getValue());
    		getAreas(2, buildingId,floor_lp);
    		
    	}else if(key.equals("room_setting")){
    		int floorId = Integer.parseInt(floor_lp.getValue());
    		getAreas(2, floorId,room_lp);
    	}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
    
    @Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
    	if(key.equals("building_setting")){
    	    eventBus.post(new AreaUndoEvent(BUILDING_CHANGED));
    	    
    	}else if(key.equals("floor_setting")){
    		
    		eventBus.post(new AreaUndoEvent(FLOOR_CHANGED));
    	}else if(key.equals("room_setting")){
    		eventBus.post(new AreaUndoEvent(ROOM_CHANGED));
    		
    	}
    }
    
    private void getAreas(final int level ,final int parentId,final ListPreference lp){
    	new SafeAsyncTask<List<Area>>() {

			@Override
			public List<Area> call() throws Exception {
				IcarerService icarerService = icarerServiceProvider.getService(getActivity());
				List<Area> areas = icarerService.getAreas(level, parentId);
				return areas;
			}
			@Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                	ToastUtils.show(getActivity(), "«Î«Û»°œ˚");
                }
            }
			
			@Override
            protected void onSuccess(final List<Area> areas) throws Exception {
                super.onSuccess(areas);
                if(areas==null||areas.size()==0){
                	return;
                }
                int size = areas.size();
                String[] entries = new String[size];
                String[] entryValues = new String[size];
                for(int i=0;i<size;i++){
                	entries[i] = areas.get(i).getName();
                	entryValues[i] = areas.get(i).getId()+"";
                }
                lp.setEntries(entries);
                lp.setEntryValues(entryValues);
            }
		}.execute();
    }
    
}
