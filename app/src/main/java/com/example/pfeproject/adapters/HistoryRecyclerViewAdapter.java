package com.example.pfeproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Command;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewHolder> {
    ArrayList<Command> array;
    Context ctx;

    public HistoryRecyclerViewAdapter(ArrayList<Command> array) {
        this.array = array;
    }

    @NonNull
    @Override
    public HistoryRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.model_history_item_layout,parent,false);
        return new HistoryRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecyclerViewHolder holder, int position) {
        holder.UpdateHistoryData(array.get(position),ctx);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }
}
