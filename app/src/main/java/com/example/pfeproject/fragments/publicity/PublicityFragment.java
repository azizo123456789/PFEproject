package com.example.pfeproject.fragments.publicity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.adapters.CategorieRecyclerViewAdapter;
import com.example.pfeproject.adapters.EntrepriseRecyclerViewAdapter;
import com.example.pfeproject.adapters.PubRecyclerViewAdapter;
import com.example.pfeproject.callback.ItemClickSupport;
import com.example.pfeproject.fragments.video.VideoPlayerFragment;
import com.example.pfeproject.model.Category;
import com.example.pfeproject.model.Entreprise;
import com.example.pfeproject.model.Publicity;
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

public class PublicityFragment extends Fragment {
    private static final String TAG = "TAG_Pub_Frag";

    @BindView(R.id.recycler_categorie)
    RecyclerView categorieRecycler;
    @BindView(R.id.recycler_entreprise)
    RecyclerView entrepriseRecycler;

    @BindView(R.id.search_btn)
    ImageButton searchBtn;
    @BindView(R.id.searchTextField)
    TextInputLayout searchInput;
    @BindView(R.id.searchEditText)
    TextInputEditText searchEditText;

    @BindView(R.id.linearV)
    LinearLayout linearLayout;

    private SessionManager manager;
    private ArrayList<Category> categorieList;
    private ArrayList<Entreprise> entrepriseList,entreprisesFiltredList;
    private ArrayList<Publicity> publicitiesList;
    private CategorieRecyclerViewAdapter categorieAdapter;
    private PubRecyclerViewAdapter publicityAdapter;

    private EntrepriseRecyclerViewAdapter entrepriseRecyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_publicity, container, false);
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
        entrepriseList = new ArrayList<>();
        entreprisesFiltredList = new ArrayList<>();
        publicitiesList = new ArrayList<>();

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
                            Log.e(TAG, "onCompleted: category error in pub fragment = " + e.getMessage());
                        } else {
                            Gson gson = new Gson();
                            Category[] categories = gson.fromJson(result.toString(), Category[].class);

                            if (categorieList.size() > 0)
                                categorieList.clear();

                            for (Category category : categories) {
//                                Log.i(TAG, "onCompleted: foreach category id : " + category.getId() + " len entreprise :" + category.getEntreprises().length);
                                if (category.getEntreprises().length > 0  )
                                {
                                    int count = 0;
                                    for (Entreprise entreprise : category.getEntreprises()) {
                                        if (entreprise.getPublicities().length > 0)
                                            count ++ ;
                                    }
                                    if (count > 0) {
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
                            categorieRecycler.setAdapter(categorieAdapter);
                            categorieRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                            configureEntreprisePubList(categorieList.get(0));
                        }
                    }
                });
    }

    private void configureEntreprisePubList(Category category) {
//        Log.i(TAG, "configureEntreprisePubList: category " + category.getId() + " entrepriseList size : " + entrepriseList.size());
        if (entrepriseList.size() > 0)
            entrepriseList.clear();
        for (Entreprise e : category.getEntreprises())
        {
            if(e.getPublicities().length > 0)
                entrepriseList.add(e);
        }


        linearLayout.removeAllViews();
        Collections.sort(entrepriseList);
        for (Entreprise entreprise : entrepriseList) {
            LinearLayout parent = new LinearLayout(getActivity());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            parent.setLayoutParams(params);
            parent.setOrientation(LinearLayout.VERTICAL);
            params.setMargins(0, 0, 0, 100);

            View card = LayoutInflater.from(getActivity()).inflate(R.layout.model_store_label_layout, null, false);
            TextView pubLabel = card.findViewById(R.id.category_label);
            pubLabel.setText(entreprise.getName());
            if (pubLabel.getParent() != null) {
                ((ViewGroup) pubLabel.getParent()).removeView(pubLabel);
            }
            parent.addView(pubLabel);

            RecyclerView recyclerView = new RecyclerView(getActivity());
            Arrays.sort(entreprise.getPublicities());
            publicityAdapter = new PubRecyclerViewAdapter(entreprise.getPublicities());
            recyclerView.setAdapter(publicityAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            parent.addView(recyclerView);

            ItemClickSupport.addTo(recyclerView, R.layout.fragment_publicity).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                    Log.d(TAG, "onItemClicked: ub item click id :  "+entreprise.getPublicities()[position].getId()+" entrepriseId ="+entreprise.getId());
                    Bundle bundle = new Bundle();
                    bundle.putString("pubId",entreprise.getPublicities()[position].getId());
                    bundle.putString("pubVideoLink",entreprise.getPublicities()[position].getVideoLink());
                    bundle.putString("entrId",entreprise.getId());
                    VideoPlayerFragment.display(getActivity().getSupportFragmentManager(),bundle);
                }
            });

            linearLayout.addView(parent);
        }

    }

    private void categorieItemClick() {
        ItemClickSupport.addTo(categorieRecycler, R.layout.fragment_publicity).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                configureEntreprisePubList(categorieList.get(position));
                ChangeBackColor(v, position);
            }
        });
    }

    private void entrepriseItemClick() {
        ItemClickSupport.addTo(entrepriseRecycler, R.layout.fragment_publicity).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                startActivity(new Intent(getActivity(), EntrepriseDetailActivity.class)
                        .putExtra("entreprise_id",entreprisesFiltredList.get(position).getId())
                        .putExtra(Types.Fragment,0));
            }
        });
    }

    private void ChangeBackColor(View view, int position) {
        int previousItem = categorieAdapter.selectedItem;
        categorieAdapter.selectedItem = position;

        categorieAdapter.notifyItemChanged(previousItem);
        categorieAdapter.notifyItemChanged(position);
    }


}