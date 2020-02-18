package com.recluse.xicor.taskshouter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ROJIT on 5/11/2018.
 */

public class ReminderNotif extends Service  implements TextToSpeech.OnInitListener {
    TextToSpeech tts;
    @Override
    public void onCreate() {
        super.onCreate();

        tts = new TextToSpeech(this, this);

        final ReminderNotif s = this;
        final Handler handler = new Handler();
        Timer timer = new Timer();


        TimerTask timertask = new TimerTask(){
            public void run(){
                handler.post(new Runnable() {
                    public void run() {
                        Date d_today = new Date();
                        Calendar today = Calendar.getInstance();
                        today.setTime(d_today);
                        LinkedHashMap<Integer,HashMap<String,String>> all_tasks = new DBInterface(getApplicationContext()).getTasks( today.get(Calendar.YEAR) +"-"+ (today.get(Calendar.MONTH)+1)+"-"+today.get(Calendar.DAY_OF_MONTH));
                        for(Map.Entry<Integer,HashMap<String,String>> e : all_tasks.entrySet()){
                            Integer id = e.getKey();
                            HashMap<String,String> data = e.getValue();
                            String type = data.get("type");
                            String notif_type = data.get("notif_type");

                            String date = data.get("date");
                            String[] date_split = date.split("-");
                            int year = Integer.parseInt(date_split[0]);
                            int month = Integer.parseInt(date_split[1]);
                            int day = Integer.parseInt(date_split[2]);

                            String time = data.get("time");
                            String[] time_split = time.split(":");
                            int hour = Integer.parseInt(time_split[0]);
                            int minute = Integer.parseInt(time_split[1]);

                            Date d = new Date(year-1900,month-1,day,hour,minute);

                            DateTime dtOrg = new DateTime(d);

                            Calendar set_day = Calendar.getInstance();
                            set_day.setTime(d);
                            if(   today.get(Calendar.YEAR) == set_day.get(Calendar.YEAR) && today.get(Calendar.MONTH) == set_day.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) == set_day.get(Calendar.DAY_OF_MONTH) && today.get(Calendar.HOUR_OF_DAY) == set_day.get(Calendar.HOUR_OF_DAY)  && today.get(Calendar.MINUTE) == set_day.get(Calendar.MINUTE)  ){
                                alertByType(data,notif_type);
                                if(type.equals("Every Day")){
                                    DateTime dtPlusOne = dtOrg.plusDays(1);
                                    String new_date = dtPlusOne.getYear()+"-"+dtPlusOne.getMonthOfYear()+"-"+dtPlusOne.getDayOfMonth();
                                    new DBInterface(getApplicationContext()).updateTaskDatetime(id,new_date,time);
                                }
                                else if(type.equals("At Intervals")){
                                    DateTime dt = new DateTime();
                                    if(data.get("interval_type").equals("Minute(s)")){
                                        dt = dtOrg.plusMinutes(Integer.parseInt(data.get("interval_duration")));
                                    }
                                    else if(data.get("interval_type").equals("Hour(s)")){
                                        dt = dtOrg.plusHours(Integer.parseInt(data.get("interval_duration")));
                                    }
                                    else if(data.get("interval_type").equals("Day(s)")){
                                        dt = dtOrg.plusDays(Integer.parseInt(data.get("interval_duration")));
                                    }
                                    else if(data.get("interval_type").equals("Month(s)")){
                                        dt = dtOrg.plusMonths(Integer.parseInt(data.get("interval_duration")));
                                    }
                                    else if(data.get("interval_type").equals("Year(s)")){
                                        dt = dtOrg.plusYears(Integer.parseInt(data.get("interval_duration")));
                                    }
                                    new DBInterface(getApplicationContext()).updateTaskDatetime(id,dt.getYear()+"-"+dt.getMonthOfYear()+"-"+dt.getDayOfMonth(),dt.getHourOfDay()+":"+dt.getMinuteOfHour());
                                }
                            }
                        }
                    }
                });}};


        timer.scheduleAtFixedRate(timertask,20,60000);

    }

    public void alertByType(HashMap<String,String> data,String type){
        String text = data.get("task_name");
        if(type.equals("Notification with Speech")){
            speakOut(text);
            NotificationCompat.Builder nb = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.mipmap.ic_launcher))
                    .setContentTitle(text)
                    .setContentText(text)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL);
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(new Random().nextInt(1000),nb.build());
        }
        else if(type.equals("Speech Only")){
            speakOut(text);
        }
        else if(type.equals("Notification only")){
            NotificationCompat.Builder nb = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.mipmap.ic_launcher))
                    .setContentTitle(text)
                    .setContentText(text)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL);
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(new Random().nextInt(1000),nb.build());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onInit(int status) {
        try {
            if (status == TextToSpeech.SUCCESS) {

                int result = tts.setLanguage(Locale.US);
                tts.setSpeechRate(0.6f);
                tts.setPitch(1.3f);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    Toast.makeText(getApplicationContext(), "Not Supported", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(getApplicationContext(), "Supported", Toast.LENGTH_LONG).show();
                }

            } else {
//                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }


    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
