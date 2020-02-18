package com.recluse.xicor.taskshouter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by ROJIT on 4/24/2018.
 */

public class DBInterface extends SQLiteOpenHelper {

    public final String DB_NAME = "TaskDB";
    public final int DB_VERSION = 1;
    public final String TASK_TABLE = "TASKS";


    public DBInterface(Context c){
        super(c,"TaskDB",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTasksTable = "CREATE TABLE TASKS(t_id integer autoincrement PRIMARY KEY,task_name TEXT,date TEXT,time TEXT,type TEXT,aloud int,disable int);";
        db.execSQL(createTasksTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE TASKS");
        onCreate(db);
    }

    public long insertTask(String taskName,String date,String time,String type,int aloud){
        ContentValues cv = new ContentValues();
        cv.put("task_name",taskName);
        cv.put("date",date);
        cv.put("time",time);
        cv.put("type",type);
        cv.put("aloud",aloud);
        cv.put("disable",0);
        long id = this.getWritableDatabase().insert(TASK_TABLE,null,cv);
        return id;
    }

    public void updateTask(int id,String taskName,String date,String time,String type,int aloud){
        this.getWritableDatabase().execSQL("UPDATE TASKS SET task_name='"+taskName+"',date='"+date+"',time='"+time+"',type='"+type+"',aloud="+aloud+" WHERE t_id="+id+";");
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

    public HashMap<Integer,HashMap<String,String>> getAllTasks(){
        HashMap<Integer,HashMap<String,String>> all_data = new HashMap<>();
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM TASKS;",new String[]{});
        while (c.moveToNext()){
            HashMap<String,String> data = new HashMap<>();
            data.put("task_name",c.getString(1));
            data.put("date",c.getString(2));
            data.put("time",c.getString(3));
            data.put("type",c.getString(4));
            data.put("aloud",Integer.toString(c.getInt(5)));
            data.put("disable",Integer.toString(c.getInt(6)));
            all_data.put(c.getInt(0),data);
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
        data.put("aloud",Integer.toString(c.getInt(5)));
        data.put("disable",Integer.toString(c.getInt(6)));
        return data;
    }
}
