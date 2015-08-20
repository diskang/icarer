package com.sjtu.icarer.events;

import com.sjtu.icarer.model.Elder;


public class ElderClickEvent {
	private final Elder elder;
	
    public ElderClickEvent(Elder elder) {
		this.elder = elder;
		//this.type = type;
	}
    
    public Elder getElder(){
    	return this.elder;
    }
}
