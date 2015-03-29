package com.sjtu.icarer.core.utils;

import com.sjtu.icarer.core.app.UserAgentProvider;

import retrofit.RequestInterceptor;

public class RestAdapterRequestInterceptor implements RequestInterceptor {

    private UserAgentProvider userAgentProvider;

    public RestAdapterRequestInterceptor(UserAgentProvider userAgentProvider) {
        this.userAgentProvider = userAgentProvider;
    }

    @Override
    public void intercept(RequestFacade request) {

        // Add header to set content type of JSON
        request.addHeader("Content-Type", "application/json");
        request.addHeader("accept", "application/json");

        // Add auth info for PARSE, normally this is where you'd add your auth info for this request (if needed).
        //request.addHeader(HEADER_PARSE_REST_API_KEY, PARSE_REST_API_KEY);
        //request.addHeader(HEADER_PARSE_APP_ID, PARSE_APP_ID);

        // Add the user agent to the request.
        request.addHeader("User-Agent", userAgentProvider.get());
//        request.addEncodedQueryParam("username", "su");
//        request.addEncodedQueryParam("digest", "");
        
    }
}
