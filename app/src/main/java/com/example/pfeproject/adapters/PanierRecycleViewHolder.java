package com.example.pfeproject.adapters;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Panier;
import com.example.pfeproject.model.Product;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public  class PanierRecycleViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.item_label)
    TextView itemLabel;
    @BindView(R.id.item_point)
    TextView itemPoint;
    @BindView(R.id.item_soc)
    TextView itemSoc;
    @BindView(R.id.item_id)
    TextView itemId;

    @BindView(R.id.item_img)
    ImageView itemImg;
    @BindView(R.id.item_btn_remove)
    Button btnRemove;

    public PanierRecycleViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void UpdateUserBasketShopItem(Panier item, int product_position){
        Product product = item.getProducts()[product_position];
        itemLabel.setText(product.getName());
        itemSoc.setText(item.getEntreprise_name());
        itemPoint.setText(""+product.getDiscountPoints()+" points");
        itemId.setText(product.getId());
    }

    public void UpdateUserBasketShopItem(Product product){
        Log.d("TAG", "UpdateUserBasketShopItem: "+product.getId());
        itemLabel.setText(product.getName());
        itemSoc.setText(product.getEntreprise_name());
        itemPoint.setText(String.valueOf(product.getDiscountPoints())+" points");
        itemId.setText(product.getId());
    }
}
