package com.sjtu.icarer.ui.elder;

import com.sjtu.icarer.model.ElderItem;

public class HeaderOrItemSection {
    
    public static enum ELDER_ITEM_TAG{
    	FUTURE,CURRENT,OVERDUE,UNKNOWN
    }
	 public int sectionManager;

    public int sectionFirstPosition;

    public boolean isHeader;

    public String text;

    public ElderItem elderItem;
     
    public ELDER_ITEM_TAG tag;
     
    public HeaderOrItemSection(String text, int sectionManager,
            int sectionFirstPosition, ELDER_ITEM_TAG tag) {
        this.isHeader = true;
        this.text = text;
        this.sectionManager = sectionManager;
        this.sectionFirstPosition = sectionFirstPosition;
        this.tag = tag;
    }
    public HeaderOrItemSection(ElderItem item, int sectionManager, int sectionFirstPosition, ELDER_ITEM_TAG tag){
    	this.isHeader = false;
    	this.elderItem = item;
    	this.sectionManager = sectionManager;
    	this.sectionFirstPosition = sectionFirstPosition;
    	this.tag = tag;
    }
}
