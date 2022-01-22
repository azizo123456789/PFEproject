package com.example.pfeproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pfeproject.R;
import com.example.pfeproject.model.TotalPoint;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserPointRecyclerViewAdapter extends RecyclerView.Adapter<UserPointRecyclerViewHolder> {
    ArrayList<TotalPoint> array;
    Context ctx;

    public UserPointRecyclerViewAdapter(ArrayList<TotalPoint> array) {
        this.array = array;
    }

    @NonNull
    @Override
    public UserPointRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.model_point_item_layout,parent,false);
        return new UserPointRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPointRecyclerViewHolder holder, int position) {
        holder.UpdateUserPoint(array.get(position),ctx);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }
}
