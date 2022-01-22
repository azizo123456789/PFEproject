package com.example.pfeproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Product;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StoreRecyclerViewAdapter extends RecyclerView.Adapter<StoreRecycleViewHolder> {
    private Product[] articleList;
    private Context context;

    public StoreRecyclerViewAdapter(Product[] articleList) {
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public StoreRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.model_store_article_layout, parent, false);

        return new StoreRecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreRecycleViewHolder holder, int position) {
        int item = articleList[position].getDiscountPoints();
        int color = -1;
        if (item <= 1000)
            color = R.color.between_0_10;
        else if (item <= 3000)
            color = R.color.between_10_30;
        else if (item <= 5000)
            color = R.color.between_30_50;
        else
            color = R.color.between_50;

        holder.UpdateStoreData(articleList[position]);
        holder.changePointBackground(context,color);
    }

    @Override
    public int getItemCount() {
        return articleList.length;
    }
}
