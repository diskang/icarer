package com.sjtu.icarer.core.web;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.sjtu.icarer.events.NetworkErrorEvent;
import com.sjtu.icarer.events.RestAdapterErrorEvent;
import com.sjtu.icarer.events.UnAuthorizedErrorEvent;
import com.sjtu.icarer.model.HttpWrapper;
import com.squareup.otto.Bus;

public class RestErrorHandler implements ErrorHandler {

    private Bus bus;

    public RestErrorHandler(Bus bus) {
        this.bus = bus;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
//        if(cause != null) {
//            if (cause.getKind().equals(Kind.NETWORK)) {/** An {@link IOException} occurred while communicating to the server. */
//            	
//                bus.post(new NetworkErrorEvent(cause));
//                
//                LogUtils.d("network error");
//            } else if(cause.getResponse() == null){
//            	//do something
//            	LogUtils.d("no resp");
//            } else if( cause.getResponse().getStatus()==401){
//            	bus.post(new NetworkErrorEvent(cause));
//            } else if(cause.getResponse().getStatus()>=400){
//            	//client request error or server error, should do something
//            } else {//HTTP code =200
//            	handleCustomHttpError(cause);
//            }     
//        }
    	Response r = cause.getResponse();
        if (r != null && r.getStatus() == 401) {
        	bus.post(new UnAuthorizedErrorEvent(cause));
        }else{
        	handleCustomHttpError(cause);
        	bus.post(new UnAuthorizedErrorEvent(cause));
        }
        return cause;
    }

    private void handleCustomHttpError(RetrofitError cause){
    	try {
    		HttpWrapper<?> errorResponse = (HttpWrapper<?>) cause.getBodyAs(HttpWrapper.class);
    		if(errorResponse.getStatus()==401){
    			bus.post(new UnAuthorizedErrorEvent(cause));
    		}else if(errorResponse.getStatus()==200){//200 OK
    			// great work here
    			
    		}else{
    			bus.post(new NetworkErrorEvent(cause));
    		}
		} catch (Exception e) {
			bus.post(new RestAdapterErrorEvent(cause));
		}
		
	}
    
}
