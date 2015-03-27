package com.sjtu.icarer.core.login;

import javax.inject.Inject;

import com.sjtu.icarer.common.utils.SafeAsyncTask;

public class LoginManager {
	private static LoginManager instance;
    @Inject
    public LoginManager() {
		instance = this;
	}
    
    public void login(){
    	
    }
    
    private static class GetDigestKeyTask extends SafeAsyncTask<Boolean>{

		@Override
		public Boolean call() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
    
    
    public LoginManager getInstance(){
    	return instance;
    }
}
