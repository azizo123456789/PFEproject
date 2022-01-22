package com.example.pfeproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pfeproject.R;
import com.example.pfeproject.model.ModelCategorie;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Adapter extends ArrayAdapter<ModelCategorie>{

    ArrayList<ModelCategorie> categories ;
    public Adapter(@NonNull Context context, @NonNull ArrayList<ModelCategorie> objects) {
        super(context, R.layout.model_gridlayout_categorie_layout, objects);
        this.categories = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.model_gridlayout_categorie_layout,parent,false);

        TextView categorieLabel = convertView.findViewById(R.id.categorie_label);
//        ImageView categorieImg = convertView.findViewById(R.id.categorie_image);


        ModelCategorie categorie = getItem(position);
        categorieLabel.setText(categorie.getCatLabel());
//        categorieImg.setImageResource(categorie.getCatImage());

        return convertView;
    }

    @Override
    public int getCount() {
        return categories.size();
    }
}
