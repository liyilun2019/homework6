package com.byted.camp.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class TodoDbHelper extends SQLiteOpenHelper {

    // TODO 定义数据库名、版本；创建数据库
    public static final String DATABASE_NAME = "database.db";
    public static final int DATABASE_VERSION = 2;
    public static final String TAG = "SQL";

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: ");
        db.execSQL(TodoContract.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i = oldVersion;i<newVersion;i++){
            switch (i){
                case 1:
                    try {
                        db.execSQL("ALTER TABLE " + TodoContract.TodoTable.TABLE_NAME
                                + " ADD " + TodoContract.TodoTable.COLUMN_NAME_PRI
                                + " INTEGER ");
                    } catch (Exception e){
                        Log.d(TAG, "onUpgrade: "+e.getClass().getName() + "  "+e.getMessage());
                    }
            }
        }
    }

}
