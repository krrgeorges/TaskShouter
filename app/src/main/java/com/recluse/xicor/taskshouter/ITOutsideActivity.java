package com.recluse.xicor.taskshouter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class ITOutsideActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itoutside);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        tts = new TextToSpeech(this, this);

        Spinner spinner = (Spinner) findViewById(R.id.o_tasknotif);
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Notification with Speech");
        categories.add("Speech Only");
        categories.add("Notification only");
        Object[] cats = categories.toArray();
        String[] s_cats = new String[cats.length];
        for(int i=0;i<=s_cats.length-1;i++){s_cats[i] = cats[i].toString();}
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getApplicationContext(),
                R.layout.basic_spinner_textview,s_cats){
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
        adapter.setDropDownViewResource(R.layout.basic_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spinner = (Spinner) findViewById(R.id.o_tasktype);
        categories = new ArrayList<>();
        categories.add("Only Once");
        categories.add("Every Day");
        categories.add("At Intervals");
        cats = categories.toArray();
        s_cats = new String[cats.length];
        for(int i=0;i<=s_cats.length-1;i++){s_cats[i] = cats[i].toString();}
        adapter = new ArrayAdapter<CharSequence>(getApplicationContext(),
                R.layout.basic_spinner_textview,s_cats){
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
        adapter.setDropDownViewResource(R.layout.basic_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                String notifType = ((Spinner)findViewById(R.id.o_tasktype)).getSelectedItem().toString();
                if(notifType.equals("At Intervals")){
                    startActivityForResult(new Intent(getApplicationContext(),IntervalActivity.class),7913);
                }
                else{
                    ( (TextView) findViewById(R.id.o_interval_set) ).setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                String notifType = ((Spinner)findViewById(R.id.o_tasktype)).getSelectedItem().toString();
                if(notifType.equals("At Intervals")){
                    startActivityForResult(new Intent(getApplicationContext(),IntervalActivity.class),7913);
                }
                else{
                    ( (TextView) findViewById(R.id.o_interval_set) ).setText("");
                }
            }
        });



        final Calendar c = new GregorianCalendar();
        final Context context = this;
        c.setTime(new Date());
        ( (Button) findViewById(R.id.o_set_taskdate) ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog d = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        try {
                            String c_date = "";
                            if (Integer.valueOf(dayOfMonth).toString().charAt(Integer.valueOf(dayOfMonth).toString().length() - 1) == '1' && Integer.valueOf(dayOfMonth).toString().charAt(0) != '1') {
                                c_date = Integer.valueOf(dayOfMonth).toString() + "st";
                            } else if (Integer.valueOf(dayOfMonth).toString().charAt(Integer.valueOf(dayOfMonth).toString().length() - 1) == '2' && Integer.valueOf(dayOfMonth).toString().charAt(0) != '1') {
                                c_date = Integer.valueOf(dayOfMonth).toString() + "nd";
                            } else if (Integer.valueOf(dayOfMonth).toString().charAt(Integer.valueOf(dayOfMonth).toString().length() - 1) == '3' && Integer.valueOf(dayOfMonth).toString().charAt(0) != '1') {
                                c_date = Integer.valueOf(dayOfMonth).toString() + "rd";
                            } else {
                                c_date = Integer.valueOf(dayOfMonth).toString() + "th";
                            }
                            String c_month = "";
                            switch(month+1){case 1:c_month="Jan";break;case 2:c_month="Feb";break;case 3:c_month="Mar";break;case 4:c_month="Apr";break;case 5:c_month="May";break;case 6:c_month="June";break;case 7:c_month="July";break;case 8:c_month="Aug";break;case 9:c_month="Sep";break;case 10:c_month="Oct";break;case 11:c_month="Nov";break;case 12:c_month="Dec";break;}
                            String c_date_string = c_date + " " + c_month + " " + year;
                            ((EditText) findViewById(R.id.o_taskdate)).setText(c_date_string);
                        }
                        catch (Exception e){
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                d.show();

            }
        });

        ( (Button) findViewById(R.id.o_set_tasktime) ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String meridiem = "AM";
                        if(hourOfDay > 12){
                            meridiem = "PM";
                            hourOfDay = hourOfDay - 12;
                        }
                        String hd = "";String md = "";

                        if(String.valueOf(hourOfDay).length() == 1){hd = "0"+String.valueOf(hourOfDay);}else{hd = String.valueOf(hourOfDay);}
                        if(String.valueOf(minute).length() == 1){md = "0"+String.valueOf(minute);}else{md = String.valueOf(minute);}

                        ( (EditText) findViewById(R.id.o_tasktime) ).setText(hd+":"+md+" "+meridiem);
                    }
                },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false).show();
            }
        });

        ( (FloatingActionButton) findViewById(R.id.o_speak_test) ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( ((EditText) findViewById(R.id.o_taskname)).getText().toString().replace(" ","").length() > 0 ){
                    speakOut(((EditText) findViewById(R.id.o_taskname)).getText().toString());
                }
                else {
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("Please enter Task Name")
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.parseColor("#A93226"))
                            .show();
                }
            }
        });

        String date = "";

        if (Integer.valueOf(c.get(Calendar.DAY_OF_MONTH)).toString().charAt(Integer.valueOf(c.get(Calendar.DAY_OF_MONTH)).toString().length()-1) == '1') {
            date = Integer.valueOf(c.get(Calendar.DAY_OF_MONTH)).toString() + "st";
        }
        else if (Integer.valueOf(c.get(Calendar.DAY_OF_MONTH)).toString().charAt(Integer.valueOf(c.get(Calendar.DAY_OF_MONTH)).toString().length()-1) == '2') {
            date = Integer.valueOf(c.get(Calendar.DAY_OF_MONTH)).toString() + "nd";
        }
        else if (Integer.valueOf(c.get(Calendar.DAY_OF_MONTH)).toString().charAt(Integer.valueOf(c.get(Calendar.DAY_OF_MONTH)).toString().length()-1) == '3') {
            date = Integer.valueOf(c.get(Calendar.DAY_OF_MONTH)).toString() + "rd";
        }
        else {
            date = Integer.valueOf(c.get(Calendar.DAY_OF_MONTH)).toString() + "th";
        }
        String date_string = "";
        if(c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH).length() <=4){date_string = date + " " + c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + c.get(Calendar.YEAR);}
        else {date_string = date + " " + c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH).substring(0, 3) + " " + c.get(Calendar.YEAR);}

        ((EditText) findViewById(R.id.o_taskdate)).setText(date_string);

        ( (FloatingActionButton) findViewById(R.id.o_main_insert_task) ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String task = ((EditText) findViewById(R.id.o_taskname)).getText().toString();
                    String date = ((EditText) findViewById(R.id.o_taskdate)).getText().toString();
                    String time = ((EditText) findViewById(R.id.o_tasktime)).getText().toString();
                    String type = ((Spinner) findViewById(R.id.o_tasktype)).getSelectedItem().toString();
                    String notifType = ((Spinner) findViewById(R.id.o_tasknotif)).getSelectedItem().toString();
                    if (task.split(" ").length > 0 && task.replace(" ", "").length() > 0) {
                        if (date.equals("") == false) {
                            if (time.equals("") == false) {
                                String[] date_sep = date.split(" ");
                                int s_date = Integer.parseInt(date_sep[0].replace("st", "").replace("nd", "").replace("rd", "").replace("th", ""));
                                int s_month = 0;
                                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
                                for (int i = 0; i <= months.length - 1; i++) {
                                    if (months[i].equals(date_sep[1])) {
                                        s_month = i + 1;
                                    }
                                }
                                int s_year = Integer.parseInt(date_sep[2]);
                                String[] time_sep = time.split(" ");
                                String meri = time_sep[1];
                                int r_hour = Integer.parseInt(time_sep[0].split(":")[0]);
                                int r_min = Integer.parseInt(time_sep[0].split(":")[1]);
                                if (meri.equals("PM")) {
                                    r_hour = r_hour + 12;
                                }
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                                Date date1 = formatter.parse(s_year + "/" + s_month + "/" + s_date + " " + r_hour + ":" + r_min + ":00");
                                Date date2 = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":00");
                                if (date1.getTime() <= date2.getTime()) {
                                    new StyleableToast
                                            .Builder(getApplicationContext())
                                            .text("Past date and time will not be accepted")
                                            .textColor(Color.WHITE)
                                            .backgroundColor(Color.parseColor("#A93226"))
                                            .show();
                                } else {
                                    DBInterface db = new DBInterface(getApplicationContext());
                                    long id = 0;
                                    if (((TextView) findViewById(R.id.o_interval_set)).getText().length() > 0) {
                                        String[] i_text = ((TextView) findViewById(R.id.o_interval_set)).getText().toString().replace(" interval set", "").split(" ");
                                        id = db.insertTask(task, s_year + "-" + s_month + "-" + s_date, r_hour + ":" + r_min, type, i_text[1], Integer.parseInt(i_text[0]), notifType);
                                    } else {
                                        id = db.insertTask(task, s_year + "-" + s_month + "-" + s_date, r_hour + ":" + r_min, type, null, 0, notifType);
                                    }
                                    if (id == -1) {
                                        Toast.makeText(getApplicationContext(),"Task not added, Please try again",Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Task added",Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }
                            } else {
                                new StyleableToast
                                        .Builder(getApplicationContext())
                                        .text("Time is mandatory")
                                        .textColor(Color.WHITE)
                                        .backgroundColor(Color.parseColor("#A93226"))
                                        .show();
                            }
                        } else {
                            new StyleableToast
                                    .Builder(getApplicationContext())
                                    .text("Date is mandatory")
                                    .textColor(Color.WHITE)
                                    .backgroundColor(Color.parseColor("#A93226"))
                                    .show();
                        }
                    } else {
                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text("Task Name should be valid")
                                .textColor(Color.WHITE)
                                .backgroundColor(Color.parseColor("#A93226"))
                                .show();
                    }
                }
                catch (Exception e){Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();}
            }
        });






    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 7913){
            if(resultCode == Activity.RESULT_OK){
                if(data.getStringExtra("interval") != null){
                    ( (TextView) findViewById(R.id.o_interval_set) ).setText(data.getStringExtra("interval")+" interval set");
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("Interval set")
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.parseColor("#27AE60"))
                            .show();
                }
                else{
                    ( (Spinner) findViewById(R.id.o_tasktype) ).setSelection(0);
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("Interval not set")
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.parseColor("#A93226"))
                            .show();
                    ( (TextView) findViewById(R.id.o_interval_set) ).setText("");
                }
            }
            else{
                ( (Spinner) findViewById(R.id.o_tasktype) ).setSelection(0);
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Interval not set")
                        .textColor(Color.WHITE)
                        .backgroundColor(Color.parseColor("#A93226"))
                        .show();
                ( (TextView) findViewById(R.id.o_interval_set) ).setText("");
            }
        }
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

    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
