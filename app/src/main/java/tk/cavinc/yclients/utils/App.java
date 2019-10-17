package tk.cavinc.yclients.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import tk.cavinc.yclients.services.CallReceiver;

/**
 * Created by cav on 11.10.19.
 */

public class App extends Application {
    private static Context sContext;
    private static SharedPreferences sSharedPreferences;

    private CallReceiver mCallReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this.getBaseContext();
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            IntentFilter filter = new IntentFilter();
            filter.addAction(getPackageName()+"android.intent.action.PHONE_STATE");
            mCallReceiver = new CallReceiver();
            registerReceiver(mCallReceiver,filter);
        }
    }

    public static Context getContext() {
        return sContext;
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }
}
