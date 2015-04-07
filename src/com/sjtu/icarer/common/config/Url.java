package com.sjtu.icarer.common.config;

public final class Url {
//	public static final String HOME_URL = "http://202.120.38.227";
//	public static final String SOAP_URL = "http://202.120.38.227/laobanHealthcare/Service1.asmx/";
//	public static final String IMG_URL = "http://202.120.38.227:8088/HouseCare/get_elder_photo.jsp";
//	public static final String DOWNLOAD_URL = "http://202.120.38.227:8088/HouseCare/get_elder_photo.jsp";
	public static final String HOME_URL = "http://121.41.46.189";
	public static final String SOAP_URL = "http://121.41.46.189:8088/icare/Service1.asmx/";
	public static final String IMG_URL = "http://121.41.46.189/HouseCare/elderphoto";
	public static final String IMG_URL_STAFF = "http://121.41.46.189/HouseCare/staffphoto";
//	public static final String UPDATE_URL = "http://121.41.46.189:8080/HouseCare/download";
	public static final String UPDATE_URL = "http://121.41.46.189/HouseCare/upload/icarer.apk";
	
	/**
     * Base URL for all requests
     */
//    public static final String URL_BASE = "http://202.120.38.227:8088";
    public static final String URL_BASE = "http://121.41.46.189:8080";
    public static final String URL_PREFFIX="/resthouse/api/service";
//    public static final String URL_PREFFIX =   "/resthouse/api/web";
    
    public static final String URL_DOWNLOAD = "/resthouse/download";
    public static final String URL_OBJECT_DOWNLOAD = "/resthouse/downloadObject";;
    
    //����
    public static final String URL_AREAS_FRAG = "/gero/{gid}/area";
    public static final String URL_AREAS = URL_PREFFIX + URL_AREAS_FRAG;
    
    public static final String URL_AREA_FRAG =     "/gero/{gid}/area/{aid}";
    public static final String URL_AREA = URL_PREFFIX + URL_AREA_FRAG;
    //����URL����digest����Կ
    public static final String URL_USER_KEY =    "/resthouse/pad/login";
    //
    public static final String URL_USER_FRAG =     "/user/{uid}";
    public static final String URL_USER = URL_PREFFIX + URL_USER_FRAG;
    //
    public static final String URL_USER_ELDERS_FRAG =     "/gero/{gid}/elder";
    public static final String URL_USER_ELDERS = URL_PREFFIX + URL_USER_ELDERS_FRAG;
    // ����Ժ����Ŀ�б�
    public static final String URL_CARE_ITEMS_FRAG= "/gero/{gid}/care_item";
    public static final String URL_CARE_ITEMS = URL_PREFFIX + URL_CARE_ITEMS_FRAG;
    
    public static final String URL_AREA_ITEMS_FRAG= "/gero/{gid}/area_item";
    public static final String URL_AREA_ITEMS = URL_PREFFIX + URL_AREA_ITEMS_FRAG;
    // ��ȡ���˵���Ŀ�б�
    public static final String URL_ELDER_ITEMS_FRAG="/gero/{gid}/elder/{eid}/care_item";
    public static final String URL_ELDER_ITEMS = URL_PREFFIX + URL_ELDER_ITEMS_FRAG;
    // ��ȡĳԱ�����ϰ���Ϣ�б�
    public static final String URL_SCHEDULE_PLAN_FRAG = "/gero/{gid}/staff/{sid}/schedule";
    public static final String URL_SCHEDULE_PLAN = URL_PREFFIX + URL_SCHEDULE_PLAN_FRAG;
    //���������˵ĸ����ϵ
    public static final String URL_CAREWORK_FRAG = "/gero/{gid}/carework";
    public static final String URL_CAREWORK = URL_PREFFIX + URL_CAREWORK_FRAG;
    //�����ͷ���ĸ����ϵ
    public static final String URL_AREAWORK_FRAG = "/gero/{gid}/areawork";
    public static final String URL_AREAWORK = URL_PREFFIX + URL_AREAWORK_FRAG;
    // ��ȡ���˵ĸ��𻤹�
    public static final String URL_ELDER_CARERS_FRAG = "/gero/{gid}/elder/{eid}/duty_carer";
    public static final String URL_ELDER_CARERS = URL_PREFFIX + URL_ELDER_CARERS_FRAG;
    // ��ȡ����ĸ��𻤹�
    public static final String URL_AREA_CARERS_FRAG = "/gero/{gid}/area/{aid}/duty_carer";
    public static final String URL_AREA_CARERS = URL_PREFFIX + URL_AREA_CARERS_FRAG;
    
    //��Ŀ��¼
    public static final String URL_CAREWORK_RECORD_FRAG = "/gero/{gid}/carework_record";
    public static final String URL_CAREWORK_RECORD = URL_PREFFIX + URL_CAREWORK_RECORD_FRAG;
    
    public static final String URL_AREAWORK_RECORD_FRAG = "/gero/{gid}/areawork_record";
    public static final String URL_AREAWORK_RECORD = URL_PREFFIX + URL_AREAWORK_RECORD_FRAG;
}
