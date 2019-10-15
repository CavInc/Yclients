package tk.cavinc.yclients.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import tk.cavinc.yclients.utils.App;

import static tk.cavinc.yclients.ui.MainActivity.URL_STR;

public class SendService extends Service {
    private SharedPreferences mSharedPreferences;

    public SendService() {
        mSharedPreferences = App.getSharedPreferences();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /*
    задача приложения получить номер телефона входящего или исходящего при начале звонка и тут же его
     отправить по адресу http (указанному в настройках приложения)
     так же POST в этот адрес направить tel= номер телефона и types = incoming или outgoing
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String phone = intent.getStringExtra("phone");
        int mode = intent.getIntExtra("direct",0);
        final String url = mSharedPreferences.getString(URL_STR,null);
        final String modeStr;
        if (mode ==0) {
            modeStr = "incoming";
        } else {
            modeStr = "outgoing";
        }

        // проверяем наличее сети
        if (isOnline(App.getContext())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    send(url, phone, modeStr);
                }
            }).start();
        }
        return START_NOT_STICKY;
    }


    private String getRequestMessage( HttpURLConnection conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));
        StringBuilder x = new StringBuilder();
        String output;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            x.append(output);
        }
        return x.toString();
    }

    private void send(String mUrl,String phone,String mode){
        try {
            URL url = new URL(mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            conn.setReadTimeout(20000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setChunkedStreamingMode(0);
            conn.setRequestMethod("POST");

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("tel", phone)
                    .appendQueryParameter("types", mode);
            String query = builder.build().getEncodedQuery();

            //conn.setRequestProperty("Content-Length", String.valueOf(query.length()));

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();



            //conn.connect();

            /*
            conn.connect();

            StringBuilder tokenUri=new StringBuilder("tel=");
            tokenUri.append(URLEncoder.encode(phone,"UTF-8"));
            tokenUri.append("&types=");
            tokenUri.append(URLEncoder.encode(mode,"UTF-8"));

            System.out.println(tokenUri.toString());

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
            outputStreamWriter.write(tokenUri.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();
            */


            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String res = getRequestMessage(conn);
                System.out.println(res);
            } else {
                System.out.println(conn.getResponseMessage());
            }
            conn.disconnect();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
