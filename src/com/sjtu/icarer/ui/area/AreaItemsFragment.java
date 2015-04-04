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
import android.widget.Button;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.utils.LogUtils;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.common.view.CircleButton;
import com.sjtu.icarer.core.LoadAreaCarerTask;
import com.sjtu.icarer.core.LoadAreaItemTask;
import com.sjtu.icarer.core.utils.Named;
import com.sjtu.icarer.core.utils.PreferenceManager;
import com.sjtu.icarer.events.RefreshCarerEvent;
import com.sjtu.icarer.model.AreaItem;
import com.sjtu.icarer.model.AreaRecord;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.persistence.DbManager;
import com.sjtu.icarer.service.IcarerService;
import com.squareup.otto.Bus;

public class AreaItemsFragment extends Fragment{
	@Inject DbManager dbManager;
	@Inject LayoutInflater layoutInflater;
	@Inject PreferenceManager preferenceManager;
	@Inject @Named("Auth")IcarerService icarerService;
	@Inject protected Bus eventBus;
	@InjectView(R.id.room_service_items)protected GridView areaItemsView;
	@InjectView(R.id.room_confirm_submit)protected Button confirmButton;
	
	private AreaRecord areaRecords;
	private Carer carer;
	private int areaId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Injector.inject(this);
		areaRecords = new AreaRecord();
		areaId = preferenceManager.getAreaId();
		getAreaCarer();
		getAreaItems();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_area_items, container, false);
		ButterKnife.inject(this, rootView);
//		areaItemsView = (GridView) rootView.findViewById(R.id.room_service_items);
//		SubmitButton = (Button)rootView.findViewById(R.id.room_confirm_submit);
		confirmButton.setEnabled(false);
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
                e.printStackTrace();  
            }
		}.start();
	}
	
	private void inflateAreaItemView(final List<AreaItem> areaItems){
		areaItemsView.setAdapter(new AreaItemAdapter(layoutInflater,areaItems));
		areaItemsView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				int count = areaItemsView.getCheckedItemCount();
//				Toaster.showShort(getActivity(), "position:"+position+",count:"+count);
				CircleButton cbCheckBox = (CircleButton)view.findViewById(R.id.cb_item_hint);
				AreaItem item = areaItems.get(position);
				cbCheckBox.toggle();
				if(cbCheckBox.isChecked()){
					confirmButton.setEnabled(true);
					Toaster.showShort(getActivity(), item.getName()+" checked");
					areaRecords.addAreaItem(item.getId(),item.getName() );//item.getId() --equals-- new Long(id).intValue()
				}else{
					Toaster.showShort(getActivity(), item.getName()+" canceled");
					areaRecords.removeAreaItem(item.getId(), item.getName());
					Toaster.showLong(getActivity(), areaRecords.toString());
					if(areaRecords.isEmpty()){
						//TODO should do something else
						confirmButton.setEnabled(false);
					}
				}
				
			}
		});
		
//		areaItemsView.setOnItemSelectedListener(new OnItemSelectedListener(){
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				CircleButton cbCheckBox = (CircleButton)view.findViewById(R.id.cb_item_hint);
//				cbCheckBox.setPressed(true);
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//				Toaster.showShort(getActivity(), "nothing selected");
//			}
//		});
	}
	
	private void getAreaCarer(){
		new LoadAreaCarerTask(getActivity(),dbManager,false){
    		@Override
            protected void onSuccess(List<Carer> carers) throws Exception {
                super.onSuccess(carers);
                LogUtils.d("area carer fetched");
                if(carers!=null&&!carers.isEmpty()){
                	carer = carers.get(0);
                	eventBus.post(new RefreshCarerEvent(carer));
                }else{
                	//TODO should do something
                }
            }
    		@Override
    		protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                	e.printStackTrace();
            }
    	}.start();
	}
	
	@OnClick(R.id.room_confirm_submit)
	public void submitAreaRecords(){
//		long[] itemIds = areaItemsView.getCheckedItemIds();
		Toaster.showShort(getActivity(), "button click");
		Toaster.showShort(getActivity(), "button click");
		if (carer!=null){
			areaRecords.setStaffId(carer.getId());
		}else{
			Toaster.showShort(getActivity(), "don't have a carer !");
			areaRecords.setStaffId(1);
		}
		areaRecords.setAreaId(areaId);
		icarerService.insertAreawork(areaRecords);
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
