package com.example.pfeproject.fragments.store;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.adapters.CategorieRecyclerViewAdapter;
import com.example.pfeproject.adapters.EntrepriseRecyclerViewAdapter;
import com.example.pfeproject.adapters.StoreRecyclerViewAdapter;
import com.example.pfeproject.callback.ItemClickSupport;
import com.example.pfeproject.model.Category;
import com.example.pfeproject.model.Entreprise;
import com.example.pfeproject.model.Product;
import com.example.pfeproject.ui.ArticleItemActivity;
import com.example.pfeproject.ui.EntrepriseDetailActivity;
import com.example.pfeproject.utils.ApiUrl;
import com.example.pfeproject.utils.SessionManager;
import com.example.pfeproject.utils.Types;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreFragment extends Fragment {
    private static final String TAG = "TAG_store_frag";

    @BindView(R.id.searchTextField)
    TextInputLayout searchInput;
    @BindView(R.id.searchEditText)
    TextInputEditText searchEditText;

    @BindView(R.id.grid_view)
    RecyclerView gridView;
    @BindView(R.id.recycler_entreprise)
    RecyclerView entrepriseRecycler;

    @BindView(R.id.linear)
    LinearLayout linearLayout;

    private StoreRecyclerViewAdapter storeAdapter;
    private CategorieRecyclerViewAdapter categorieAdapter;

    private ArrayList<Category> categorieList;
    private ArrayList<Entreprise> entreprisesList,entreprisesFiltredList;
    private ArrayList<Product> productsList;

    private SessionManager manager;

    private EntrepriseRecyclerViewAdapter entrepriseRecyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_store, container, false);
        ButterKnife.bind(this, root);
        manager = new SessionManager(getActivity());

        configureCategoryList();
        categorieItemClick();
        entrepriseItemClick();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                entrepriseRecyclerViewAdapter.getFilter().filter(s);
                entrepriseRecycler.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return root;
    }

    private void configureCategoryList() {
        categorieList = new ArrayList<>();
        entreprisesList = new ArrayList<>();
        productsList = new ArrayList<>();
        entreprisesFiltredList = new ArrayList<>();

        Ion.with(getActivity())
                .load(ApiUrl.GET, ApiUrl.apiGetAllCategory)
                .setHeader(Types.ContentType, Types.ContentTypeValue)
                .setHeader(Types.Authorization, "Bearer " + manager.getTokenUser())
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: category error in store fragment = " + e.getMessage());
                        } else {
                            Gson gson = new Gson();
                            Category[] categories = gson.fromJson(result.toString(), Category[].class);

                            for (Category category : categories) {
//                                Log.i(TAG, "onCompleted: foreach category id : " + category.getId() + " len entreprise :" + category.getEntreprises().length);
                                if (category.getEntreprises().length > 0)
                                {
                                    int count = 0;
                                    for (Entreprise entreprise : category.getEntreprises())
                                    {
                                        if(entreprise.getProducts().length > 0)
                                            count ++;
                                    }
                                    if(count > 0) {
                                        categorieList.add(category);
                                        for (Entreprise entreprise : category.getEntreprises())
                                            entreprisesFiltredList.add(entreprise);
                                    }
                                }

                            }

                            entrepriseRecyclerViewAdapter = new EntrepriseRecyclerViewAdapter(entreprisesFiltredList);
                            entrepriseRecycler.setAdapter(entrepriseRecyclerViewAdapter);
                            entrepriseRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
                            entrepriseRecycler.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));


                            Collections.sort(categorieList);
                            categorieAdapter = new CategorieRecyclerViewAdapter(categorieList);
                            gridView.setAdapter(categorieAdapter);
                            gridView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                            categorieAdapter.notifyDataSetChanged();

                            configureEntrepriseStoreList(categorieList.get(0));

                        }
                    }
                });
    }

    private void configureEntrepriseStoreList(Category category) {
//        Log.i(TAG, "configureEntrepriseStoreList: category " + category.getId() + " entrepriseList size : " + entreprisesList.size());
        if (entreprisesList.size() > 0)
            entreprisesList.clear();

        for (Entreprise e : category.getEntreprises())
        {
            if(e.getProducts().length > 0)
                entreprisesList.add(e);
        }

        linearLayout.removeAllViews();
        Collections.sort(entreprisesList);
        for (Entreprise model : entreprisesList) {
            LinearLayout parent = new LinearLayout(getActivity());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            parent.setLayoutParams(params);
            parent.setOrientation(LinearLayout.VERTICAL);
            params.setMargins(0, 0, 0, 100);


            View card = LayoutInflater.from(getActivity()).inflate(R.layout.model_store_label_layout, null, false);
            TextView storeLabel = card.findViewById(R.id.category_label);
            storeLabel.setText(model.getName());
            if (storeLabel.getParent() != null) {
                ((ViewGroup) storeLabel.getParent()).removeView(storeLabel);
            }
            parent.addView(storeLabel);

            Arrays.sort(model.getProducts());
            RecyclerView recyclerView = new RecyclerView(getActivity());
            storeAdapter = new StoreRecyclerViewAdapter(model.getProducts());
            recyclerView.setAdapter(storeAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            parent.addView(recyclerView);

            ItemClickSupport.addTo(recyclerView, R.layout.model_store_article_layout).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    startActivity(new Intent(getActivity(), ArticleItemActivity.class).putExtra("article_id", model.getProducts()[position].getId()));
                }
            });

            linearLayout.addView(parent);
        }
    }

    private void categorieItemClick() {
        ItemClickSupport.addTo(gridView, R.layout.fragment_store).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                Log.d(TAG, "onItemClicked: item clicked ... " + categorieList.get(position).getName());
                configureEntrepriseStoreList(categorieList.get(position));
                ChangeBackColor(v, position);
            }
        });
    }

    private void ChangeBackColor(View view, int position) {
//        Log.d("TAG", "ChangeBackColor: " + position);
        int previousItem = categorieAdapter.selectedItem;
        categorieAdapter.selectedItem = position;

        categorieAdapter.notifyItemChanged(previousItem);
        categorieAdapter.notifyItemChanged(position);
    }

    private void entrepriseItemClick() {
        ItemClickSupport.addTo(entrepriseRecycler, R.layout.fragment_publicity).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                startActivity(new Intent(getActivity(), EntrepriseDetailActivity.class)
                        .putExtra("entreprise_id",entreprisesFiltredList.get(position).getId())
                        .putExtra(Types.Fragment,1));
            }
        });
    }

}