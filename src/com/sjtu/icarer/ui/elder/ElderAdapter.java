package com.sjtu.icarer.ui.elder;

import com.sjtu.icarer.R;
import com.sjtu.icarer.common.view.CircleButton;
import com.sjtu.icarer.model.Elder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ElderAdapter extends BaseAdapter{
	LayoutInflater inflater;
	public ElderAdapter(final LayoutInflater inflater,
			final Elder[] elements) {
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
	      view = inflater.inflate(R.layout.elder_item, parent, false);
	      holder = new ViewHolder(view);
	      view.setTag(holder);
	    }

	    holder.elderInfoView.setText("John Doe");
	    // etc...

	    return view;
	}
	
//	@Override
//	protected void update(final int positon, final ElderAdapter elder){
//		
//	}
	static class ViewHolder {
	    @InjectView(R.id.tv_elder_info) TextView elderInfoView;
	    @InjectView(R.id.iv_elder_image) ImageView elderImageImage;
	    @InjectView(R.id.cb_elder_hint) CircleButton elderHintView;

	    public ViewHolder(View view) {
	      ButterKnife.inject(this, view);
	    }
	  }

}
