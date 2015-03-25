package com.sjtu.icarer.ui.elder;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.sjtu.icarer.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ElderItemsFragment extends Fragment{
	@InjectView(R.id.lv_elders)      protected ListView lvEldersView;
	@InjectView(R.id.lv_elder_items) protected ListView lvElderItemsView;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_elders_items, container, false);
		ButterKnife.inject(this, rootView);
		return rootView;
	}
}
