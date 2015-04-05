package com.sjtu.icarer.ui.elder;

public class ElderItemSection {
	 public int sectionManager;

    public int sectionFirstPosition;

    public boolean isHeader;

    public String text;

    public ElderItemSection(String text, boolean isHeader, int sectionManager,
            int sectionFirstPosition) {
        this.isHeader = isHeader;
        this.text = text;
        this.sectionManager = sectionManager;
        this.sectionFirstPosition = sectionFirstPosition;
    }
}
