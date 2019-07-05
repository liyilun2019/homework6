package com.byted.camp.todolist.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byted.camp.todolist.MainActivity;
import com.byted.camp.todolist.NoteEditActivity;
import com.byted.camp.todolist.NoteOperator;
import com.byted.camp.todolist.R;
import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.db.TodoContract;

import java.util.ArrayList;
import java.util.List;

import static com.byted.camp.todolist.db.TodoDbHelper.TAG;

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class NoteListAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private final NoteOperator operator;
    private final List<Note> notes = new ArrayList<>();

    public NoteListAdapter(NoteOperator operator) {
        this.operator = operator;
    }

    public void refresh(List<Note> newNotes) {
        notes.clear();
        if (newNotes != null) {
            notes.addAll(newNotes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(itemView, operator);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int pos) {
        holder.bind(notes.get(pos));
        final int index = pos;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity)v.getContext();
                Intent intent = new Intent(mainActivity, NoteEditActivity.class);
                intent.putExtra(TodoContract.TodoTable.COLUMN_NAME_ID,notes.get(index).id);
                intent.putExtra(TodoContract.TodoTable.COLUMN_NAME_PRI,notes.get(index).getPriority());
                intent.putExtra(TodoContract.TodoTable.COLUMN_NAME_CONTENT,notes.get(index).getContent());
                Log.d(TAG, "onClick: "+v.getContext().getClass().getName());
                mainActivity.startActivityForResult(intent,MainActivity.REQUEST_CODE_MODIFY);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
