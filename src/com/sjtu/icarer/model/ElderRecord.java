package com.sjtu.icarer.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ElderRecord {
    private int elderId;
    private int staffId;
    private Set<item> elderItem;
    private Date finishTime;
    
    public ElderRecord() {
	}
    
    public ElderRecord(int elderId, int staffId) {
		this.elderId = elderId;
		this.staffId = staffId;
		elderItem = new HashSet<ElderRecord.item>();
	}
    
	public int getElderId() {
		return elderId;
	}

	public void setElderId(int elderId) {
		this.elderId = elderId;
	}

	public int getStaffId() {
		return staffId;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	
    public Set<item> getElderItem() {
		return elderItem;
	}

	public void setElderItem(Set<item> elderItem) {
		this.elderItem = elderItem;
	}
	
	public void addElderItem(int id, String name){
		elderItem.add(new item(id, name));
	}
	
	public void removeElderItem(int id, String name){
		elderItem.remove(new item(id, name));
	}
	
	protected class item{
    	int id;
        String name;
        public item(int id, String name) {
			this.id = id;
			this.name = name;
		}
    }
    
}
