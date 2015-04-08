package com.sjtu.icarer.model;

import java.io.Serializable;


public class Elder extends User implements Serializable{
	/**
	 * NOTE: 
	 *       id:      user_id
	 *    elder_id:   elder_id
	 */
	private static final long serialVersionUID = -737490097890850021L;
	private int elderId;
	private int careLevel;
	private int areaId;

	public Elder(){
	}
	
	public Elder(int id){
		setId(id);
	}
	
	public int getElderId() {
		return elderId;
	}

	public void setElderId(int elderId) {
		this.elderId = elderId;
	}
	
	public int getCareLevel() {
		return careLevel;
	}

	public void setCareLevel(int careLevel) {
		this.careLevel = careLevel;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	
	
	
}
