package com.sjtu.icarer.model;

/***************************
 * 
 * 此类为监测设备类，定义设备设备的基本信息
 * 
 * 
************************* */

public class RunningDevice {
	public String DeviceId = null;
	public String DeviceMac = null;
	public String DeviceName = null;
	public String ElderId = null;
	public String RoomNo = null;
	public String Gero_id = null;
	public String Type = null;
	
	public RunningDevice(){
		DeviceId = "";
		DeviceMac = "";
		DeviceName = "";
		ElderId = "";
		RoomNo = "";
		Gero_id = "";
		Type = "";
	}
	
	public void SetDeviceId(String localId){
		this.DeviceId=localId;
	}
	
	public void SetDeviceMac(String localMac){
		this.DeviceMac = localMac;
	}
	
	public void SetDeviceName(String localName){
		this.DeviceName = localName;
	}
	
	public void SetDeviceElderId(String localElderId){
		this.ElderId = localElderId;
	}
	
	public void SetDeviceRoomNo(String localRoomNo){
		this.RoomNo = localRoomNo;
	}
	
	public void SetDeviceGero_id(String localGeroId){
		this.Gero_id = localGeroId;
	}
	
	public void SetDeviceType(String localDeviceType){
		this.Type = localDeviceType;
	}
	
	
	public String GetDeviceId(){
		return this.DeviceId;
	}
	
	public String GetDeviceMac(){
		return this.DeviceMac;
	}
	
	public String GetDeviceName(){
		return this.DeviceName;
	}
	
	public String GetDeviceElerId(){
		return this.ElderId;
	}
	
	public String GetDeviceGeroId(){
		return this.Gero_id;
	}
	
	public String GetDeviceRoomNo(){
		return this.RoomNo;
	}
	
	public String GetDeviceType(){
		return this.Type;
	}
	
}


