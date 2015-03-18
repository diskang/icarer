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
		
	
		RoomItemMapping.put("��ɨ��̨",  "drawable://"+R.drawable.icon_sweep);
		RoomItemMapping.put("�������",  "drawable://"+R.drawable.icon_toilet);
		RoomItemMapping.put("�忪ˮ",   "drawable://"+R.drawable.icon_water);
		RoomItemMapping.put("��������",  "drawable://"+R.drawable.icon_sheet);
		RoomItemMapping.put("������",  "drawable://"+R.drawable.icon_room);
	 
		ElderItemMapping.put("ϴ��",   "drawable://"+R.drawable.icon_face);
		ElderItemMapping.put("ϴ��",   "drawable://"+R.drawable.icon_hand);
		ElderItemMapping.put("ϴ��",   "drawable://"+R.drawable.icon_foot);
		ElderItemMapping.put("ϴ��",   "drawable://"+R.drawable.icon_shower);
		ElderItemMapping.put("������","drawable://"+ R.drawable.icon_mental);
	}

	
}
