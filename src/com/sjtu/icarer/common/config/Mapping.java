package com.sjtu.icarer.common.config;

import java.util.HashMap;
import java.util.Map;

import com.sjtu.icarer.R;




public class Mapping {
	public static String defaultItemImage = "drawable://"+R.drawable.ic_launcher;
	public static String defaultPersonImage = "drawable://"+R.drawable.head_blank;

	public static HashMap<String,String> RoomItemMapping = new HashMap<String,String>();
	public static HashMap<String,String> ElderItemMapping = new HashMap<String,String>();
	public static HashMap<String,String> ElderPicMapping = new HashMap<String,String>();
	public static Map<String, Integer> icons = new HashMap<String, Integer>();
	
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
	
	

	static {
		 icons.put("balcony_clean",      R.drawable.balcony_clean);
		 icons.put("beard_shave",        R.drawable.beard_shave);
	    icons.put("bedsheet_change",    R.drawable.bedsheet_change);
	    icons.put("clothes_change",     R.drawable.clothes_change);
	    icons.put("clothes_on_off",     R.drawable.clothes_on_off);
	    icons.put("clothes_wash",       R.drawable.clothes_wash);
	    icons.put("diaper_change",      R.drawable.diaper_change);
	    icons.put("diaper_wash",        R.drawable.diaper_wash);
	   
	    icons.put("face_wash",          R.drawable.face_wash);
	    icons.put("feet_wash",          R.drawable.feet_wash);
	    icons.put("food_comminute",     R.drawable.food_comminute);
	    icons.put("food_feed",          R.drawable.food_feed);
	    icons.put("hair_comb",          R.drawable.hair_comb);
	    icons.put("hands_wash",         R.drawable.hands_wash);
	    icons.put("medicine_distribute",R.drawable.medicine_distribute);
	    icons.put("mental_nursing",     R.drawable.mental_nursing);
	    icons.put("mouth_wash",         R.drawable.mouth_wash);
	    icons.put("nail_cut",           R.drawable.nail_cut);
	    icons.put("room_clean",         R.drawable.room_clean);
	    icons.put("shower_take",        R.drawable.shower_take);
	    icons.put("teeth_brush",        R.drawable.teeth_brush);
	    icons.put("toilet_assist",      R.drawable.toilet_assist);
	    icons.put("toilet_clean",       R.drawable.toilet_clean);
	    icons.put("water_drink",        R.drawable.water_drink);
	    icons.put("water_get",          R.drawable.water_get);
	   
	}

	
}
