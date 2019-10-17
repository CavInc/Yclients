package tk.cavinc.yclients.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import tk.cavinc.yclients.R;
import tk.cavinc.yclients.services.CallReceiver;
import tk.cavinc.yclients.services.SendService;
import tk.cavinc.yclients.utils.App;

public class MainActivity extends Activity implements View.OnClickListener {
    public static final String URL_STR = "url";
    private static final int REQUEST_PHONE = 564;
    private SharedPreferences mSharedPreferences;

    private EditText mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = App.getSharedPreferences();

        mUrl = findViewById(R.id.url_et);
        findViewById(R.id.save_bt).setOnClickListener(this);

        String url = mSharedPreferences.getString(URL_STR,null);
        if (url != null) {
            mUrl.setText(url);
        }

        mSharedPreferences = App.getSharedPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},REQUEST_PHONE);
            }
            if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG},REQUEST_PHONE);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registryBroadcard();
        }
    }

    private void registryBroadcard(){
        Intent intent = new Intent("android.intent.action.PHONE_STATE");
        intent.setClass(this, CallReceiver.class);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        store();
        super.onBackPressed();
    }

    private void store(){
        String url = mUrl.getText().toString();
        if (url.indexOf("http://") == -1){
            url = "http://"+url;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(URL_STR,url);
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        store();
        /*
        Intent sendData = new Intent(this, SendService.class);
        sendData.putExtra("phone", "333333");
        sendData.putExtra("direct", 0);
        startService(sendData);
        */
    }
}
