package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.DebugActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD = 1002;
    public static final int REQUEST_CODE_MODIFY=1003;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private TodoDbHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper = new TodoDbHelper(this);
        db = helper.getWritableDatabase();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
                notesAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }else if(requestCode==REQUEST_CODE_MODIFY&&resultCode==Activity.RESULT_OK){
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }


    private List<Note> loadNotesFromDatabase() {
        // endTODO 从数据库中查询数据，并转换成 JavaBeans
//        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+ TodoContract.TodoTable.TABLE_NAME,
                null);
        ArrayList<Note> ret = new ArrayList<>();
        while(cursor.moveToNext()){
            Note tmp = new Note(
                    cursor.getLong(
                            cursor.getColumnIndexOrThrow(
                                    TodoContract.TodoTable.COLUMN_NAME_ID)));
            tmp.setContent(cursor.getString(cursor.getColumnIndexOrThrow(
                    TodoContract.TodoTable.COLUMN_NAME_CONTENT)));
            tmp.setDate(new Date(cursor.getString(cursor.getColumnIndexOrThrow(
                    TodoContract.TodoTable.COLUMN_NAME_DATE))));
            tmp.setState(State.from(cursor.getInt(cursor.getColumnIndexOrThrow(
                    TodoContract.TodoTable.COLUMN_NAME_STATE
                ))));

            try{
                tmp.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(
                        TodoContract.TodoTable.COLUMN_NAME_PRI)));
            }catch (Exception e){
                tmp.setPriority(0);
            }

            ret.add(tmp);
//            Log.d("SQL", "loadNotesFromDatabase: add "+tmp.id);
        }
        ret.sort(new Comparator<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                //Log.d("SQL", "compare: "+o1.getPriority()+" "+o2.getPriority());
                int diff = o2.getPriority()-o1.getPriority();
                if(diff==0){
                    diff = o2.getDate().compareTo(o1.getDate());
                }
                return diff;
            }
        });
        return ret;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        int deleteRows = db.delete(TodoContract.TodoTable.TABLE_NAME ,
                TodoContract.TodoTable.COLUMN_NAME_ID + " LIKE ?",
                new String[]{""+note.id});

    }

    private void updateNode(Note note) {
        // TODO 更新数据

        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoTable.COLUMN_NAME_CONTENT,note.getContent());
//        values.put(TodoContract.TodoTable.COLUMN_NAME_DATE, note.getDate().toString());
        values.put(TodoContract.TodoTable.COLUMN_NAME_STATE,note.getState().intValue);
        values.put(TodoContract.TodoTable.COLUMN_NAME_PRI,note.getPriority());

        int count = db.update(TodoContract.TodoTable.TABLE_NAME,
                values,
                TodoContract.TodoTable.COLUMN_NAME_ID+" LIKE ?",
                new String[]{""+note.id});
    }

}
