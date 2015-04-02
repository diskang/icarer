package com.sjtu.icarer.ui.area;

import java.util.List;

import javax.inject.Inject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.utils.LogUtils;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.core.LoadAreaCarerTask;
import com.sjtu.icarer.core.LoadAreaItemTask;
import com.sjtu.icarer.events.RefreshCarerEvent;
import com.sjtu.icarer.model.AreaItem;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.persistence.DbManager;
import com.squareup.otto.Bus;

public class AreaItemsFragment extends Fragment{
	@Inject DbManager dbManager;
	@Inject LayoutInflater layoutInflater;
	@Inject protected Bus eventBus;
	private GridView serviceView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Injector.inject(this);
		getAreaCarer();
		getAreaItems();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_area_items, container, false);
		
		serviceView = (GridView) rootView.findViewById(R.id.room_service_items);
//		SubmitButton = (Button)rootView.findViewById(R.id.room_confirm_submit);
		
		return rootView;
	}
	
	private void getAreaItems(){
		new LoadAreaItemTask(getActivity(),dbManager,false) {
			@Override
            protected void onSuccess(List<AreaItem> items) throws Exception {
                super.onSuccess(items);
//                List<AreaItem> is = items;
                LogUtils.d("area items fetched");
                inflateAreaItemView(items);
            }
			@Override
    		protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                LogUtils.d(e.getMessage());
                e.printStackTrace();  
            }
		}.start();
	}
	
	private void inflateAreaItemView(List<AreaItem> areaItems){
		((GridView) serviceView).setAdapter(new AreaItemAdapter(layoutInflater,areaItems));
		serviceView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toaster.showShort(getActivity(), "position:"+position);
			}
		});
	}
	
	private void getAreaCarer(){
		new LoadAreaCarerTask(getActivity(),dbManager,false){
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
                	LogUtils.d(e.getMessage());
                	e.printStackTrace();
              
            }
    	}.start();
	}
	
	@Override
	public void onResume() {
        super.onResume();
        eventBus.register(this);
        
    }

    @Override
	public void onPause() {
        super.onPause();
        eventBus.unregister(this);
        
    }
}
