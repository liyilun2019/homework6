package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {
    private static TodoContract instance;

    // TODO 定义表结构和 SQL 语句常量
    public static class TodoTable implements BaseColumns {
        public static final String TABLE_NAME = "note";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_DATE = "DATE";
        public static final String COLUMN_NAME_STATE="STATE";
        public static final String COLUMN_NAME_CONTENT="CONTENT";
        public static final String COLUMN_NAME_PRI = "PRIORITY";
    }
    public static final String SQL_CREATE_TABLE = "CREATE TABLE "+TodoTable.TABLE_NAME
            +"("+TodoTable.COLUMN_NAME_ID+" INTEGER PRIMARY KEY,"
            +TodoTable.COLUMN_NAME_DATE+" TEXT,"
            +TodoTable.COLUMN_NAME_STATE+" INTEGER,"
            +TodoTable.COLUMN_NAME_CONTENT+" TEXT," +
            TodoTable.COLUMN_NAME_PRI+" INTEGER)";

    public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS "+TodoTable.TABLE_NAME;

    public TodoContract getInstance(){
        if(instance==null){
            instance = new TodoContract();
        }
        return instance;
    }

    private TodoContract() {
    }

}
