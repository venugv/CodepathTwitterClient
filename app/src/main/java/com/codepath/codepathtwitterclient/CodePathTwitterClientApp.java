package com.codepath.codepathtwitterclient;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.codepath.codepathtwitterclient.restclient.RestClient;

/**
 * Created by vvenkatraman on 12/14/15.
 */
public class CodePathTwitterClientApp  extends com.activeandroid.app.Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        CodePathTwitterClientApp.context = this;
        }

    public static RestClient getRestClient() {
        return (RestClient) RestClient.getInstance(RestClient.class, CodePathTwitterClientApp.context);
    }

    public static Context getContext() {
        return context;
    }
}
