package com.sjtu.icarer.model;

import java.io.Serializable;

public class CareItem  extends BaseItem implements Serializable{
	
	private static final long serialVersionUID = 7500982126368426344L;
	private String name;
    private int geroId;
    private int level;
    private int frequency;
    
	public CareItem() {
	}

	public int getGeroId() {
		return geroId;
	}

	public void setGeroId(int geroId) {
		this.geroId = geroId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
}
