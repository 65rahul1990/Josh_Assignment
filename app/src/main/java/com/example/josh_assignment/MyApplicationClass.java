package com.example.josh_assignment;

import android.app.Application;
import android.content.SharedPreferences;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplicationClass extends Application {
    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/TitilliumWeb-SemiBold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        sharedPreferences =  getSharedPreferences("PAGE", MODE_PRIVATE);

    }

    public static SharedPreferences getSharedPreferences(){
        return sharedPreferences;
    }

    public static void handlePageSize(int isFirstLogin){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("page", isFirstLogin);
        editor.apply();
    }
}
