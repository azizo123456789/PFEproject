package com.example.pfeproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Entreprise;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EntrepriseRecyclerViewAdapter extends RecyclerView.Adapter<EntrepriseRecycleViewHolder> implements Filterable {
    private ArrayList<Entreprise> entreprises,filtredEntreprises;
    private Context context;



    public EntrepriseRecyclerViewAdapter(ArrayList<Entreprise> entreprises) {
        this.entreprises = entreprises;
        filtredEntreprises = new ArrayList<>(entreprises);
    }

    @NonNull
    @Override
    public EntrepriseRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.model_entreprise_item_layout,parent,false);
        return new EntrepriseRecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrepriseRecycleViewHolder holder, int position) {
        holder.UpdateEntreprise(entreprises.get(position));
    }

    @Override
    public int getItemCount() {
        return entreprises.size();
    }

    @Override
    public Filter getFilter() {
        return FilterEntreprise;
    }

    public Filter FilterEntreprise = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.d("TAG", "performFiltering:CharSequence =  "+constraint);
            String searchText = constraint.toString().toLowerCase();
            ArrayList<Entreprise> tempList = new ArrayList<>();
            if (!searchText.isEmpty() || searchText.length() != 0)
            {
                for (Entreprise e : filtredEntreprises)
                {
                    if (e.getName().toLowerCase().contains(searchText))
                        tempList.add(e);
                }
            }
            Log.d("TAG", "performFiltering:tempList.size  =  "+tempList.size());
            FilterResults filterResults = new FilterResults();
            filterResults.values = tempList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            entreprises.clear();
            entreprises.addAll((Collection<? extends Entreprise>) results.values);
            notifyDataSetChanged();
        }
    };
}
