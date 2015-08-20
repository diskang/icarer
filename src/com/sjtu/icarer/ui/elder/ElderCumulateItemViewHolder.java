package com.sjtu.icarer.ui.elder;

import com.sjtu.icarer.R;
import com.sjtu.icarer.common.config.Mapping;
import com.sjtu.icarer.common.view.CircleButton;
import com.sjtu.icarer.common.view.IconNumberView;
import com.sjtu.icarer.model.ElderItem;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ElderCumulateItemViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
    private TextView itemInfoView;
    private ImageView itemIconImage;
    private CircleButton itemHintView;
    private IconNumberView itemNumberView;
    private OnElderItemClickListener itemClickListener;
    
	public ElderCumulateItemViewHolder(View itemView, OnElderItemClickListener listener) {
		super(itemView);
		itemInfoView = (TextView)itemView.findViewById(R.id.tv_item_info);
		itemIconImage=(ImageView)itemView.findViewById(R.id.iv_item_icon);
		itemHintView = (CircleButton)itemView.findViewById(R.id.cb_item_hint);
		itemNumberView = (IconNumberView)itemView.findViewById(R.id.iv_item_number);
		this.itemClickListener = listener;
		itemView.setOnClickListener(this);
	}
	public void bindItem(ElderItem item) {
		String name =item.getCareItemName();
		String icon =item.getIcon();
		
		itemInfoView.setText(name);
		
		String numberString = item.getSubmitTimesToday()<100?
				item.getSubmitTimesToday()+"":"…";
		itemNumberView.setNumber(numberString);
		
		if(itemHintView.isChecked()){
			int resId = Mapping.icons_selected.containsKey(icon)?
			        Mapping.icons_selected.get(icon):R.drawable.default_user;
			itemIconImage.setBackgroundResource(resId);
		}else{
			int resId = Mapping.icons.containsKey(icon)?
			        Mapping.icons.get(icon):R.drawable.default_user;
			itemIconImage.setBackgroundResource(resId);
			
		}
    }
	
	@Override
    public String toString() {
        return itemInfoView.getText().toString();
    }

	@Override
	public void onClick(View v) {
		if(itemClickListener!=null){
			itemClickListener.onElderItemClick(v,getAdapterPosition());
		}
		
	}
}
