package com.sjtu.icarer.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.Gson;


public class ElderRecord {
    private int elderId;
    private int staffId;
    private Set<item> elderItem;
    private Date finishTime;
    private boolean isSubmit;
    
    public ElderRecord() {
    	elderItem = new HashSet<ElderRecord.item>();
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
	
	public boolean isEmpty(){
		return elderItem.isEmpty();
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
        	return new HashCodeBuilder(19, 29)
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
