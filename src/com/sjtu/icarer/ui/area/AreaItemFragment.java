package com.sjtu.icarer.ui.area;

import java.util.List;

import javax.inject.Inject;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.utils.LogUtils;
import com.sjtu.icarer.core.LoadAreaItemTask;
import com.sjtu.icarer.model.AreaItem;
import com.sjtu.icarer.persistence.DbManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AreaItemFragment extends Fragment{
	@Inject DbManager dbManager;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Injector.inject(this);
		getAreaItems();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_room_service, container, false);
		return rootView;
	}
	
	private void getAreaItems(){
		new LoadAreaItemTask(getActivity(),dbManager,false) {
			@Override
            protected void onSuccess(List<AreaItem> items) throws Exception {
                super.onSuccess(items);
                List<AreaItem> is = items;
                LogUtils.d("area items fetched");
                // show icon
            }
			@Override
    		protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                LogUtils.d(e.getMessage());
                e.printStackTrace();  
            }
		}.start();
	}
}
