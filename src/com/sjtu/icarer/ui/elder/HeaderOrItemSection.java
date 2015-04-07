package com.sjtu.icarer.ui.elder;

import com.sjtu.icarer.model.ElderItem;

public class HeaderOrItemSection {
	 public int sectionManager;

    public int sectionFirstPosition;

    public boolean isHeader;

    public String text;

     public ElderItem elderItem;
     
    public HeaderOrItemSection(String text, int sectionManager,
            int sectionFirstPosition) {
        this.isHeader = true;
        this.text = text;
        this.sectionManager = sectionManager;
        this.sectionFirstPosition = sectionFirstPosition;
    }
    public HeaderOrItemSection(ElderItem item, int sectionManager, int sectionFirstPosition){
    	this.isHeader = false;
    	this.elderItem = item;
    	this.sectionManager = sectionManager;
    	this.sectionFirstPosition = sectionFirstPosition;
    }
}
