package com.sjtu.icarer.ui.area;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.sjtu.icarer.R;
import com.sjtu.icarer.common.config.Mapping;
import com.sjtu.icarer.common.view.CircleButton;
import com.sjtu.icarer.common.view.IconNumberView;
import com.sjtu.icarer.model.AreaItem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AreaItemAdapter extends BaseAdapter{
	private final LayoutInflater inflater;
	private final List<AreaItem> areaItems;
	public AreaItemAdapter(final LayoutInflater inflater,
			final List<AreaItem> areaItems) {
		this.inflater= inflater;
		this.areaItems = areaItems;
		//setItems()
	}
	@Override
	public int getCount() {
		if(areaItems==null)return 0;
		return areaItems.size();
	}

	@Override
	public AreaItem getItem(int position) {
		return areaItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return areaItems.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
	    if (view != null) {
	      holder = (ViewHolder) view.getTag();
	    } else {
	      view = inflater.inflate(R.layout.item_cumulate_item, parent, false);
	      holder = new ViewHolder(view);
	      view.setTag(holder);
	    }
	    String name =  getItem(position).getName();
	    String icon =  getItem(position).getIcon();
	    Integer number = getItem(position).getSubmitTimesToday();
	    String numberString = number<100?number+"":"…";
		
	    int resId = Mapping.icons.containsKey(icon)?
	        Mapping.icons.get(icon):R.drawable.default_user;
	    holder.itemInfoView.setText(name);
	    holder.itemIconImage.setBackgroundResource(resId);
	    holder.itemNumberView.setNumber(numberString);
	    return view;
	}
	
//	@Override
//	protected void update(final int positon, final ElderAdapter elder){
//		
//	}
	static class ViewHolder {
	    @InjectView(R.id.tv_item_info) TextView itemInfoView;
	    @InjectView(R.id.iv_item_icon) ImageView itemIconImage;
	    @InjectView(R.id.cb_item_hint) CircleButton itemHintView;
	    @InjectView(R.id.iv_item_number) IconNumberView itemNumberView;
	    public ViewHolder(View view) {
	      ButterKnife.inject(this, view);
	    }
	  }
}
