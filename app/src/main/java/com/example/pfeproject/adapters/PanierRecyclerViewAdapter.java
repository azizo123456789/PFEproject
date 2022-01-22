package com.example.pfeproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pfeproject.R;
import com.example.pfeproject.callback.ListItemClickListener;
import com.example.pfeproject.model.Panier;
import com.example.pfeproject.model.Product;
import com.example.pfeproject.utils.SessionManager;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PanierRecyclerViewAdapter extends RecyclerView.Adapter<PanierRecycleViewHolder> {
    private static final String TAG = "Tag_panierAdapter";
    Context ctx;
    private ArrayList<Panier> articles;
    private ArrayList<Product> products;
    private SessionManager manager;
    final private ListItemClickListener mOnClickListener;

    public PanierRecyclerViewAdapter(ArrayList<Panier> list, SessionManager manager,ListItemClickListener mOnClickListener) {
        this.articles = list;
        this.manager = manager;
        this.mOnClickListener = mOnClickListener;
        configurePanierProductList();
    }
    private void configurePanierProductList(){
        products = new ArrayList<>();
        Log.d(TAG, "configurePanierProductList: productAdapter size before : "+products.size()+" articlesSize= "+articles.size());
        for(Panier e : articles)
        {
            for (Product product : e.getProducts()) {
                product.setEntreprise_id(e.getEntreprise_id());
                product.setEntreprise_name(e.getEntreprise_name());
                products.add(product);
            }
        }
        Log.d(TAG, "configurePanierProductList: productAdapter size after : "+products.size());

    }

    @NonNull
    @Override
    public PanierRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.model_panier_item_layout, parent, false);
        return new PanierRecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PanierRecycleViewHolder holder, int position) {
        Product product = products.get(position);
//        Log.d(TAG, "onBindViewHolder: size ... "+products.size()+" position  = "+position);
        holder.UpdateUserBasketShopItem(product);

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onListItemClick(position,product);
            }
        });
    }

    public void configureList(ArrayList<Panier> arrayList,int position) {
        articles = arrayList;
        notifyDataSetChanged();
        notifyItemRangeChanged(position, articles.size());
        configurePanierProductList();
    }

    public int UserBasketTotal(){
        notifyDataSetChanged();
        return manager.retrievebasketShopTotalPoint();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
