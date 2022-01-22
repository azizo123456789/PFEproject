package com.example.pfeproject.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Product;
import com.example.pfeproject.model.TotalPoint;
import com.example.pfeproject.utils.ApiUrl;
import com.example.pfeproject.utils.SessionManager;
import com.example.pfeproject.utils.Types;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleItemActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG_Article_item";

    @BindView(R.id.back_btn)
    ImageButton btn_back;
    @BindView(R.id.article_shop_btn)
    ExtendedFloatingActionButton btn_shop;

    @BindView(R.id.article_image)
    ImageView article_image;

    @BindView(R.id.article_label)
    TextView article_label;
    @BindView(R.id.article_discountPoints)
    TextView article_discountPoints;
    @BindView(R.id.article_price)
    TextView article_price;
    @BindView(R.id.article_discountPricePerPoints)
    TextView article_discountPricePerPoints;
    @BindView(R.id.article_description)
    TextView article_desc;

    private int article_point_test = 5200;
    private int user_point_test = 4200;

    private String article_id, entreprise_id,entreprise_name;
    private SessionManager manager;
    private Product product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_item);
        ButterKnife.bind(this);
        manager = new SessionManager(this);

        btn_back.setOnClickListener(this);
        btn_shop.setOnClickListener(this);

        configureArticleData();
    }

    private void configureArticleData() {
        Intent extra = getIntent();
        article_id = extra.getStringExtra("article_id");

        Ion.with(ArticleItemActivity.this)
                .load(ApiUrl.GET, ApiUrl.apiGetProductById + article_id)
                .setHeader(Types.ContentType, Types.ContentTypeValue)
                .setHeader(Types.Authorization, "Bearer " + manager.getTokenUser())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(ArticleItemActivity.this, "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: configureArticleData = " + e.getMessage());
                        } else {
                            product = new Product(article_id, result.get("name").getAsString() ,result.get("price").getAsDouble(),
                                    result.get("discountPriceperPoints").getAsDouble(), result.get("discountPoints").getAsInt(),
                                    result.get("imageLink").isJsonNull() ? null : result.get("imageLink").getAsString() );
                            article_label.setText(product.getName());
                            article_price.setText( String.valueOf(product.getPrice())+ " DT");
                            article_price.setPaintFlags(article_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            article_desc.setText(result.get("description").getAsString());
                            article_discountPoints.setText(String.valueOf(product.getDiscountPoints()) + " points");
                            double finalPrice = product.getPrice() - product.getDiscountPriceperPoints();
                            article_discountPricePerPoints.setText(String.valueOf(finalPrice) + "DT");

                            entreprise_id = result.get("entreprise").getAsJsonObject().get("id").getAsString();
                            entreprise_name = result.get("entreprise").getAsJsonObject().get("name").getAsString();

                            if(product.getImageLink() != null) {
                                String replacedImageLink = product.getImageLink().replace(ApiUrl.hostnameHost, ApiUrl.hostname);
                                Picasso.get().load(replacedImageLink).placeholder(R.drawable.image_store).into(article_image);
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.article_shop_btn:
                AddUserArticle();
                break;

            case R.id.back_btn:
                startActivity(new Intent(ArticleItemActivity.this, HomeActivity.class).putExtra(Types.Fragment, 1));
                finish();
                break;
        }
    }

    private void AddUserArticle() {
//  retrieve from array shared entreprise point  ( if userPoint > articlePoint okay else Toast and enabled false )
        manager.saveOrUpdateUserPointList(null);
        TotalPoint userPointPerEntreprise = manager.retrieveUserEntreprisePoint(entreprise_id);
        if (userPointPerEntreprise != null) {
            Log.d(TAG, "AddUserArticle: getDiscountPoints = "+product.getDiscountPoints()+" / user Point ="+userPointPerEntreprise.getTotalpoints()+" / rest point "+userPointPerEntreprise.getRestpoints());
            if (product.getDiscountPoints() > userPointPerEntreprise.getRestpoints())
                Toast.makeText(ArticleItemActivity.this, "" + getString(R.string.toast_shop_not_enable), Toast.LENGTH_SHORT).show();
            else {
//        add array panier kol item yzido fel panier yetzed fel liste total point -- w point per entreprise ---
//        ki yna7i el article mel panier les item yetna7aw w el total point ++ w point per entreprise ++
                boolean success = manager.AddItemToBasketShop(entreprise_id,entreprise_name,product);
                if (!success)
                    Toast.makeText(this, ""+getString(R.string.toast_shop_article_exist), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, ""+getString(R.string.toast_shop_enable), Toast.LENGTH_SHORT).show();

            }
        }
        else
//            if userPointPerEntreprise == null : entreprise not found
        {
            Toast.makeText(this, "" + getString(R.string.toast_shop_not_enable), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "AddUserArticle: NO ADD getDiscountPoints = "+product.getDiscountPoints()+" / entrId ="+entreprise_id);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, ""+getString(R.string.toast_on_back_pressed), Toast.LENGTH_SHORT).show();
    }
}