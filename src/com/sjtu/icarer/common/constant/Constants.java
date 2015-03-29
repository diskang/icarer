package com.sjtu.icarer.common.constant;



public final class Constants {
	public static final String ROOM_SERVICE_ACTION = "org.sjtu.icarer.RoomService";
	public static final String ELDER_SERVICE_ACTION = "org.sjtu.icarer.ElderService";
	public static final String GERO_ID = "126";//SHOULD BE REMOVED
	public static final String ACTION_UPDATE_INFO = "COM.SJTU.ICARER.UPDATE";
	
	public static final String PREFER_GERO_ID = "GERO_ID";
	public static final String PREFER_ROOM_NUMBER = "romNumber";
	public static final String PREFER_CARER_ID = "carerId";
	public static final String PREFER_CARER_NAME = "carerName";
	
	public static final String PREFER_ELDER_NAME = "ELDER_NAME";
	public static final String PREFER_ELDER_ID = "ELDER_ID";
	public static final String PREFER_ITEM_ELDER = "ITEM_ELDER";
	public static final String PREFER_ITEM_ROOM = "ITEM_ROOM";
	
	public static final String PREFER_TIMESTAMP = "TIMESTAMP";
	
	public static final String FRAGMENT_INDEX = "com.sjtu.icarer.fragment";
	
	
	public static final class Auth {
        private Auth() {}

        /**
         * Account type id
         */
        public static final String BOOTSTRAP_ACCOUNT_TYPE = "com.sjtu.icarer";

        /**
         * Account name
         */
        public static final String BOOTSTRAP_ACCOUNT_NAME = "icarer";

        /**
         * Provider id
         */
        public static final String BOOTSTRAP_PROVIDER_AUTHORITY = "com.sjtu.icarer.sync";

        /**
         * Auth token type
         */
        public static final String AUTHTOKEN_TYPE = BOOTSTRAP_ACCOUNT_TYPE;
        /**
         * user data
         */
        public static final String ICARER_ACCOUNT_USER = "loginUser";
    }
	
	public static final class Prefer {
        private Prefer() {}
        public static final String AREA_ID = "areaId";
        public static final String AREA_FULL_NAME = "areaFullName";
        
	}
	
}
