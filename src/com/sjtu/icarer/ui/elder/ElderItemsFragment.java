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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.common.view.CircleButton;
import com.sjtu.icarer.common.view.superslim.LayoutManager;
import com.sjtu.icarer.core.LoadElderCarerTask;
import com.sjtu.icarer.core.LoadElderItemTask;
import com.sjtu.icarer.core.LoadElderTask;
import com.sjtu.icarer.core.PostElderWorkRecord;
import com.sjtu.icarer.core.utils.Named;
import com.sjtu.icarer.events.RefreshCarerEvent;
import com.sjtu.icarer.events.RefreshScreenEvent;
import com.sjtu.icarer.model.Carer;
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
    @Inject LayoutInflater layoutInflater;
    @Inject Bus eventBus;
    @Inject @Named("Auth")IcarerService icarerService;
    @InjectView(R.id.lv_elders)      protected ListView lvEldersView;
    @InjectView(R.id.recycler_view) protected RecyclerView lvElderItemsView;
    @InjectView(R.id.items_submit)protected Button confirmButton;
    
    private ElderRecord elderRecords;
    private int currentCarerId = 0;
	private List<Carer> historyCarers;
    
    private int currentElderId = 0;
    private int currentElderPosition = 0;
    
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
		getElders();
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
	
	private void getElders(){
		new LoadElderTask(getActivity(),dbManager,false) {
			@Override
            protected void onSuccess(List<Elder> elders) throws Exception {
                super.onSuccess(elders);
                inflateEldersView(elders);
                refreshByClickingElder(0);// potential problem: if the elder view has blocked, cannot refresh
            }
			@Override
    		protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                e.printStackTrace();  
            }
		}.start();
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
	
	private void inflateEldersView(final List<Elder> elders){
		lvEldersView.setAdapter(new ElderAdapter(layoutInflater,elders));
		lvEldersView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				currentElderId=(int) id;
				currentElderPosition = position;
				for(int i=0,len = lvEldersView.getChildCount();i<len;i++){// single choice maintenance
					View elderView = (View)lvEldersView.getChildAt(i);
					if(i==position-lvEldersView.getFirstVisiblePosition()){
					    elderView.setBackgroundResource(R.drawable.border_photo);//add a border to photo
					}else{
						 elderView.setBackgroundResource(0);//remove background resource
					}
				}
				//Toaster.showLong(getActivity(), lvEldersView.getChildCount()+":"+position+":"+lvEldersView.getFirstVisiblePosition());
				getElderItems(elders.get(position));
				getElderCarer(elders.get(position));
				confirmButton.setEnabled(false);
				elderRecords=new ElderRecord(currentElderId, currentCarerId);
			}
		});      
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
				String itemName = elderItem.getCareItemName();
				int itemId = elderItem.getId();
				cbCheckBox.toggle();
				if(cbCheckBox.isChecked()){
					confirmButton.setEnabled(true);
//					Toaster.showShort(getActivity(), itemName+" checked");
					elderRecords.addElderItem(itemId,itemName );//item.getId() --equals-- new Long(id).intValue()
				}else{
					elderRecords.removeElderItem(itemId, itemName);
//					Toaster.showShort(getActivity(), elderRecords.toString());
					if(elderRecords.isEmpty()){
						//TODO should do something else
						confirmButton.setEnabled(false);
					}
				}
			}
        	
        });
        mViews.setAdapter(mAdapter);
	}
	
	private void getElderCarer(final Elder elder){//TODO
		new LoadElderCarerTask(getActivity(),dbManager,elder){
    		@Override
            protected void onSuccess(List<Carer> carers) throws Exception {
                super.onSuccess(carers);
                if(carers!=null&&carers.size()>0){
                    
                    historyCarers = carers;
                    Carer currentCarer = historyCarers.get(0);
                    currentCarerId = carers.get(0).getId();
                    eventBus.post(new RefreshCarerEvent(currentCarer));
                 }else{
                	 currentCarerId=0;
                	 eventBus.post(new RefreshCarerEvent(null));
                      }
            }
    		@Override
    		protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                	e.printStackTrace();
            }
    	}.start();
	}
	
	private void refreshByClickingElder(final int position){
		
		lvEldersView.postDelayed(new Runnable(){

			@Override
			public void run() {
				if(lvEldersView.getChildCount()<=0){
					Toaster.showShort(getActivity(), "cannot init because no elders' view");
					return;
				}
            lvEldersView.performItemClick(
            		lvEldersView.getChildAt(position),
            		position,
            		lvEldersView.getAdapter().getItemId(position));
            
			}
		}, 300);
		confirmButton.setEnabled(false);
		elderRecords=new ElderRecord(currentElderId, currentCarerId);
	}
	
	@Subscribe
	public void refreshScreen(RefreshScreenEvent event){
		refreshByClickingElder(currentElderPosition);
	}
	
	@OnClick(R.id.items_submit)
	public void submitElderRecords(){
		if(currentCarerId==0){
			Toaster.showShort(getActivity(), getResources().getString(R.string.message_no_carer));
			refreshByClickingElder(currentElderPosition);
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
				refreshByClickingElder(currentElderPosition);
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
