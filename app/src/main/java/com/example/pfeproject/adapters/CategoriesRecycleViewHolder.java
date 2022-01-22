package com.example.pfeproject.adapters;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Category;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesRecycleViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.categorie_label)
    TextView categorieLabel;
    @BindView(R.id.categorie_id)
    TextView categorieId;
    @BindView(R.id.categorie_linear)
    LinearLayout categorieLinear;
    @BindView(R.id.card_category)
    MaterialCardView cardView;

    public CategoriesRecycleViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateCategorieLabel(Category categorie, boolean change, Context ctx) {
        this.categorieLabel.setText(categorie.getName());
        this.categorieId.setText(categorie.getId());
        if (change) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.blue_teal));
            categorieLabel.setTextColor(ContextCompat.getColor(ctx, R.color.white));
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_200));
            categorieLabel.setTextColor(ContextCompat.getColor(ctx, R.color.black));
        }
    }

}
