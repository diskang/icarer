package com.sjtu.icarer.model;

import java.io.Serializable;
import java.sql.Time;

import com.google.gson.Gson;

public class ElderItem extends BaseItem implements Serializable{
    /**
	 * 
	 */
    private static final long serialVersionUID = 2494480166901792104L;
    
    private int careItemId;
    private String careItemName;
    private int elderId;
    private Time startTime;
    private Time EndTime;
    private int submitTimesToday;
    
	public ElderItem() {
    	super();
	}

	public int getCareItemId() {
		return careItemId;
	}

	public void setCareItemId(int careItemId) {
		this.careItemId = careItemId;
	}

	public String getCareItemName() {
		return careItemName;
	}

	public void setCareItemName(String careItemName) {
		this.careItemName = careItemName;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return EndTime;
	}

	public void setEndTime(Time endTime) {
		EndTime = endTime;
	}

	public int getElderId() {
		return elderId;
	}

	public void setElderId(int elderId) {
		this.elderId = elderId;
	}

	public int getSubmitTimesToday() {
		return submitTimesToday;
	}

	public void setSubmitTimesToday(int submitTimesToday) {
		this.submitTimesToday = submitTimesToday;
	}
	
    @Override
    public String toString(){
    	Gson gson = new Gson();
    	return gson.toJson(this);
    }
}
