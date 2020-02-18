package com.recluse.xicor.taskshouter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.Space;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class LauncherActivity extends AppCompatActivity {


    int[][] states = new int[][] {
            new int[] {-android.R.attr.state_checked},
            new int[] {android.R.attr.state_checked},
    };

    int[] thumbColors = new int[] {
            Color.parseColor("#E74C3C"),
            Color.parseColor("#2ECC71"),
    };

    int[] trackColors = new int[] {
            Color.parseColor("#E74C3C"),
            Color.parseColor("#2ECC71"),
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_launcher, null);
        setContentView(root);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        final Spinner spinner = (Spinner) findViewById(R.id.categories);
        final ArrayList<String> categories = new ArrayList<>();
        categories.add("Tasks for Today");
        categories.add("All Tasks");
        categories.add("Disabled Tasks");
        categories.add("Calendar");
        categories.add("Previous Tasks");
        Object[] cats = categories.toArray();
        String[] s_cats = new String[cats.length];
        for(int i=0;i<=s_cats.length-1;i++){s_cats[i] = cats[i].toString();}
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getApplicationContext(),
                R.layout.spinner_textview,s_cats){
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(TypefaceUtils.load(getAssets(),"fonts/Montserrat-Medium.ttf"));
                return v;
            }
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(TypefaceUtils.load(getAssets(),"fonts/Montserrat-Medium.ttf"));
                return v;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if(spinner.getSelectedItem().toString().equals(categories.get(0))){setPrefacedTaskType(0);renderTasks(0);}
                else if(spinner.getSelectedItem().toString().equals(categories.get(1))){setPrefacedTaskType(1);renderTasks(1);}
                else if(spinner.getSelectedItem().toString().equals(categories.get(2))){setPrefacedTaskType(2);renderTasks(2);}
                else if(spinner.getSelectedItem().toString().equals(categories.get(3))){setPrefacedTaskType(3);renderTasks(3);}
                else if(spinner.getSelectedItem().toString().equals(categories.get(4))){setPrefacedTaskType(4);renderTasks(4);}
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.insert_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    insertTask();
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        spinner.setSelection(getPrefacedTaskType());

        SharedPreferences prefs = getSharedPreferences("ts_data", MODE_PRIVATE);
        boolean restoredText = prefs.getBoolean("add_task_notif", false);

        Intent i = new Intent(getApplicationContext(),ITOutsideActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),0,i,0);
            //CREATE NEW ACTIVITY FOR THIS
        NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle("Add Task - TaskShouter").setOngoing(true).setContentIntent(pi);

        NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
        nm.notify(12348,mBuilder.build());


        startService(new Intent(this,ReminderNotif.class));
    }

    public void setPrefacedTaskType(int type){
        SharedPreferences.Editor editor = getSharedPreferences("ts_data", MODE_PRIVATE).edit();
        editor.putInt("task_screen", type);
        editor.apply();
    }
    public int getPrefacedTaskType(){
        SharedPreferences prefs = getSharedPreferences("ts_data", MODE_PRIVATE);
        int restoredText = prefs.getInt("task_screen", 0);
        return restoredText;
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editor = getSharedPreferences("ts_data", MODE_PRIVATE).edit();
        editor.putInt("task_screen",0);
        editor.apply();
        super.onDestroy();
    }

    public void insertTask(){
        startActivityForResult(new Intent(this,InsertTaskActivity.class),7902);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 7902){
            if(resultCode == Activity.RESULT_OK){
                if(data.getStringExtra("result").equals("YES")){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.task_body),"Task added",Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(Color.parseColor("#1A5276"));
                    snackbar.show();
                    renderTasks(getPrefacedTaskType());
                }
                else{
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.task_body),"Task could not be added",Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(Color.parseColor("#C0392B"));
                    snackbar.show();
                    renderTasks(getPrefacedTaskType());
                }
            }
        }
        else if(requestCode == 7912){
            if(resultCode == Activity.RESULT_OK){
                if(data.getStringExtra("result").equals("YES")){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.task_body),"Task updated",Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(Color.parseColor("#1A5276"));
                    snackbar.show();
                    renderTasks(getPrefacedTaskType());
                }
                else{
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.task_body),"Task could not be updated",Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(Color.parseColor("#C0392B"));
                    snackbar.show();
                    renderTasks(getPrefacedTaskType());
                }
            }
        }
    }

    public LinkedHashMap<Integer,HashMap<String,String>> orderOnDT(LinkedHashMap<Integer,HashMap<String,String>> data){
        LinkedHashMap<Integer,HashMap<String,String>> main_data = new LinkedHashMap<Integer,HashMap<String,String>>();
        Object[] ks = data.keySet().toArray();
        for(int i=0;i<=ks.length-1;i++){
            for(int j=i+1;j<=ks.length-1;j++){
                String i_date = data.get(Integer.valueOf(String.valueOf(ks[i]))).get("date");
                String j_date = data.get(Integer.valueOf(String.valueOf(ks[j]))).get("date");
                Date i_d = new Date(Integer.parseInt(i_date.split("-")[0]),Integer.parseInt(i_date.split("-")[1]),Integer.parseInt(i_date.split("-")[2]));
                Date j_d = new Date(Integer.parseInt(j_date.split("-")[0]),Integer.parseInt(j_date.split("-")[1]),Integer.parseInt(j_date.split("-")[2]));
                if(i_d.compareTo(j_d) > 0){
                    Object a = ks[i];
                    Object b = ks[j];
                    ks[i] = b;
                    ks[j] = a;
                }
            }
        }
        for(Object k:ks){
            main_data.put(Integer.parseInt(String.valueOf(k)),data.get(k));
        }
        return main_data;
    }

    public void renderTasks(int state){
        TransitionManager.beginDelayedTransition(( (LinearLayout) findViewById(R.id.main_task_container) ), new ChangeBounds());
        LinkedHashMap<Integer,HashMap<String,String>> temp_data = new LinkedHashMap<>();
        if(state == 1 || state ==  4){
            temp_data = new DBInterface(getApplicationContext()).getAllTasks();
            temp_data = orderOnDT(temp_data);
            if(temp_data.size() == 0) {
                ((TextView) findViewById(R.id.tv_for_no_tasks)).setText("ADD A NEW TASK");
            }
            else{
                try {
                    boolean foundToday = false;
                    for (Map.Entry<Integer, HashMap<String, String>> ee : temp_data.entrySet()) {
                        if (ee.getValue().get("date").equals(new GregorianCalendar().get(Calendar.YEAR) + "-" + (Integer.valueOf(new GregorianCalendar().get(Calendar.MONTH)) + 1) + "-" + new GregorianCalendar().get(Calendar.DAY_OF_MONTH))) {
                            foundToday = true;
                            break;
                        }
                    }
                    if (foundToday == false) {
                        LinkedHashMap<Integer, HashMap<String, String>> revamp_hm = new LinkedHashMap<>();
                        Object[] temp_ks = temp_data.keySet().toArray();
                        Date today = new Date();
                        boolean insertedToday = false;
                        for (int i = 0; i <= temp_ks.length - 1; i++) {
                            try {
                                if (i == 0) {
                                    String[] pd = temp_data.get(Integer.valueOf(temp_ks[i].toString())).get("date").split("-");
                                    Date prev_day = new Date(Integer.parseInt(pd[0]) - 1900, Integer.parseInt(pd[1]) - 1, Integer.parseInt(pd[2]));
                                    if (today.compareTo(prev_day) < 0) {
                                        if (insertedToday == false) {
                                            revamp_hm.put(0, new HashMap<String, String>());
                                            insertedToday = true;
                                        }
                                        revamp_hm.put(Integer.valueOf(temp_ks[i].toString()), temp_data.get(Integer.valueOf(temp_ks[i].toString())));
                                    } else {
                                        revamp_hm.put(Integer.valueOf(temp_ks[i].toString()), temp_data.get(Integer.valueOf(temp_ks[i].toString())));
                                    }
                                } else {
                                    String[] pd = temp_data.get(Integer.valueOf(temp_ks[i - 1].toString())).get("date").split("-");
                                    String[] nd = temp_data.get(Integer.valueOf(temp_ks[i].toString())).get("date").split("-");
                                    Date prev_day = new Date(Integer.parseInt(pd[0]) - 1900, Integer.parseInt(pd[1]) - 1, Integer.parseInt(pd[2]));
                                    Date next_day = new Date(Integer.parseInt(nd[0]) - 1900, Integer.parseInt(nd[1]) - 1, Integer.parseInt(nd[2]));
                                    if (today.compareTo(prev_day) > 0 && today.compareTo(next_day) < 0) {
                                        if (insertedToday == false) {
                                            revamp_hm.put(0, new HashMap<String, String>());
                                            insertedToday = true;
                                        }
                                        revamp_hm.put(Integer.valueOf(temp_ks[i].toString()), temp_data.get(Integer.valueOf(temp_ks[i].toString())));
                                    } else {
                                        revamp_hm.put(Integer.valueOf(temp_ks[i].toString()), temp_data.get(Integer.valueOf(temp_ks[i].toString())));
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        if (insertedToday == false) {
                            revamp_hm.put(0, new HashMap<String, String>());
                        }
                        temp_data = revamp_hm;
                    }
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }
        else if(state == 0){
            Calendar c = new GregorianCalendar();
            String today = c.get(Calendar.YEAR) + "-" + (Integer.valueOf(c.get(Calendar.MONTH))+1) +"-" + c.get(Calendar.DAY_OF_MONTH);
            for(Map.Entry<Integer,HashMap<String,String>> te : new DBInterface(getApplicationContext()).getAllTasks().entrySet()){
                if(today.equals(te.getValue().get("date"))){
                    temp_data.put(te.getKey(),te.getValue());
                }
            }
            if(temp_data.size() == 0){
                ( (TextView) findViewById(R.id.tv_for_no_tasks) ).setText("NO TASKS FOR TODAY");
            }
        }
        else if(state == 2){
            for(Map.Entry<Integer,HashMap<String,String>> te : new DBInterface(getApplicationContext()).getAllTasks().entrySet()){
                if(Integer.parseInt(te.getValue().get("disable")) == 1){
                    temp_data.put(te.getKey(),te.getValue());
                }
            }
            if(temp_data.size() == 0){
                ( (TextView) findViewById(R.id.tv_for_no_tasks) ).setText("NO DISABLED TASKS");
            }
        }
        //remove elements before TODAY or after,depending on state
        if(state == 1){
            try {
                if (temp_data.size() > 0) {
                    String today_date = new GregorianCalendar().get(Calendar.YEAR) + "-" + (new GregorianCalendar().get(Calendar.MONTH) + 1) + "-" + new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
                    int r_index = 0;
                    for (Map.Entry<Integer, HashMap<String, String>> e : temp_data.entrySet()) {
                        try {
                            if (e.getValue().get("date").equals(today_date) || e.getValue().get("date") == null) {
                                break;
                            }
                        }catch (Exception ne){
                            break;
                        }
                        r_index += 1;
                    }
                    Object[] keys = temp_data.keySet().toArray();
                    for (int i = 0; i <= r_index - 1; i++) {
                        temp_data.remove(keys[i]);
                    }
                    if (temp_data.size() == 0) {
                        ((TextView) findViewById(R.id.tv_for_no_tasks)).setText("ADD A NEW TASK");
                    }
                } else {
                    ((TextView) findViewById(R.id.tv_for_no_tasks)).setText("ADD A NEW TASK");
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
        else if(state == 4){
            if(temp_data.size() > 0){
                try {
                    String today_date = new GregorianCalendar().get(Calendar.YEAR) + "-" + (new GregorianCalendar().get(Calendar.MONTH) + 1) + "-" + new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
                    int r_index = 0;
                    for (Map.Entry<Integer, HashMap<String, String>> e : temp_data.entrySet()) {
                        try {
                            if (e.getValue().get("date").equals(today_date) || e.getValue().get("date") == null) {
                                break;
                            }
                        }catch (Exception ne){
                            break;
                        }
                        r_index += 1;
                    }
                    Object[] keys = temp_data.keySet().toArray();
                    for (int i = r_index; i <= keys.length - 1; i++) {
                        temp_data.remove(keys[i]);
                    }
                    if(temp_data.size() == 0){
                        ((TextView) findViewById(R.id.tv_for_no_tasks)).setText("NO PREVIOUS TASKS");
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
            else {
                ((TextView) findViewById(R.id.tv_for_no_tasks)).setText("NO PREVIOUS TASKS");
            }
        }

        ( (LinearLayout) findViewById(R.id.main_task_container) ).removeAllViews();
        if(state != 3) {
            if (temp_data.size() > 0) {
                ((RelativeLayout) findViewById(R.id.for_no_tasks)).setVisibility(View.INVISIBLE);
                setTaskCount(temp_data.size());
                String present_date = "";
                for (Map.Entry<Integer, HashMap<String, String>> e : temp_data.entrySet()) {
                    final int id = e.getKey();
                    final HashMap<String, String> taskData = e.getValue();
                    if (taskData.get("task_name") == null) {
                        setTaskCount(temp_data.size() - 1);
                        LinearLayout.LayoutParams dll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (Integer.parseInt(temp_data.keySet().toArray()[0].toString()) == id) {
                            dll.setMargins(dpAsPixels(60), 0, dpAsPixels(60), 0);
                        } else {
                            dll.setMargins(dpAsPixels(60), dpAsPixels(6), dpAsPixels(60), 0);
                        }
                        dll.gravity = Gravity.CENTER;
                        TextView date_tv = new TextView(this);
                        date_tv.setPadding(0, dpAsPixels(9), 0, dpAsPixels(9));
                        date_tv.setTextColor(Color.WHITE);
                        date_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        date_tv.setBackground(getDrawable(R.drawable.today_date_border));

                        date_tv.setText("TODAY");
                        date_tv.setTag(R.id.today_tv);


                        date_tv.setLayoutParams(dll);
                        CalligraphyUtils.applyFontToTextView(date_tv, TypefaceUtils.load(getAssets(), "fonts/Montserrat-Bold.otf"));
                        ((LinearLayout) findViewById(R.id.main_task_container)).addView(date_tv);
                        dll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        dll.gravity = Gravity.CENTER;
                        date_tv = new TextView(this);
                        date_tv.setPadding(0, dpAsPixels(40), 0, dpAsPixels(40));
                        date_tv.setTextColor(Color.BLACK);
                        date_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                        date_tv.setText("NO TASKS FOR TODAY");


                        date_tv.setLayoutParams(dll);
                        CalligraphyUtils.applyFontToTextView(date_tv, TypefaceUtils.load(getAssets(), "fonts/Montserrat-Bold.otf"));
                        ((LinearLayout) findViewById(R.id.main_task_container)).addView(date_tv);

                    } else {
                        LinearLayout.LayoutParams cll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        cll.setMargins(0, dpAsPixels(6), 0, 0);
                        LinearLayout cl = new LinearLayout(this);
                        cl.setOrientation(LinearLayout.VERTICAL);
                        cl.setPadding(dpAsPixels(12), dpAsPixels(12), dpAsPixels(12), dpAsPixels(12));
                        cl.setLayoutParams(cll);
                        Date date1 = null, date2 = null;
                        try {
                            Calendar c = new GregorianCalendar();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            date1 = formatter.parse(taskData.get("date").replace("-", "/") + " " + taskData.get("time") + ":00");
                            date2 = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":00");
                        } catch (Exception de) {
                            String s = "";
                        }
                        if (Integer.parseInt(taskData.get("disable")) == 0) {

                            if (date1.getTime() >= date2.getTime()) {
                                cl.setBackground(getResources().getDrawable(R.drawable.card_layed));
                            } else {
                                cl.setBackground(getResources().getDrawable(R.drawable.card_layed_done));
                            }

                        } else {
                            cl.setBackground(getResources().getDrawable(R.drawable.card_layed_disabled));
                        }
                        final Context c = this;
                        cl.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder ab = new AlertDialog.Builder(c);
                                ab.setCancelable(true);
                                ab.setTitle("Delete Task");
                                ab.setMessage("Are you sure you want to delete the task?");
                                ab.setPositiveButton("Delete Task", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new DBInterface(getApplicationContext()).deleteTask(id);
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.task_body), "Task deleted", Snackbar.LENGTH_LONG);
                                        snackbar.getView().setBackgroundColor(Color.parseColor("#78281F"));
                                        snackbar.show();
                                        renderTasks(getPrefacedTaskType());
                                    }
                                });
                                ab.setNegativeButton("Cancel", null);
                                ab.show();
                                return true;
                            }
                        });
                        cl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), UpdateTaskActivity.class);
                                intent.putExtra("task_id", id);
                                startActivityForResult(intent, 7912);
                            }
                        });

                        LinearLayout.LayoutParams icll1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        LinearLayout icl1 = new LinearLayout(this);
                        icl1.setOrientation(LinearLayout.HORIZONTAL);
                        icl1.setWeightSum(4);
                        icl1.setLayoutParams(icll1);

                        LinearLayout.LayoutParams tv1_icll1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2);
                        TextView tv1_icl1 = new TextView(this);
                        tv1_icl1.setTextColor(Color.parseColor("#FFFFFF"));
                        CalligraphyUtils.applyFontToTextView(tv1_icl1, TypefaceUtils.load(getAssets(), "fonts/Montserrat-Medium.ttf"));
                        tv1_icl1.setLayoutParams(tv1_icll1);
                        String r_time = taskData.get("time");
                        String r_date = taskData.get("date");

                        String date_string = "";
                        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                        String c_date = "";

                        if (Integer.valueOf(r_date.split("-")[2]).toString().charAt(Integer.valueOf(r_date.split("-")[2]).toString().length() - 1) == '1' && Integer.valueOf(r_date.split("-")[2]).toString().charAt(0) != '1') {
                            c_date = Integer.valueOf(r_date.split("-")[2]).toString() + "st";
                        } else if (Integer.valueOf(r_date.split("-")[2]).toString().charAt(Integer.valueOf(r_date.split("-")[2]).toString().length() - 1) == '2' && Integer.valueOf(r_date.split("-")[2]).toString().charAt(0) != '1') {
                            c_date = Integer.valueOf(r_date.split("-")[2]).toString() + "nd";
                        } else if (Integer.valueOf(r_date.split("-")[2]).toString().charAt(Integer.valueOf(r_date.split("-")[2]).toString().length() - 1) == '3' && Integer.valueOf(r_date.split("-")[2]).toString().charAt(0) != '1') {
                            c_date = Integer.valueOf(r_date.split("-")[2]).toString() + "rd";
                        } else {
                            c_date = Integer.valueOf(r_date.split("-")[2]).toString() + "th";
                        }

                        date_string = c_date + " " + months[Integer.valueOf(r_date.split("-")[1]) - 1] + " " + r_date.split("-")[0];

                        String[] m_time = r_time.split(":");
                        int r_hour = Integer.parseInt(m_time[0]);
                        int r_min = Integer.parseInt(m_time[1]);
                        String meri = "AM";
                        if (r_hour > 12) {
                            r_hour = r_hour - 12;
                            meri = "PM";
                        }
                        String mh = "";
                        String mm = "";
                        if (String.valueOf(r_hour).length() == 1) {
                            mh = "0" + r_hour;
                        } else {
                            mh = String.valueOf(r_hour);
                        }
                        if (String.valueOf(r_min).length() == 1) {
                            mm = "0" + r_min;
                        } else {
                            mm = String.valueOf(r_min);
                        }

                        tv1_icl1.setText(mh + ":" + mm + " " + meri);

                        LinearLayout.LayoutParams tv2_icll1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2);
                        tv2_icll1.gravity = Gravity.RIGHT;
                        TextView tv2_icl1 = new TextView(this);
                        tv2_icl1.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                        tv2_icl1.setTextColor(Color.parseColor("#FFFFFF"));
                        CalligraphyUtils.applyFontToTextView(tv2_icl1, TypefaceUtils.load(getAssets(), "fonts/Montserrat-Medium.ttf"));
                        tv2_icl1.setLayoutParams(tv2_icll1);
                        tv2_icl1.setText(taskData.get("type"));

                        LinearLayout.LayoutParams tv1_cll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2);
                        tv1_cll.setMargins(0, dpAsPixels(6), 0, 0);
                        TextView tv1_cl = new TextView(this);
                        tv1_cl.setTextSize(20);
                        tv1_cl.setTextColor(Color.parseColor("#FFFFFF"));
                        CalligraphyUtils.applyFontToTextView(tv1_cl, TypefaceUtils.load(getAssets(), "fonts/Montserrat-Medium.ttf"));
                        tv1_cl.setText(taskData.get("task_name"));
                        tv1_cl.setLayoutParams(tv1_cll);

                        LinearLayout.LayoutParams icll2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        icll2.setMargins(0, dpAsPixels(6), 0, 0);
                        LinearLayout icl2 = new LinearLayout(this);
                        icl2.setOrientation(LinearLayout.HORIZONTAL);
                        icl2.setWeightSum(4);
                        icl2.setLayoutParams(icll2);

                        LinearLayout.LayoutParams tv1_icll2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                        TextView tv1_icl2 = new TextView(this);
                        tv1_icl2.setTextColor(Color.parseColor("#FFFFFF"));
                        CalligraphyUtils.applyFontToTextView(tv1_icl2, TypefaceUtils.load(getAssets(), "fonts/Montserrat-Medium.ttf"));
                        tv1_icl2.setLayoutParams(tv1_icll2);
                        tv1_icl2.setText(taskData.get("notif_type"));

                        LinearLayout.LayoutParams tv2_icll2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 3);
                        SwitchCompat tv2_icl2 = new SwitchCompat(this);
                        DrawableCompat.setTintList(DrawableCompat.wrap(tv2_icl2.getThumbDrawable()), new ColorStateList(states, thumbColors));
                        DrawableCompat.setTintList(DrawableCompat.wrap(tv2_icl2.getTrackDrawable()), new ColorStateList(states, trackColors));
                        tv2_icl2.setLayoutParams(tv2_icll2);
                        if (Integer.parseInt(taskData.get("disable")) == 0) {
                            tv2_icl2.setChecked(true);
                        } else {
                            tv2_icl2.setChecked(false);
                        }
                        tv2_icl2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    new DBInterface(getApplicationContext()).enableTask(id);
                                    Snackbar snackbar = Snackbar.make(findViewById(R.id.task_body), "Task action enabled", Snackbar.LENGTH_LONG);
                                    snackbar.getView().setBackgroundColor(Color.parseColor("#1A5276"));
                                    snackbar.show();
                                    renderTasks(getPrefacedTaskType());
                                } else {
                                    new DBInterface(getApplicationContext()).disableTask(id);
                                    Snackbar snackbar = Snackbar.make(findViewById(R.id.task_body), "Task action disabled", Snackbar.LENGTH_LONG);
                                    snackbar.getView().setBackgroundColor(Color.parseColor("#78281F"));
                                    snackbar.show();
                                    renderTasks(getPrefacedTaskType());
                                }
                            }
                        });

                        icl1.addView(tv1_icl1);
                        icl1.addView(tv2_icl1);
                        icl2.addView(tv1_icl2);
                        if (date1.getTime() >= date2.getTime()) {
                            icl2.addView(tv2_icl2);
                        }
                        cl.addView(icl1);
                        cl.addView(tv1_cl);
                        cl.addView(icl2);

                        if (present_date.equals(taskData.get("date")) == false) {
                            present_date = taskData.get("date");
                            LinearLayout.LayoutParams dll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            if (Integer.parseInt(temp_data.keySet().toArray()[0].toString()) == id) {
                                dll.setMargins(dpAsPixels(60), 0, dpAsPixels(60), 0);
                            } else {
                                dll.setMargins(dpAsPixels(60), dpAsPixels(6), dpAsPixels(60), 0);
                            }

                            dll.gravity = Gravity.CENTER;
                            TextView date_tv = new TextView(this);
                            date_tv.setPadding(0, dpAsPixels(9), 0, dpAsPixels(9));
                            date_tv.setTextColor(Color.WHITE);
                            date_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            date_tv.setBackground(getDrawable(R.drawable.date_border));



                            date_tv.setLayoutParams(dll);
                            CalligraphyUtils.applyFontToTextView(date_tv, TypefaceUtils.load(getAssets(), "fonts/Montserrat-Bold.otf"));
                            ((LinearLayout) findViewById(R.id.main_task_container)).addView(date_tv);
                            if (taskData.get("date").equals(new GregorianCalendar().get(Calendar.YEAR) + "-" + (Integer.valueOf(new GregorianCalendar().get(Calendar.MONTH)) + 1) + "-" + new GregorianCalendar().get(Calendar.DAY_OF_MONTH)) == false) {
                                date_tv.setText(date_string);
                            } else {
                                date_tv.setBackground(getDrawable(R.drawable.today_date_border));
                                date_tv.setText("TODAY");
                                date_tv.setTag(R.id.today_tv);
                            }

                        }
                        TransitionManager.setTransitionName(cl, taskData.get("task_name"));
                        ((LinearLayout) findViewById(R.id.main_task_container)).addView(cl);
                    }

                }

                LinearLayout.LayoutParams s_ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpAsPixels(100));
                Space s_l = new Space(this);
                s_l.setLayoutParams(s_ll);
                ((LinearLayout) findViewById(R.id.main_task_container)).addView(s_l);

            } else {
                setTaskCount(0);
                ((RelativeLayout) findViewById(R.id.for_no_tasks)).setVisibility(View.VISIBLE);
            }
        }
        else {
            ((RelativeLayout) findViewById(R.id.for_no_tasks)).setVisibility(View.INVISIBLE);
            View.inflate(this,R.layout.cal_layout,((LinearLayout) findViewById(R.id.main_task_container)));

            setTaskCount(0);

            Spinner spinner = (Spinner) findViewById(R.id.year_spinner);
            ArrayList<String> categories = new ArrayList<>();
            for(int i=new GregorianCalendar().get(Calendar.YEAR);i<=Integer.valueOf(new GregorianCalendar().get(Calendar.YEAR))+5;i++){
                categories.add(""+i);
            }
            Object[] cats = categories.toArray();
            String[] s_cats = new String[cats.length];
            for(int i=0;i<=s_cats.length-1;i++){s_cats[i] = cats[i].toString();}
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getApplicationContext(),
                    R.layout.blank_tv_right,s_cats){
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    View v = super.getView(position, convertView, parent);
                    ((TextView) v).setTypeface(TypefaceUtils.load(getAssets(),"fonts/Montserrat-Medium.ttf"));
                    return v;
                }
                public View getDropDownView(int position, View convertView, ViewGroup parent)
                {
                    View v = super.getDropDownView(position, convertView, parent);
                    ((TextView) v).setTypeface(TypefaceUtils.load(getAssets(),"fonts/Montserrat-Medium.ttf"));
                    return v;
                }
            };
            adapter.setDropDownViewResource(R.layout.blank_spinner_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    addDays( (  (Spinner) findViewById(R.id.month_spinner)  ).getSelectedItem().toString().replace(" ",""),  Integer.valueOf((  (Spinner) findViewById(R.id.year_spinner)  ).getSelectedItem().toString().replace(" ",""))  );
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            spinner.setSelection(0);


            spinner = (Spinner) findViewById(R.id.month_spinner);
            categories = new ArrayList<>();
            String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            for(int i=0;i<=months.length-1;i++){
                categories.add(months[i]);
            }
            cats = categories.toArray();
            s_cats = new String[cats.length];
            for(int i=0;i<=s_cats.length-1;i++){s_cats[i] = cats[i].toString();}
            adapter = new ArrayAdapter<CharSequence>(getApplicationContext(),
                    R.layout.blank_tv_left,s_cats){
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    View v = super.getView(position, convertView, parent);
                    ((TextView) v).setTypeface(TypefaceUtils.load(getAssets(),"fonts/Montserrat-Medium.ttf"));
                    return v;
                }
                public View getDropDownView(int position, View convertView, ViewGroup parent)
                {
                    View v = super.getDropDownView(position, convertView, parent);
                    ((TextView) v).setTypeface(TypefaceUtils.load(getAssets(),"fonts/Montserrat-Medium.ttf"));
                    return v;
                }
            };
            adapter.setDropDownViewResource(R.layout.blank_spinner_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {

                    addDays( (  (Spinner) findViewById(R.id.month_spinner)  ).getSelectedItem().toString().replace(" ",""),  Integer.valueOf((  (Spinner) findViewById(R.id.year_spinner)  ).getSelectedItem().toString().replace(" ",""))  );
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
            spinner.setSelection( new GregorianCalendar().get(Calendar.MONTH) );



            ( (ImageButton) findViewById(R.id.previous) ).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int selected_month = ((Spinner) findViewById(R.id.month_spinner)).getSelectedItemPosition();
                        int selected_year = Integer.parseInt(((Spinner) findViewById(R.id.year_spinner)).getSelectedItem().toString().replace(" ", ""));
                        Calendar c = new GregorianCalendar(selected_year, selected_month, 1);
                        c.add(Calendar.DATE, -1);
                        ArrayList<Integer> s_years = new ArrayList<Integer>();
                        for (int i = new GregorianCalendar().get(Calendar.YEAR); i <= Integer.valueOf(new GregorianCalendar().get(Calendar.YEAR)) + 5; i++) {
                            s_years.add(i);
                        }
                        for (Integer s_year : s_years) {
                            if (s_year == c.get(Calendar.YEAR)) {
                                ((Spinner) findViewById(R.id.month_spinner)).setSelection(c.get(Calendar.MONTH));
                                ((Spinner) findViewById(R.id.year_spinner)).setSelection(s_years.indexOf(s_year));
                                break;
                            }
                        }
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });

            ( (ImageButton) findViewById(R.id.next) ).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selected_month = ((Spinner) findViewById(R.id.month_spinner)).getSelectedItemPosition();
                    int selected_year = Integer.parseInt( ( (Spinner) findViewById(R.id.year_spinner) ).getSelectedItem().toString().replace(" ",""));
                    Calendar c = new GregorianCalendar(selected_year,selected_month,1);
                    c.set(Calendar.DAY_OF_MONTH,c.getActualMaximum(Calendar.DAY_OF_MONTH));
                    c.add(Calendar.DATE,1);
                    ArrayList<Integer> s_years = new ArrayList<Integer>();
                    for(int i=new GregorianCalendar().get(Calendar.YEAR);i<=Integer.valueOf(new GregorianCalendar().get(Calendar.YEAR))+5;i++){
                        s_years.add(i);
                    }
                    for(Integer s_year:s_years){
                        if(s_year == c.get(Calendar.YEAR)){
                            ( (Spinner) findViewById(R.id.month_spinner) ).setSelection(c.get(Calendar.MONTH));
                            ( (Spinner) findViewById(R.id.year_spinner) ).setSelection(s_years.indexOf(s_year));
                            break;
                        }
                    }
                }
            });

        }
    }

    public void addDays(String month,int year){

        ((LinearLayout) findViewById(R.id.days_container)).removeAllViews();
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int m_index = 0;
        for (int i = 0; i <= months.length - 1; i++) {
            if (months[i].equals(month)) {
                m_index = i;
                break;
            }
        }
        Calendar calendar = new GregorianCalendar(year, m_index, 1);
        int no_days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        LinkedHashMap<Integer, Integer> days_with_date = new LinkedHashMap<>();

        for (int i = 1; i <= no_days; i++) {
            days_with_date.put(i, new GregorianCalendar(year, m_index, i).get(Calendar.DAY_OF_WEEK));
        }


        ArrayList<Integer> row = new ArrayList<>();
        for(int i=0;i<=6;i++){row.add(0);}

        for(Map.Entry<Integer,Integer> e :days_with_date.entrySet()){
            if(row.get(6) != 0){
                addRow(row,m_index,year);
                for(int i =  0;i<=row.size()-1;i++){row.set(i,0);}
                row.set(e.getValue().intValue()-1,e.getKey().intValue());
            }
            else if( e.getKey().intValue() ==   Integer.parseInt(  days_with_date.keySet().toArray()[days_with_date.size()-1].toString()  )  ){
                row.set(e.getValue().intValue()-1,e.getKey().intValue());
                addRow(row,m_index,year);
            }
            else {
                row.set(e.getValue().intValue()-1,e.getKey().intValue());
            }
        }


    }

    public void addRow(final ArrayList<Integer> days, final int month, final int year){
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setWeightSum(7);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        for(int i=0;i<=days.size() - 1;i++){
            TextView tv = new TextView(this);
            if(days.get(i) != 0){tv.setText(days.get(i).toString());}else{tv.setText(" ");}
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setPadding(0,dpAsPixels(20),0,dpAsPixels(20));
            if(   (year) == new GregorianCalendar().get(Calendar.YEAR) && (month) == new GregorianCalendar().get(Calendar.MONTH) &&  Integer.valueOf(new GregorianCalendar().get(Calendar.DAY_OF_MONTH)) == days.get(i)  ){
                tv.setBackground(getDrawable(R.drawable.today_cal));


                if(new DBInterface(getApplicationContext()).getTasks(year+"-"+(month+1)+"-"+days.get(i)).size() > 0){
                    tv.setTextColor(Color.parseColor("#85C1E9"));
                    final LinkedHashMap<Integer,HashMap<String,String>> p_tasks = new DBInterface(getApplicationContext()).getTasks(year+"-"+(month+1)+"-"+days.get(i));
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ( (LinearLayout) findViewById(R.id.day_task_sv) ).removeAllViews();
                            for(Map.Entry<Integer,HashMap<String,String>> e: p_tasks.entrySet()){
                                TextView tv = new TextView(LauncherActivity.this);
                                String r_time = e.getValue().get("time");
                                String[] m_time = r_time.split(":");
                                int r_hour = Integer.parseInt(m_time[0]);
                                int r_min = Integer.parseInt(m_time[1]);
                                String meri = "AM";
                                if (r_hour > 12) {
                                    r_hour = r_hour - 12;
                                    meri = "PM";
                                }
                                String mh = "";
                                String mm = "";
                                if (String.valueOf(r_hour).length() == 1) {
                                    mh = "0" + r_hour;
                                } else {
                                    mh = String.valueOf(r_hour);
                                }
                                if (String.valueOf(r_min).length() == 1) {
                                    mm = "0" + r_min;
                                } else {
                                    mm = String.valueOf(r_min);
                                }

                                tv.setText(e.getValue().get("task_name")+"      "+mh + ":" + mm + " " + meri);
                                tv.setBackground(getDrawable(R.drawable.p_t_bg));
                                tv.setPadding(dpAsPixels(20),dpAsPixels(9),dpAsPixels(20),dpAsPixels(9));
                                tv.setTextColor(Color.parseColor("#FFFFFF"));
                                tv.setTextSize(16);
                                LinearLayout.LayoutParams tv_ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                tv_ll.setMargins(dpAsPixels(2),0,dpAsPixels(2),0);
                                tv.setLayoutParams(tv_ll);

                                ( (LinearLayout) findViewById(R.id.day_task_sv) ).addView(tv);
                            }
                            LinearLayout.LayoutParams space_ll = new LinearLayout.LayoutParams(dpAsPixels(100),LinearLayout.LayoutParams.WRAP_CONTENT);
                            Space s = new Space(LauncherActivity.this);
                            s.setLayoutParams(space_ll);
                            ( (LinearLayout) findViewById(R.id.day_task_sv) ).addView(s);
                        }
                    });
                }
                else{tv.setTextColor(Color.parseColor("#FFFFFF"));tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ( (LinearLayout) findViewById(R.id.day_task_sv) ).removeAllViews();
                    }
                });}


                tv.setTextSize(16);
                CalligraphyUtils.applyFontToTextView(tv,TypefaceUtils.load(getAssets(),"fonts/Montserrat-Bold.otf"));
            }
            else {
                if(new DBInterface(getApplicationContext()).getTasks(year+"-"+(month+1)+"-"+days.get(i)).size() > 0){
                    tv.setTextColor(Color.parseColor("#229954"));CalligraphyUtils.applyFontToTextView(tv,TypefaceUtils.load(getAssets(),"fonts/Montserrat-Bold.otf"));
                    final LinkedHashMap<Integer,HashMap<String,String>> p_tasks = new DBInterface(getApplicationContext()).getTasks(year+"-"+(month+1)+"-"+days.get(i));
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ( (LinearLayout) findViewById(R.id.day_task_sv) ).removeAllViews();
                            for(Map.Entry<Integer,HashMap<String,String>> e: p_tasks.entrySet()){
                                TextView tv = new TextView(LauncherActivity.this);
                                CalligraphyUtils.applyFontToTextView(tv,TypefaceUtils.load(getAssets(),"fonts/Montserrat-Medium.ttf"));
                                String r_time = e.getValue().get("time");
                                String[] m_time = r_time.split(":");
                                int r_hour = Integer.parseInt(m_time[0]);
                                int r_min = Integer.parseInt(m_time[1]);
                                String meri = "AM";
                                if (r_hour > 12) {
                                    r_hour = r_hour - 12;
                                    meri = "PM";
                                }
                                String mh = "";
                                String mm = "";
                                if (String.valueOf(r_hour).length() == 1) {
                                    mh = "0" + r_hour;
                                } else {
                                    mh = String.valueOf(r_hour);
                                }
                                if (String.valueOf(r_min).length() == 1) {
                                    mm = "0" + r_min;
                                } else {
                                    mm = String.valueOf(r_min);
                                }

                                tv.setText(e.getValue().get("task_name")+"      "+mh + ":" + mm + " " + meri);
                                tv.setBackground(getDrawable(R.drawable.p_t_bg));
                                tv.setPadding(dpAsPixels(20),dpAsPixels(9),dpAsPixels(20),dpAsPixels(9));
                                tv.setTextColor(Color.parseColor("#FFFFFF"));
                                tv.setTextSize(16);
                                LinearLayout.LayoutParams tv_ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                tv_ll.setMargins(dpAsPixels(2),0,dpAsPixels(2),0);
                                tv.setLayoutParams(tv_ll);

                                ( (LinearLayout) findViewById(R.id.day_task_sv) ).addView(tv);
                            }
                            LinearLayout.LayoutParams space_ll = new LinearLayout.LayoutParams(dpAsPixels(100),LinearLayout.LayoutParams.WRAP_CONTENT);
                            Space s = new Space(LauncherActivity.this);
                            s.setLayoutParams(space_ll);
                            ( (LinearLayout) findViewById(R.id.day_task_sv) ).addView(s);
                        }});
                }
                else{tv.setTextColor(Color.parseColor("#000000"));CalligraphyUtils.applyFontToTextView(tv,TypefaceUtils.load(getAssets(),"fonts/Montserrat-Medium.ttf"));tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ( (LinearLayout) findViewById(R.id.day_task_sv) ).removeAllViews();
                    }
                });}

            }
            tv.setLayoutParams(new LinearLayout.LayoutParams(dpAsPixels(20),LinearLayout.LayoutParams.WRAP_CONTENT,1));
            ll.addView(tv);
        }
        ((LinearLayout) findViewById(R.id.days_container)).addView(ll);
    }

    public void setTaskCount(int count){
        if(count == 0){( (TextView) findViewById(R.id.task_count) ).setText("");}
        else if(count == 1){( (TextView) findViewById(R.id.task_count) ).setText(count+" Task");}
        else{( (TextView) findViewById(R.id.task_count) ).setText(count+" Tasks");}
    }

    public void darkMode(){

    }

    public void lightMode(){

    }

    protected int dpAsPixels(int no){
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (no*scale + 0.5f);
        return dpAsPixels;
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
