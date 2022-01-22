package com.example.pfeproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Publicity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PubRecyclerViewAdapter extends RecyclerView.Adapter<PubRecycleViewHolder> {
    private Publicity[] pub_list;
    Context context;


    public PubRecyclerViewAdapter(Publicity[] pub_list) {
        this.pub_list = pub_list;
    }


    @NonNull
    @Override
    public PubRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.model_publicity_layout, parent, false);

        return new PubRecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PubRecycleViewHolder holder, int position) {
        int item = pub_list[position].getPointToEarn();
        int color = -1;
        if (item <= 10)
            color = holder.cardView.getContext().getResources().getColor(R.color.between_0_10);
        else if (item <= 30)
            color = holder.cardView.getContext().getResources().getColor(R.color.between_10_30);
        else if (item <= 50)
            color = holder.cardView.getContext().getResources().getColor(R.color.between_30_50);
        else
            color = holder.cardView.getContext().getResources().getColor(R.color.between_50);

        holder.UpdateCardColor(color);
        holder.UpdatePubLabel(context,pub_list[position]);
    }

    @Override
    public int getItemCount() {
        return pub_list.length;
    }

}
