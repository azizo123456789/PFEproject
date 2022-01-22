package com.example.pfeproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.adapters.PubRecyclerViewAdapter;
import com.example.pfeproject.adapters.StoreRecyclerViewAdapter;
import com.example.pfeproject.callback.ItemClickSupport;
import com.example.pfeproject.fragments.video.VideoPlayerFragment;
import com.example.pfeproject.model.Category;
import com.example.pfeproject.model.Entreprise;
import com.example.pfeproject.model.Product;
import com.example.pfeproject.model.Publicity;
import com.example.pfeproject.utils.ApiUrl;
import com.example.pfeproject.utils.SessionManager;
import com.example.pfeproject.utils.Types;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class EntrepriseDetailActivity extends AppCompatActivity {
    private static final String TAG = "TAG_ent_act";

    @BindView(R.id.ent_article_recycler)
    RecyclerView articleRecycler;
    @BindView(R.id.ent_pub_recycler)
    RecyclerView pubRecycler;

    @BindView(R.id.ent_pic)
    CircleImageView entPic;
    @BindView(R.id.ent_name)
    TextView entName;
    @BindView(R.id.ent_stat_pub)
    TextView entStatPub;
    @BindView(R.id.ent_stat_article)
    TextView entStatArticle;

    @BindView(R.id.btn_back)
    ImageButton btn_Back;

    @BindView(R.id.txt_articles)
    TextView txt_articles;
    @BindView(R.id.txt_pub)
    TextView txt_pub;

    private String entreprise_id ;
    private int fragmentId=0;
    private SessionManager manager;
    private Entreprise entreprise;

    private StoreRecyclerViewAdapter articleRecyclerViewAdapter;
    private PubRecyclerViewAdapter pubRecyclerViewAdapter;

    private Publicity[] pubArray;
    private Product[] productsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entreprise_detail);
        ButterKnife.bind(this);
        manager = new SessionManager(this);

        entreprise_id = getIntent().getStringExtra("entreprise_id");
        fragmentId = getIntent().getIntExtra(Types.Fragment,0);
        configureEntrepriseData();
        articlesItemClick();
        publicityItemClick();

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntrepriseDetailActivity.this,HomeActivity.class).putExtra(Types.Fragment,fragmentId));
                finish();
            }
        });
    }

    private void configureEntrepriseData(){

        Ion.with(this)
                .load(ApiUrl.GET, ApiUrl.apiGetEntrepriseById+entreprise_id )
                .setHeader(Types.ContentType , Types.ContentTypeValue)
                .setHeader(Types.Authorization , manager.getTokenUser())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(EntrepriseDetailActivity.this, "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: configureEntrepriseData error = " + e.getMessage());
                        }
                        else
                        {
                            Gson gson = new Gson();
                            JsonArray pubJson = result.get("publicities").getAsJsonArray();
                            pubArray = gson.fromJson(pubJson.toString(),Publicity[].class);

                            JsonArray productJson = result.get("products").getAsJsonArray();
                            productsArray = gson.fromJson(productJson.toString(),Product[].class);

                            JsonObject categoryJson = result.get("category").getAsJsonObject();
                            Category category = new Category(categoryJson.get("id").getAsString(), categoryJson.get("name").getAsString());

                            entreprise = new Entreprise(entreprise_id, result.get("imageLink").isJsonNull() ? null : result.get("imageLink").getAsString(),
                                    result.get("name").getAsString(),pubArray,category, productsArray);

                            if(entreprise.getImageLink() != null) {
                                String replacedImageLink= entreprise.getImageLink().replace(ApiUrl.hostnameHost,ApiUrl.hostname);
                                Picasso.get().load(replacedImageLink).placeholder(R.drawable.avatar_company).into(entPic);
                            }
                            entName.setText(entreprise.getName());
                            entStatPub.setText(String.valueOf(entreprise.getPublicities().length));
                            entStatArticle.setText(String.valueOf(entreprise.getProducts().length));

                            articleRecyclerViewAdapter = new StoreRecyclerViewAdapter(productsArray);
                            articleRecycler.setAdapter(articleRecyclerViewAdapter);
                            articleRecycler.setLayoutManager(new LinearLayoutManager(EntrepriseDetailActivity.this,LinearLayoutManager.HORIZONTAL,false));
                            if (productsArray.length == 0)
                                txt_articles.setVisibility(View.GONE);

                            pubRecyclerViewAdapter = new PubRecyclerViewAdapter(pubArray);
                            pubRecycler.setAdapter(pubRecyclerViewAdapter);
                            pubRecycler.setLayoutManager(new LinearLayoutManager(EntrepriseDetailActivity.this,LinearLayoutManager.HORIZONTAL,false));
                            if (pubArray.length == 0)
                                txt_pub.setVisibility(View.GONE);

                        }
                    }
                });
    }

    private void articlesItemClick(){
        ItemClickSupport.addTo(articleRecycler , R.layout.activity_entreprise_detail).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                startActivity(new Intent(EntrepriseDetailActivity.this,ArticleItemActivity.class).putExtra("article_id",productsArray[position].getId()));
            }
        });
    }

    private void publicityItemClick(){
        ItemClickSupport.addTo(pubRecycler, R.layout.activity_entreprise_detail).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Toast.makeText(EntrepriseDetailActivity.this, "point to earn : "+pubArray[position].getPointToEarn(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onItemClicked: item click id :  "+pubArray[position].getId());
                Bundle bundle = new Bundle();
                bundle.putString("pubId",pubArray[position].getId());
                bundle.putString("pubVideoLink",pubArray[position].getVideoLink());
                bundle.putString("entrId",entreprise_id);
                VideoPlayerFragment.display(getSupportFragmentManager(),bundle);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, ""+getString(R.string.toast_on_back_pressed), Toast.LENGTH_SHORT).show();
    }
}