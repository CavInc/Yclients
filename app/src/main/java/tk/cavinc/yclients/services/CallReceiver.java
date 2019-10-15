package tk.cavinc.yclients.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {
    private static boolean incomingCall = false;

    @Override
    public void onReceive(Context context, Intent intent) {
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
                    context.startService(sendData);
                }
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                //Телефон находится в режиме звонка (набор номера при исходящем звонке / разговор)
                // вызов наружу
                //Bundle bundle = intent.getExtras();
                //String phoneNr= bundle.getString("incoming_number");
                String phoneNr = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Toast.makeText(context,"OFFHOOK :: "+phoneNr,Toast.LENGTH_LONG).show();
                if (phoneNr != null) {
                    Intent sendData = new Intent(context, SendService.class);
                    sendData.putExtra("phone", phoneNr);
                    sendData.putExtra("direct", 1);
                    context.startService(sendData);
                }
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                // завершение вызова
                Toast.makeText(context,"STATE_IDLE",Toast.LENGTH_LONG).show();
            }else if (phoneState.equals(TelephonyManager.EXTRA_INCOMING_NUMBER)) {

                Toast.makeText(context,"INCOING_NUMBER",Toast.LENGTH_LONG).show();
            }
        }
    }
}
