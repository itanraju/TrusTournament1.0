package com.trust.tournamentdemo.activiy;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.trust.tournamentdemo.OpenAds.AppOpenManager;
import com.trust.tournamentdemo.R;

public class MyApplication extends Application {

    private static MyApplication mInstance;
    private static Context context;
    AppOpenManager appOpenManager;
    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = getApplicationContext();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        appOpenManager = new AppOpenManager(this);
    }
}
