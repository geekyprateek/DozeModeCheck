package com.example.prateek.hh_poc;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Prateek on 31/01/17.
 */



    public class ForegroundService extends Service {
        private static final String LOG_TAG = "ForegroundService";
        NotificationManager mNotifyManager;
        NotificationCompat.Builder mBuilder;

        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d("ServiceCheck","Service Started");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    preExecute();
                    downloadfile("http://speedtest.ftp.otenet.gr/files/test1Gb.db");
                }
            }).start();

            return START_STICKY;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public IBinder onBind(Intent intent) {
            // Used only in case of bound services.
            return null;
        }

    protected void preExecute() {

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("File Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.mipmap.ic_launcher);
//        Toast.makeText(context, "Downloading the file... The download progress is on notification bar.", Toast.LENGTH_LONG).show();
    }


    protected Void downloadfile(String...params){
        Log.d("ServiceCheck","Service Started");
        URL url;
        int count;
        try {
            url = new URL(params[0]);
            String pathl="";
            try {
                String storeDir = Environment.getExternalStorageDirectory().toString();
                String pathr = url.getPath();
                String filename = pathr.substring(pathr.lastIndexOf('/')+1);
                pathl = storeDir+"/"+filename;
                File f = new File(pathl);
                f.createNewFile();
                startForeground(400,
                        mBuilder.build());
                if(f.exists()){
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    InputStream is = con.getInputStream();
                    FileOutputStream fos = new FileOutputStream(pathl);
                    int lenghtOfFile = con.getContentLength();
                    byte data[] = new byte[1024];
                    long total = 0;

                    while ((count = is.read(data)) != -1) {
                        total += count;
                        // publishing the progress
                        mBuilder.setProgress(100, (int)((total*100)/lenghtOfFile), false);
                        Log.d("fileDownload",Integer.toString((int)(total)));
                        // Displays the progress bar on notification

                        //mNotifyManager.notify(0, mBuilder.build());
                        // writing data to output file
                        fos.write(data, 0, count);
                    }

                    is.close();
                    fos.flush();
                    fos.close();
                    Log.d("ServiceCheck","Service Closed");
                }
                else{
                    Log.e("Error","Not found: "+storeDir);

                }

            } catch (Exception e) {
                e.printStackTrace();

            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }
    }

