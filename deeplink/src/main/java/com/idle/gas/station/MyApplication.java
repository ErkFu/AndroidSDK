package com.idle.gas.station;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;

public class MyApplication  extends Application {

    public void onCreate() {
        super.onCreate();
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
        AppLinkData.fetchDeferredAppLinkData(this,   new AppLinkData.CompletionHandler() {
            @Override
            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                if (appLinkData != null) {
                UnityMsgSender.SendMsg(appLinkData.getTargetUri().toString());
                Log.e("【SplashActivity", "appLinkData: " + appLinkData.getTargetUri().toString());
                }
                else{
                    Log.e("【SplashActivity", "appLinkData is null!");
                }
            }
        });
    }
}
