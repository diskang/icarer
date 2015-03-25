package com.sjtu.icarer.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class AreaRecord {
    private int areaId;
    private int staffId;
    private Set<item> areaItem;
    private Date finishTime;
    
    public AreaRecord() {
	}
    
    public AreaRecord(int areaId, int staffId) {
		this.areaId = areaId;
		this.staffId = staffId;
		areaItem = new HashSet<AreaRecord.item>();
	}
    
	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
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
	
    public Set<item> getAreaItem() {
		return areaItem;
	}

	public void setAreaItem(Set<item> areaItem) {
		this.areaItem = areaItem;
	}
	
	public void addAreaItem(int id, String name){
		areaItem.add(new item(id, name));
	}
	
	public void removeAreaItem(int id, String name){
		areaItem.remove(new item(id, name));
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
