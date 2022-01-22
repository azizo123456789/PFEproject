package com.example.pfeproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Category;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategorieRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecycleViewHolder> {
    private ArrayList<Category> categorie_list;
    private Context context;
    public static int lastClickedPosition = -1;
    public int selectedItem;


    public CategorieRecyclerViewAdapter(ArrayList<Category> categorie_list) {
        this.categorie_list = categorie_list;
        selectedItem = 0;
    }

    @NonNull
    @Override
    public CategoriesRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
//        modified...
        View view = inflater.inflate(R.layout.model_gridlayout_categorie_layout, parent, false);

        return new CategoriesRecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesRecycleViewHolder holder, int position) {
        boolean change = position == selectedItem;
        holder.updateCategorieLabel(categorie_list.get(position),change,context);
    }

    @Override
    public int getItemCount() {
        return categorie_list.size();
    }
}
