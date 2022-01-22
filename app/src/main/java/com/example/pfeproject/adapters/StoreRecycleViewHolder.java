package com.example.pfeproject.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Product;
import com.example.pfeproject.utils.ApiUrl;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreRecycleViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.article_title)
    TextView article_title;

    @BindView(R.id.article_point)
    MaterialButton article_point;

    @BindView(R.id.article_price)
    TextView article_price;

    @BindView(R.id.article_image)
    ImageView article_image;




    public StoreRecycleViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void UpdateStoreData(Product article) {
        article_title.setText(article.getName());
        article_price.setText(""+article.getPrice()+" DT");
        article_point.setText(article.getDiscountPoints()+" points");
        if(article.getImageLink() != null) {
            String replacedImageLink = article.getImageLink().replace(ApiUrl.hostnameHost, ApiUrl.hostname);
            Picasso.get().load(replacedImageLink).placeholder(R.drawable.image_store).into(article_image);
        }
    }

    public void changePointBackground(Context ctx, int color){
        article_point.setBackgroundTintList(ContextCompat.getColorStateList(ctx,color));
    }

}
