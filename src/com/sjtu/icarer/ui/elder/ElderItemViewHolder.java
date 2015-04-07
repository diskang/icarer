package com.sjtu.icarer.ui.elder;

import com.sjtu.icarer.R;
import com.sjtu.icarer.common.config.Mapping;
import com.sjtu.icarer.common.view.CircleButton;
import com.sjtu.icarer.model.ElderItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ElderItemViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
	 private TextView itemInfoView;
    private ImageView itemIconImage;
//    @InjectView(R.id.cb_item_hint) private CircleButton itemHintView;
    private OnElderItemClickListener itemClickListener;
    
	public ElderItemViewHolder(View itemView, OnElderItemClickListener listener) {
		super(itemView);
		itemInfoView = (TextView)itemView.findViewById(R.id.tv_item_info);
		itemIconImage=(ImageView)itemView.findViewById(R.id.iv_item_icon);
		this.itemClickListener = listener;
		itemView.setOnClickListener(this);
	}
	
	public void bindItem(ElderItem item) {
		String name =item.getCareItemName();
		String icon =item.getIcon();
		int resId = Mapping.icons.containsKey(icon)?
		        Mapping.icons.get(icon):R.drawable.default_user;
		itemInfoView.setText(name);
		itemIconImage.setBackgroundResource(resId);
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
