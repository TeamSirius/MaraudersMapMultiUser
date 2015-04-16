package com.tylerlubeck.maraudersmapmultiuser.API;

import retrofit.RequestInterceptor;

/**
 * Created by Tyler on 4/16/2015.
 */
public class RequestInterceptors {
    public static class AuthorizedHeaderIntercepter implements RequestInterceptor {

        private String username;
        private String apiKey;
        public AuthorizedHeaderIntercepter(String username, String apiKey) {
            this.username = username;
            this.apiKey = apiKey;
        }

        @Override
        public void intercept(RequestFacade request) {
            String authorization = String.format("ApiKey %s:%s", username, apiKey);
            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authorization);
        }
    }
}
