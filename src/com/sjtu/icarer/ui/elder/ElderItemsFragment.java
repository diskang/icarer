package com.sjtu.icarer.ui.elder;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.utils.LogUtils;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.core.LoadAreaItemTask;
import com.sjtu.icarer.core.LoadElderItemTask;
import com.sjtu.icarer.core.LoadElderTask;
import com.sjtu.icarer.model.AreaItem;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.model.ElderItem;
import com.sjtu.icarer.persistence.DbManager;
import com.sjtu.icarer.ui.area.AreaItemAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ElderItemsFragment extends Fragment{
	@Inject DbManager dbManager;
	@Inject LayoutInflater layoutInflater;
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
                LogUtils.d(e.getMessage());
                e.printStackTrace();  
            }
		}.start();
	}
	
	private void inflateEldersView(List<Elder> elders){
		((ListView) lvEldersView).setAdapter(new ElderAdapter(layoutInflater,elders));
		lvEldersView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toaster.showShort(getActivity(), "position:"+position);
			}
		});
	}
}
