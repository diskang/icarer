package com.sjtu.icarer.common.config;

import java.util.HashMap;

import com.sjtu.icarer.R;




public class Mapping {
	public static String defaultItemImage = "drawable://"+R.drawable.ic_launcher;
	public static String defaultPersonImage = "drawable://"+R.drawable.head_blank;

	public static HashMap<String,String> RoomItemMapping = new HashMap<String,String>();
	public static HashMap<String,String> ElderItemMapping = new HashMap<String,String>();
	public static HashMap<String,String> ElderPicMapping = new HashMap<String,String>();
	static
	{
		
	
		RoomItemMapping.put("清扫阳台",  "drawable://"+R.drawable.icon_sweep);
		RoomItemMapping.put("整理厕所",  "drawable://"+R.drawable.icon_toilet);
		RoomItemMapping.put("冲开水",   "drawable://"+R.drawable.icon_water);
		RoomItemMapping.put("更换床单",  "drawable://"+R.drawable.icon_sheet);
		RoomItemMapping.put("整理房间",  "drawable://"+R.drawable.icon_room);
	 
		ElderItemMapping.put("洗脸",   "drawable://"+R.drawable.icon_face);
		ElderItemMapping.put("洗手",   "drawable://"+R.drawable.icon_hand);
		ElderItemMapping.put("洗脚",   "drawable://"+R.drawable.icon_foot);
		ElderItemMapping.put("洗澡",   "drawable://"+R.drawable.icon_shower);
		ElderItemMapping.put("心理护理","drawable://"+ R.drawable.icon_mental);
	}

	
}
