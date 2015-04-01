package com.sjtu.icarer.events;

import com.sjtu.icarer.model.Carer;

public class RefreshCarerEvent {
	private final Carer carer;
	//private final int type;
	//public final static int ELDER_CARER=0;
	//public final static int AREA_CARER=1;
    public RefreshCarerEvent(Carer carer) {
		this.carer = carer;
		//this.type = type;
	}
    
    public Carer getCarer(){
    	return carer;
    }
//    public Boolean isElderCarer(){
//    	return type ==ELDER_CARER;
//    }
//    
//    public Boolean isAreaCarer(){
//    	return type ==AREA_CARER;
//    }
}
