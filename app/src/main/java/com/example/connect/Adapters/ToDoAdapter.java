package com.example.connect.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connect.AddNewTask;
import com.example.connect.CallsFragment;
import com.example.connect.Models.ToDoModel;
import com.example.connect.R;
import com.example.connect.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private DatabaseHandler db;
    private CallsFragment fragment;


    public ToDoAdapter(DatabaseHandler db, CallsFragment fragment) {
        this.db = db;
        this.fragment = fragment;
        this.todoList = new ArrayList<>();
    }

    public int getItemCount(){
        return  todoList.size();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    private  boolean toBoolean(int n){
        return  n!=0;
    }
    public Context getContext() {

        if (fragment != null) {
            return fragment.requireContext();
        } else {
            // Handle the case when 'fragment' is null
            return null; // or throw an exception or handle it in a way that makes sense for your application
        }
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }
    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask addNewTaskFragment = new AddNewTask();
        addNewTaskFragment.setArguments(bundle);
        addNewTaskFragment.show(fragment.getActivity().getSupportFragmentManager(), AddNewTask.TAG);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
        }
    }
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        db.openDatabase();
//
//        final ToDoModel item = todoList.get(position);
//        holder.task.setText(item.getTask());
//        holder.task.setChecked(toBoolean(item.getStatus()));
//        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    db.updateStatus(item.getId(), 1);
//                } else {
//                    db.updateStatus(item.getId(), 0);
//                }
//            }
//        });
//    }
@Override
public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    // Check if db is not null before using it
    if (db != null) {
        db.openDatabase();

        final ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                } else {
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
    } else {
        // Handle the case where db is null (log an error, throw an exception, etc.)
        Log.e("ToDoAdapter", "DatabaseHandler is null");
    }
}

}
