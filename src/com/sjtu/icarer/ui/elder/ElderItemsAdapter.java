package com.sjtu.icarer.ui.elder;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sjtu.icarer.R;
import com.sjtu.icarer.common.view.superslim.GridSLM;
import com.sjtu.icarer.common.view.superslim.LinearSLM;
import com.sjtu.icarer.model.ElderItem;

/**
 *
 */
public class ElderItemsAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0x01;

    private static final int VIEW_TYPE_ITEM_CHECKABLE = 0x00;
    
    private static final int VIEW_TYPE_ITEM_CUMULATE = 0x10;

    private static final int LINEAR = 0;
    
    private final List<HeaderOrItemSection> mItems;

    private int mHeaderDisplay;

    private boolean mMarginsFixed;

    private final Context mContext;

    private OnElderItemClickListener onElderItemClickListener;
    
    public ElderItemsAdapter(Context context, int headerMode, List<ElderItem> elderItems) {
        mContext = context;
        //final String[] countryNames = context.getResources().getStringArray(R.array.country_names);
        mHeaderDisplay = headerMode;

        mItems = new ItemGrouping(elderItems).getItemSectionList();
    }

    public boolean isItemHeader(int position) {
        return mItems.get(position).isHeader;
    }

    public String itemToString(int position) {
        return mItems.get(position).text;
    }

    public List<HeaderOrItemSection> getHeaderOrItems(){
    	return mItems;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.elderitems_section_header, parent, false);
            
            return new HeaderViewHolder(view);
        } else if(viewType==VIEW_TYPE_ITEM_CHECKABLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_checkable_item, parent, false);
            
            return new ElderCheckableItemViewHolder(view,onElderItemClickListener);
        } else{//VIEW_TYPE_ITEM_CUMULATE for unknown elder items
        	view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cumulate_item, parent, false);
        	return new ElderCumulateItemViewHolder(view,onElderItemClickListener);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HeaderOrItemSection item = mItems.get(position);
        final View itemView = holder.itemView;

        if(item.isHeader){
        	((HeaderViewHolder)holder).bindItem(item.text);
        	
        }else if(item.tag==HeaderOrItemSection.ELDER_ITEM_TAG.UNKNOWN){
        	((ElderCumulateItemViewHolder)holder).bindItem(item.elderItem);
        }else{
        	((ElderCheckableItemViewHolder)holder).bindItem(item.elderItem);
        }

        final GridSLM.LayoutParams lp = new GridSLM.LayoutParams(
                itemView.getLayoutParams());
        // Overrides xml attrs, could use different layouts too.
        if (item.isHeader) {
            lp.headerDisplay = mHeaderDisplay;
            if (lp.isHeaderInline() || (mMarginsFixed && !lp.isHeaderOverlay())) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            lp.headerEndMarginIsAuto = !mMarginsFixed;
            lp.headerStartMarginIsAuto = !mMarginsFixed;
        }
        lp.setSlm(item.sectionManager == LINEAR ? LinearSLM.ID : GridSLM.ID);
        lp.setColumnWidth(mContext.getResources().getDimensionPixelSize(R.dimen.grid_column_width));
        lp.setFirstPosition(item.sectionFirstPosition);
        itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemViewType(int position) {
    	HeaderOrItemSection hiSection= mItems.get(position);
    	if(hiSection.isHeader)return VIEW_TYPE_HEADER;
    	
    	switch(hiSection.tag){//unknown items can be post multiple times
    	case UNKNOWN:
    		return VIEW_TYPE_ITEM_CUMULATE;
    	default:
    		return VIEW_TYPE_ITEM_CHECKABLE;
    	}
    	
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    
    @Override
    public long getItemId(int position){
    	HeaderOrItemSection section= mItems.get(position);
    	if(section.isHeader){
    		return position;
    	}else{
    		return section.elderItem.getId();
    	}
    }
    
    public void setElderItemClickListener(OnElderItemClickListener listener){
    	this.onElderItemClickListener = listener;
    };

    public void setHeaderDisplay(int headerDisplay) {
        mHeaderDisplay = headerDisplay;
        notifyHeaderChanges();
    }

    public void setMarginsFixed(boolean marginsFixed) {
        mMarginsFixed = marginsFixed;
        notifyHeaderChanges();
    }

    private void notifyHeaderChanges() {
        for (int i = 0; i < mItems.size(); i++) {
            HeaderOrItemSection item = mItems.get(i);
            if (item.isHeader) {
                notifyItemChanged(i);
            }
        }
    }

}
