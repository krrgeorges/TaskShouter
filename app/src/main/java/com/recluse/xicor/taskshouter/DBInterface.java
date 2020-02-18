package com.recluse.xicor.taskshouter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by ROJIT on 4/24/2018.
 */

public class DBInterface extends SQLiteOpenHelper {

    public final String DB_NAME = "TaskDB";
    public final int DB_VERSION = 1;
    public final String TASK_TABLE = "TASKS";

    Context context;
    public DBInterface(Context c){
        super(c,"TaskDB",null,1);context = c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTasksTable = "CREATE TABLE TASKS(t_id integer PRIMARY KEY AUTOINCREMENT ," +
                "task_name TEXT," +
                "date TEXT," +
                "time TEXT," +
                "type TEXT," +
                "disable INTEGER," +
                "interval_type TEXT DEFAULT NULL," +
                "interval_duration INTEGER DEFAULT NULL," +
                "notif_type TEXT);";
        db.execSQL(createTasksTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE TASKS");
        onCreate(db);
    }

    public long insertTask(String taskName,String date,String time,String type,String intervalType,int intervalDuration,String notifType){
        ContentValues cv = new ContentValues();
        cv.put("task_name",taskName);
        cv.put("date",date);
        cv.put("time",time);
        cv.put("type",type);
        cv.put("disable",0);
        cv.put("interval_type",intervalType);
        cv.put("interval_duration",intervalDuration);
        cv.put("notif_type",notifType);
        long id = this.getWritableDatabase().insert(TASK_TABLE,null,cv);
        return id;
    }

    public void updateTask(int id,String taskName,String date,String time,String type,int aloud,String intervalType,int intervalDuration,String notifType){
        this.getWritableDatabase().execSQL("UPDATE TASKS SET task_name='"+taskName+"',date='"+date+"',time='"+time+"',type='"+type+"',interval_type='"+intervalType+"',interval_duration="+intervalDuration+",notif_type='"+notifType+"' WHERE t_id="+id+";");
    }

    public void updateTaskDatetime(int id,String date,String time){
        this.getWritableDatabase().execSQL("UPDATE TASKS SET date='"+date+"',time='"+time+"' WHERE t_id="+id+";");
    }

    public void disableTask(int id){
        this.getWritableDatabase().execSQL("UPDATE TASKS SET disable=1 WHERE t_id="+id+";");
    }

    public void enableTask(int id){
        this.getWritableDatabase().execSQL("UPDATE TASKS SET disable=0 WHERE t_id="+id+";");
    }

    public void deleteTask(int id){
        this.getWritableDatabase().execSQL("DELETE FROM TASKS WHERE t_id="+id+";");
    }



    public LinkedHashMap<Integer,HashMap<String,String>> getAllTasks(){
        LinkedHashMap<Integer,HashMap<String,String>> all_data = new LinkedHashMap<>();
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM TASKS ORDER BY date,time;",new String[]{});
        ArrayList<Integer> keys = new ArrayList<>();
        while (c.moveToNext()){
            HashMap<String,String> data = new HashMap<>();
            data.put("task_name",c.getString(1));
            data.put("date",c.getString(2));
            data.put("time",c.getString(3));
            data.put("type",c.getString(4));
            data.put("disable",Integer.toString(c.getInt(5)));
            data.put("interval_type",c.getString(6));
            data.put("interval_duration",Integer.toString(c.getInt(7)));
            data.put("notif_type",c.getString(8));
            all_data.put(c.getInt(0),data);
            keys.add(c.getInt(0));
        }
        return all_data;
    }

    public HashMap<String,String> getTask(int id){
        HashMap<String,String> data = new HashMap<>();
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM TASKS WHERE t_id="+id+";",new String[]{});
        c.moveToNext();
        data.put("task_name",c.getString(1));
        data.put("date",c.getString(2));
        data.put("time",c.getString(3));
        data.put("type",c.getString(4));
        data.put("disable",Integer.toString(c.getInt(5)));
        data.put("interval_type",c.getString(6));
        data.put("interval_duration",Integer.toString(c.getInt(7)));
        data.put("notif_type",c.getString(8));
        return data;
    }

    public LinkedHashMap<Integer,HashMap<String,String>> getTasks(String date){
        LinkedHashMap<Integer,HashMap<String,String>> all_data = new LinkedHashMap<>();
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM TASKS WHERE date = '"+date+"' ORDER BY time;",new String[]{});
        ArrayList<Integer> keys = new ArrayList<>();
        while (c.moveToNext()){
            HashMap<String,String> data = new HashMap<>();
            data.put("task_name",c.getString(1));
            data.put("date",c.getString(2));
            data.put("time",c.getString(3));
            data.put("type",c.getString(4));
            data.put("disable",Integer.toString(c.getInt(5)));
            data.put("interval_type",c.getString(6));
            data.put("interval_duration",Integer.toString(c.getInt(7)));
            data.put("notif_type",c.getString(8));
            all_data.put(c.getInt(0),data);
            keys.add(c.getInt(0));
        }
        return all_data;
    }
}
