package com.sjtu.icarer.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.Gson;

public class AreaRecord {
    private int areaId;
    private int staffId;
    private Set<item> areaItem;
    private Date finishTime;
    private boolean isSubmit;
    
    public AreaRecord() {
    	areaItem = new HashSet<AreaRecord.item>();
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
	
	public boolean isEmpty(){
		return areaItem.isEmpty();
	}
	
	public class item{
		/*
		 * equals and hashCode
		 * see https://stackoverflow.com/questions/27581/what-issues-should-be-considered-when-overriding-equals-and-hashcode-in-java/27609#27609
		 * */
    	int id;
        String name;
        public item(int id, String name) {
			this.id = id;
			this.name = name;
		}
        
        public int getId(){
        	return id;
        }
        
        public String getName(){
        	return name;
        }
        
        @Override
	    public boolean equals(Object obj){
	    	if(!(obj instanceof item)) return false;
	    	if(obj == this) return true;
	    	item i = (item) obj;
	    	return new EqualsBuilder()
	    	    .append(id, i.id)
	    	    .append(name, i.name)
	    	    .isEquals();
	    }
        
        @Override
        public int hashCode(){
        	return new HashCodeBuilder(17, 31)
        	    .append(id)
        	    .append(name)
        	    .toHashCode();
        }
    }
    @Override
    public String toString(){
    	Gson gson = new Gson();
    	return gson.toJson(this);
    }
    
    public boolean isSubmit() {
		return isSubmit;
	}

	public void setSubmit(boolean isSubmit) {
		this.isSubmit = isSubmit;
	}
}
