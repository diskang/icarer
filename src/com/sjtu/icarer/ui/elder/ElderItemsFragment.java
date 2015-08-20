package com.sjtu.icarer.ui.elder;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.config.Mapping;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.common.view.CircleButton;
import com.sjtu.icarer.common.view.superslim.LayoutManager;
import com.sjtu.icarer.core.LoadElderItemTask;
import com.sjtu.icarer.core.PostElderWorkRecord;
import com.sjtu.icarer.core.utils.Named;
import com.sjtu.icarer.events.ElderClickEvent;
import com.sjtu.icarer.events.RefreshCarerEvent;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.model.ElderItem;
import com.sjtu.icarer.model.ElderRecord;
import com.sjtu.icarer.persistence.DbManager;
import com.sjtu.icarer.service.IcarerService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class ElderItemsFragment extends Fragment{
	
	private static final String KEY_HEADER_POSITIONING = "key_header_mode";

    private static final String KEY_MARGINS_FIXED = "key_margins_fixed";
    
    @Inject DbManager dbManager;
    
    @Inject Bus eventBus;
    @Inject @Named("Auth")IcarerService icarerService;
    
    @InjectView(R.id.recycler_view) protected RecyclerView lvElderItemsView;
    @InjectView(R.id.items_submit)protected Button confirmButton;
    
    private ElderRecord elderRecords;
    private int currentCarerId = 0;
    private Elder currentElder;
    
    
    //private int currentElderId = 0;
    //private int currentElderPosition = 0;
    
    private HeaderOrItemViewHolder mViews;
    private ElderItemsAdapter mAdapter;
    private int mHeaderDisplay;
    private boolean mAreMarginsFixed;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Injector.inject(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_elders_items, container, false);
		ButterKnife.inject(this, rootView);
		confirmButton.setEnabled(false);
		
		return rootView;
	}
	
	@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mHeaderDisplay = savedInstanceState
                    .getInt(KEY_HEADER_POSITIONING,
                            getResources().getInteger(R.integer.default_header_display));
            mAreMarginsFixed = savedInstanceState
                    .getBoolean(KEY_MARGINS_FIXED,
                            getResources().getBoolean(R.bool.default_margins_fixed));
        } else {
            mHeaderDisplay = getResources().getInteger(R.integer.default_header_display);
            mAreMarginsFixed = getResources().getBoolean(R.bool.default_margins_fixed);
        }
        
        mViews = new HeaderOrItemViewHolder(view);
        mViews.initViews(new LayoutManager(getActivity()));
        
    }
	
	
	
	private void getElderItems(Elder elder){
		new LoadElderItemTask(getActivity(),dbManager, elder){
			@Override
            protected void onSuccess(List<ElderItem> elderItems) throws Exception {
                super.onSuccess(elderItems);
                inflateElderItemsView(elderItems);
            }
			@Override
    		protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                e.printStackTrace();  
            }
		}.start();
	}
	

	private void inflateElderItemsView(final List<ElderItem> items){
		
        mAdapter = new ElderItemsAdapter(getActivity(), mHeaderDisplay, items);
        mAdapter.setMarginsFixed(mAreMarginsFixed);
        mAdapter.setHeaderDisplay(mHeaderDisplay);
        
        final List<HeaderOrItemSection> headerOrItems = mAdapter.getHeaderOrItems();
        
        mAdapter.setElderItemClickListener(new OnElderItemClickListener(){

			@Override
			public void onElderItemClick(View view, int position) {
				HeaderOrItemSection item = headerOrItems.get(position);
				if(item.isHeader)return;
				ElderItem elderItem = item.elderItem;
				if (elderItem==null)return;
				
				CircleButton cbCheckBox = (CircleButton)view.findViewById(R.id.cb_item_hint);
				ImageView itemIconImage=(ImageView)view.findViewById(R.id.iv_item_icon);
				String itemName = elderItem.getCareItemName();
				String itemIcon = elderItem.getIcon();
				
				int itemId = elderItem.getId();
				cbCheckBox.toggle();
				if(cbCheckBox.isChecked()){
					confirmButton.setEnabled(true);
					int resId = Mapping.icons_selected.containsKey(itemIcon)?
					        Mapping.icons_selected.get(itemIcon):R.drawable.default_user;
					itemIconImage.setBackgroundResource(resId);
//					Toaster.showShort(getActivity(), itemName+" checked");
					elderRecords.addElderItem(itemId,itemName );//item.getId() --equals-- new Long(id).intValue()
				}else{
					elderRecords.removeElderItem(itemId, itemName);
//					Toaster.showShort(getActivity(), elderRecords.toString());
					int resId = Mapping.icons.containsKey(itemIcon)?
					        Mapping.icons.get(itemIcon):R.drawable.default_user;
					itemIconImage.setBackgroundResource(resId);
					if(elderRecords.isEmpty()){
						//TODO should do something else
						confirmButton.setEnabled(false);
					}
				}
			}
        	
        });
        mViews.setAdapter(mAdapter);
	}
	
	@Subscribe
	public void refreshCarer(RefreshCarerEvent event){
		currentCarerId = event.getCarer().getId();
	}
	
	@Subscribe
	public void refreshElderItems(ElderClickEvent event){
		
		currentElder = event.getElder();
		getElderItems(currentElder);
		confirmButton.setEnabled(false);
		elderRecords=new ElderRecord(currentElder.getElderId(),currentCarerId);
	}
	
	@OnClick(R.id.items_submit)
	public void submitElderRecords(){
		if(currentCarerId==0){
			Toaster.showShort(getActivity(), getResources().getString(R.string.message_no_carer));
			refreshElderItems(new ElderClickEvent(currentElder));
			return ;
		}else{
			elderRecords.setStaffId(currentCarerId);
		}
		new PostElderWorkRecord(getActivity(), icarerService, dbManager, elderRecords){
			@Override
			protected void onSuccess(Boolean result) throws IOException{
				super.onSuccess(result);
				Toaster.showShort(getActivity(), getResources().getString(R.string.message_upload_success));
			}
			
			@Override
			protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                Toaster.showShort(getActivity(), getResources().getString(R.string.message_upload_failed));
                e.printStackTrace();  
            } 
			@Override
			protected void onFinally()throws RuntimeException{
				super.onFinally();
				//refreshByClickingElder(currentElderPosition);
				refreshElderItems(new ElderClickEvent(currentElder));
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
