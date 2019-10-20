package tk.cavinc.yclients.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import tk.cavinc.yclients.utils.App;

//https://www.journaldev.com/23653/android-oreo-implicit-and-explicit-broadcast-receiver
//http://qaru.site/questions/7202892/oreo-broadcast-receiver-not-working
//https://jollydroid.ru/notebook/2019-06-28-android-broadcast-old-way

public class CallReceiver extends BroadcastReceiver {
    private static final String LOCK_STATE_RINGING = "ring";
    private static boolean incomingCall = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = App.getSharedPreferences();

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                // входящие
                //Bundle bundle = intent.getExtras();
               // String phoneNr= bundle.getString("incoming_number");
                String phoneNr = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Toast.makeText(context,"EXTRA_STATE :: "+phoneNr,Toast.LENGTH_LONG).show();
                if (phoneNr !=null) {
                    Intent sendData = new Intent(context, SendService.class);
                    sendData.putExtra("phone", phoneNr);
                    sendData.putExtra("direct", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(LOCK_STATE_RINGING,true);
                    editor.apply();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        context.startForegroundService(sendData);
                    } else {
                        context.startService(sendData);
                    }
                }
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                //Телефон находится в режиме звонка (набор номера при исходящем звонке / разговор)
                // вызов наружу
                if (pref.getBoolean(LOCK_STATE_RINGING,false)) {
                    return;
                }
                //Bundle bundle = intent.getExtras();
                //String phoneNr= bundle.getString("incoming_number");
                String phoneNr = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Toast.makeText(context,"OFFHOOK :: "+phoneNr,Toast.LENGTH_LONG).show();
                if (phoneNr != null) {
                    Intent sendData = new Intent(context, SendService.class);
                    sendData.putExtra("phone", phoneNr);
                    sendData.putExtra("direct", 1);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        context.startForegroundService(sendData);
                    } else {
                        context.startService(sendData);
                    }
                }
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                // завершение вызова
                Toast.makeText(context,"STATE_IDLE",Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(LOCK_STATE_RINGING,false);
                editor.apply();
            }else if (phoneState.equals(TelephonyManager.EXTRA_INCOMING_NUMBER)) {

                Toast.makeText(context,"INCOING_NUMBER",Toast.LENGTH_LONG).show();
            }
        }
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")){
            Toast.makeText(context,"PROCESS_OUTGOING_CALLS",Toast.LENGTH_LONG).show();
        }
    }
}
