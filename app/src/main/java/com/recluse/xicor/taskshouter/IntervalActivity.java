package com.recluse.xicor.taskshouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class IntervalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Spinner spinner = (Spinner) findViewById(R.id.i_type);
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Minute(s)");
        categories.add("Hour(s)");
        categories.add("Day(s)");
        categories.add("Month(s)");
        categories.add("Year(s)");
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


        ( (Button) findViewById(R.id.cancel_duration) ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });

        ( (Button) findViewById(R.id.set_duration) ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dur = Integer.parseInt(( (EditText) findViewById(R.id.i_duration )).getText().toString());
                String type = ( (Spinner) findViewById(R.id.i_type) ).getSelectedItem().toString();
                if(dur > 0){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("interval",dur+" "+type);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        });
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
