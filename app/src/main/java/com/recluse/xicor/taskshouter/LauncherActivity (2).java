package com.recluse.xicor.taskshouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Spinner spinner = (Spinner) findViewById(R.id.categories);
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Present Tasks");
        categories.add("All Tasks");
        categories.add("Disabled Tasks");
        categories.add("Calendar");
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

    }

    public void insertTask(){
        startActivityForResult(new Intent(this,InsertTaskActivity.class),7902);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 7902){
            if(resultCode == Activity.RESULT_OK){
                if(data.getStringExtra("result").equals("YES")){
                    Snackbar.make(findViewById(R.id.task_body),"Task added",Snackbar.LENGTH_LONG).show();
                }
                else{
                    Snackbar.make(findViewById(R.id.task_body),"Task could not be added",Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    public void showTasks(){

    }

    public void darkMode(){

    }

    public void lightMode(){

    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
