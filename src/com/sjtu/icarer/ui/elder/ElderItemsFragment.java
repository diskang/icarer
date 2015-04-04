package com.sjtu.icarer.ui.elder;

import java.util.List;

import javax.inject.Inject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.utils.LogUtils;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.common.view.CircleButton;
import com.sjtu.icarer.core.LoadElderCarerTask;
import com.sjtu.icarer.core.LoadElderItemTask;
import com.sjtu.icarer.core.LoadElderTask;
import com.sjtu.icarer.events.RefreshCarerEvent;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.model.ElderItem;
import com.sjtu.icarer.persistence.DbManager;
import com.squareup.otto.Bus;

public class ElderItemsFragment extends Fragment{
	@Inject DbManager dbManager;
	@Inject LayoutInflater layoutInflater;
	@Inject Bus eventBus;
	@InjectView(R.id.lv_elders)      protected ListView lvEldersView;
	@InjectView(R.id.lv_elder_items) protected ListView lvElderItemsView;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Injector.inject(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_elders_items, container, false);
		ButterKnife.inject(this, rootView);
		getElders();
		return rootView;
	}
	
	private void getElders(){
		new LoadElderTask(getActivity(),dbManager,false) {
			@Override
            protected void onSuccess(List<Elder> elders) throws Exception {
                super.onSuccess(elders);
                inflateEldersView(elders);
            }
			@Override
    		protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                LogUtils.d(e.getMessage());
                e.printStackTrace();  
            }
		}.start();
	}
	
	private void getElderItems(Elder elder){
		new LoadElderItemTask(getActivity(),dbManager, elder, false){
			@Override
            protected void onSuccess(List<ElderItem> elderItems) throws Exception {
                super.onSuccess(elderItems);
            }
			@Override
    		protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                e.printStackTrace();  
            }
		}.start();
	}
	
	private void inflateEldersView(final List<Elder> elders){
		lvEldersView.setAdapter(new ElderAdapter(layoutInflater,elders));
		lvEldersView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int checkPosition = lvEldersView.getCheckedItemPosition();
				for(int i=0,len = elders.size();i<len;i++){// single choice maintenance
					View elderView = (View)lvEldersView.getChildAt(i-lvEldersView.getFirstVisiblePosition());
					CircleButton cbCheckBox = (CircleButton)elderView.findViewById(R.id.cb_elder_hint);
					cbCheckBox.setChecked(i==position);
				}
				getElderCarer(elders.get(position));
				getElderItems(elders.get(position));
				Toaster.showShort(getActivity(), "checkPosition:"+checkPosition);
			}
		});
		//should click the 1st view of elders TODO
	}
	
	private void getElderCarer(final Elder elder){
		new LoadElderCarerTask(getActivity(),dbManager,elder,false){
    		@Override
            protected void onSuccess(List<Carer> carers) throws Exception {
                super.onSuccess(carers);
                if(carers!=null&&!carers.isEmpty()){
                	LogUtils.d(elder.getName()+"'s carer is"+carers.get(0).getName());
                	eventBus.post(new RefreshCarerEvent(carers.get(0)));
                }else{
                	LogUtils.d(elder.getName()+"'s carer is null");
                }
            }
    		@Override
    		protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
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
