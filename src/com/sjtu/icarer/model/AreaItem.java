package com.sjtu.icarer.model;

import java.io.Serializable;

public class AreaItem extends BaseItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1247093774775662657L;
	private int geroId;
	private String name;
	private int frequency;
	private int submitTimesToday;
	
	public AreaItem() {
		super();
	}
	public int getGeroId() {
		return geroId;
	}
	public void setGeroId(int geroId) {
		this.geroId = geroId;
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
	public int getSubmitTimesToday() {
		return submitTimesToday;
	}

	public void setSubmitTimesToday(int submitTimesToday) {
		this.submitTimesToday = submitTimesToday;
	}
}
