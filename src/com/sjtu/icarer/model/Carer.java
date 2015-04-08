package com.sjtu.icarer.model;

import java.io.Serializable;
import java.sql.Date;

public class Carer extends User implements Serializable{
	
	/**
	 * @author shiykang
	 * @since 
	 * 
	 * NOTE: id: staff id
	 */
	private static final long serialVersionUID = -1874253476056538032L;
	private int elderId;
	private int areaId;
	private Date workDate;

	public Carer(){
	}
	
	public Carer(int id){
		setId(id);
	}
	public Date getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}

	public int getElderId() {
		return elderId;
	}

	public void setElderId(int elderId) {
		this.elderId = elderId;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	

}
