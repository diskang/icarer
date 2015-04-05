package com.sjtu.icarer.ui.elder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.sjtu.icarer.R;
import com.sjtu.icarer.common.view.superslim.GridSLM;
import com.sjtu.icarer.common.view.superslim.LinearSLM;
import com.sjtu.icarer.model.ElderItem;

/**
 *
 */
public class ElderItemsAdapter extends RecyclerView.Adapter<ItemSectionViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0x01;

    private static final int VIEW_TYPE_CONTENT = 0x00;

    private static final int LINEAR = 0;
     
    private final List<ElderItem> elderItems;
    
    private final List<ElderItemSection> mItems;

    private int mHeaderDisplay;

    private boolean mMarginsFixed;

    private final Context mContext;

    public ElderItemsAdapter(Context context, int headerMode, List<ElderItem> elderItems) {
        mContext = context;
        this.elderItems = elderItems;
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

    @Override
    public ItemSectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.elderitems_section_header, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_line_item, parent, false);
        }
        return new ItemSectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemSectionViewHolder holder, int position) {
        final ElderItemSection item = mItems.get(position);
        final View itemView = holder.itemView;

        holder.bindItem(item.text);

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
        return mItems.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

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
            ElderItemSection item = mItems.get(i);
            if (item.isHeader) {
                notifyItemChanged(i);
            }
        }
    }

}
