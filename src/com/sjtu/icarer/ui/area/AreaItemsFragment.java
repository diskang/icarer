package com.sjtu.icarer.ui.area;

import java.io.IOException;
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
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.config.Mapping;
import com.sjtu.icarer.common.utils.LogUtils;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.common.view.CircleButton;
import com.sjtu.icarer.core.LoadAreaItemTask;
import com.sjtu.icarer.core.PostAreaWorkRecord;
import com.sjtu.icarer.core.utils.Named;
import com.sjtu.icarer.core.utils.PreferenceManager;
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
	@InjectView(R.id.items_submit)protected Button confirmButton;
	
	private AreaRecord areaRecords;
	private Carer currentCarer;
//	private int areaId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Injector.inject(this);
		areaRecords = new AreaRecord();
//		areaId = preferenceManager.getAreaId();
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
				ImageView itemIconImage=(ImageView)view.findViewById(R.id.iv_item_icon);
				AreaItem item = areaItems.get(position);
				String itemIcon = item.getIcon();
				cbCheckBox.toggle();
				if(cbCheckBox.isChecked()){
					confirmButton.setEnabled(true);
					int resId = Mapping.icons_selected.containsKey(itemIcon)?
					        Mapping.icons_selected.get(itemIcon):R.drawable.default_user;
					itemIconImage.setBackgroundResource(resId);
					areaRecords.addAreaItem(item.getId(),item.getName() );//item.getId() --equals-- new Long(id).intValue()
				}else{
					areaRecords.removeAreaItem(item.getId(), item.getName());
//					Toaster.showLong(getActivity(), areaRecords.toString());
					int resId = Mapping.icons.containsKey(itemIcon)?
					        Mapping.icons.get(itemIcon):R.drawable.default_user;
					itemIconImage.setBackgroundResource(resId);
					if(areaRecords.isEmpty()){
						//TODO should do something else
						confirmButton.setEnabled(false);
					}
				}
			}
		});
		
	}
	
	@OnClick(R.id.items_submit)
    public void submitAreaRecords(){
//		Toaster.showShort(getActivity(), "button click");
        if (currentCarer==null){
            Toaster.showShort(getActivity(), "no carer!");
            currentCarer = new Carer(1);
            }
		new PostAreaWorkRecord(getActivity(), icarerService, dbManager, preferenceManager, areaRecords, currentCarer){
			@Override
			protected void onSuccess(Boolean result)throws IOException {
				super.onSuccess(result);
				Toaster.showShort(getActivity(), getResources().getString(R.string.message_upload_success));
			}
			
			@Override
			protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                e.printStackTrace();  
                Toaster.showShort(getActivity(), getResources().getString(R.string.message_upload_failed));
            } 
			@Override
			protected void onFinally() throws RuntimeException{
				super.onFinally();
				refreshAreaItems();
			}
		}.start();
	}
	
	private void refreshAreaItems(){
//		for(int i=0;i<areaItemsView.getChildCount();++i){
//			View areaview = areaItemsView.getChildAt(i);
//			CircleButton cbCheckBox = (CircleButton)areaview.findViewById(R.id.cb_item_hint);
//			cbCheckBox.setChecked(false);
//		}
		getAreaItems();
		confirmButton.setEnabled(false);
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
