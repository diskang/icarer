package com.sjtu.icarer.ui.elder;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.common.utils.lang.StringUtils;
import com.sjtu.icarer.model.Elder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ElderAdapter extends BaseAdapter{
	private final LayoutInflater inflater;
	private final List<Elder> elders;
	private final DisplayImageOptions options;
	public ElderAdapter(final LayoutInflater inflater,
			final List<Elder> elders) {
		this.inflater= inflater;
		this.elders = elders;
		this.options = new DisplayImageOptions.Builder()
		    .showImageOnLoading(R.drawable.default_user)
		    .showImageForEmptyUri(R.drawable.default_user)
		    .showImageOnFail(R.drawable.default_user)
		    .cacheInMemory(true)
		    .cacheOnDisk(true)
		    .considerExifParams(true)
		    .bitmapConfig(Bitmap.Config.RGB_565)
		    .build();
	}
	@Override
	public int getCount() {
		if(elders==null){
			return 0;
		}
		return elders.size();
	}

	@Override
	public Elder getItem(int position) {
		return elders.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getElderId();
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
	    Elder elder = getItem(position);
	    String name = elder.getName();
	    String photoUrl = elder.getPhotoUrl();
	    String FullPhotoUrl = StringUtils.isBlank(photoUrl)?"drawable://"+R.drawable.default_user://TODO change to another picture
	    		Url.URL_BASE+Url.URL_OBJECT_DOWNLOAD+"?file_url="+photoUrl;
	    
	    holder.elderInfoView.setText(name);
	    loadElderImage(FullPhotoUrl, holder.elderImageImage );
	    return view;
	}
	
	static class ViewHolder {
	    @InjectView(R.id.tv_elder_info) TextView elderInfoView;
	    @InjectView(R.id.iv_elder_image) ImageView elderImageImage;

	    public ViewHolder(View view) {
	      ButterKnife.inject(this, view);
	    }
	  }
	
	private void loadElderImage(String photoUrl, ImageView view){
		 ImageLoader.getInstance().displayImage(photoUrl, view, options);
	}

}
