package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NoteEditActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private TodoDbHelper helper;
    private long gotid;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);
        helper = new TodoDbHelper(this);
        db = helper.getWritableDatabase();

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteEditActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean flg = saveNote2Database(content.toString().trim());
                if(flg){
                    setResult(Activity.RESULT_OK);
                }
                finish();
            }
        });

        gotid = getIntent().getLongExtra("ID",-1);
        if(gotid<0){
            Toast.makeText(this,"Invalid id="+gotid,Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(this,"id="+gotid,Toast.LENGTH_SHORT).show();
        }
        editText.setText(getIntent().getStringExtra("CONTENT"));

    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }

    private int getPriority(){
        if(((RadioButton)findViewById(R.id.pri0)).isChecked()){
            return 0;
        }else if(((RadioButton)findViewById(R.id.pri1)).isChecked()){
            return 1;
        }else if(((RadioButton)findViewById(R.id.pri2)).isChecked()){
            return 2;
        }
        return 0;
    }

    private boolean saveNote2Database(String content) {
        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoTable.COLUMN_NAME_CONTENT,content);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date.getTime();
        values.put(TodoContract.TodoTable.COLUMN_NAME_DATE, date.toString());
        values.put(TodoContract.TodoTable.COLUMN_NAME_STATE,0);
        values.put(TodoContract.TodoTable.COLUMN_NAME_PRI,getPriority());
        try {
            long newRowId = db.update(TodoContract.TodoTable.TABLE_NAME,values,
                    TodoContract.TodoTable.COLUMN_NAME_ID+" LIKE ?",
                    new String[]{""+gotid});
            Log.d("SQL", "update: "+newRowId);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
