package com.sjtu.icarer.ui.area;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.sjtu.icarer.R;
import com.sjtu.icarer.common.view.CircleButton;
import com.sjtu.icarer.model.AreaItem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AreaItemAdapter extends BaseAdapter{
	LayoutInflater inflater;
	public AreaItemAdapter(final LayoutInflater inflater,
			final List<AreaItem> elements) {
		this.inflater= inflater;
		//setItems()
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
	    if (view != null) {
	      holder = (ViewHolder) view.getTag();
	    } else {
	      view = inflater.inflate(R.layout.item_item, parent, false);
	      holder = new ViewHolder(view);
	      view.setTag(holder);
	    }

	    holder.itemInfoView.setText("John Doe");
	    // etc...

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

	    public ViewHolder(View view) {
	      ButterKnife.inject(this, view);
	    }
	  }
}