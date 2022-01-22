package com.example.pfeproject.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.adapters.PanierRecyclerViewAdapter;
import com.example.pfeproject.callback.ListItemClickListener;
import com.example.pfeproject.model.Command;
import com.example.pfeproject.model.Panier;
import com.example.pfeproject.model.Product;
import com.example.pfeproject.model.TotalPoint;
import com.example.pfeproject.utils.ApiUrl;
import com.example.pfeproject.utils.SessionManager;
import com.example.pfeproject.utils.Types;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserPanierActivity extends AppCompatActivity implements ListItemClickListener, View.OnClickListener {
    private static final String TAG = "TAG_userPanier";
    @BindView(R.id.btn_back)
    ImageButton btn_back;

    @BindView(R.id.btn_panier_confirm)
    Button btn_panier_confirm;

    @BindView(R.id.recycler_panier)
    RecyclerView recyclerPanier;

    @BindView(R.id.panier_total)
    TextView panier_total;

    @BindView(R.id.myProgBar)
    AVLoadingIndicatorView myProgBar;

    PanierRecyclerViewAdapter adapter;
    int total = 0;
    private ArrayList<Panier> userbasketShop;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panier);
        ButterKnife.bind(this);

        manager = new SessionManager(this);

        btn_back.setOnClickListener(this);
        btn_panier_confirm.setOnClickListener(this);

        configureUserBasketShop();
    }

    private void configureUserBasketShop() {
        userbasketShop = manager.retrieveUserBasketShop();

        if (userbasketShop.size() > 0) {
            adapter = new PanierRecyclerViewAdapter(userbasketShop, manager, this);
            recyclerPanier.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerPanier.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
            recyclerPanier.setAdapter(adapter);
            total = adapter.UserBasketTotal();
        } else
            btn_panier_confirm.setVisibility(View.GONE);

        panier_total.setText(String.valueOf(total) + " points");
    }


    @Override
    public void onListItemClick(int position, Product product) {
        manager.removeBasketShopItem(product.getEntreprise_id(), product.getId());
        adapter.configureList(manager.retrieveUserBasketShop(), position);
        total = manager.retrievebasketShopTotalPoint();
        panier_total.setText(String.valueOf(total) + " points");
        if (total == 0)
            btn_panier_confirm.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                startActivity(new Intent(UserPanierActivity.this, HomeActivity.class).putExtra(Types.Fragment, 2));
                finish();
                break;

            case R.id.btn_panier_confirm:
                createDialogBox();
                break;
        }
    }

    private void createDialogBox() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.dialog_title))
                .setMessage(getString(R.string.dialog_supporting_text))
                .setNeutralButton(getString(R.string.dialog_decline), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(getString(R.string.dialog_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myProgBar.setVisibility(View.VISIBLE);
                confirmCommande();
            }
        }).show();
    }

    private void confirmCommande(){
//        get all product ids form baskset shop
        String userProduct = "";
        ArrayList<Panier> userBasketShopArray = manager.retrieveUserBasketShop();
        for (Panier panier : userBasketShopArray){
            for (int i = 0 ; i < panier.getProducts().length ; i++)
            {
                if (userProduct.isEmpty())
                    userProduct += "idp="+panier.getProducts()[i].getId();
                else
                    userProduct = userProduct +"&idp="+panier.getProducts()[i].getId();
            }
        }
        Log.d(TAG, "confirmCommande: api url => "+ApiUrl.apiSaveUserCommand+manager.getUserId()+"/product/?"+userProduct);

//        passer la commande
        Ion.with(UserPanierActivity.this)
                .load(ApiUrl.POST , ApiUrl.apiSaveUserCommand+manager.getUserId()+"/product/?"+userProduct)
                .setHeader(Types.ContentType , Types.ContentTypeValue)
                .setHeader(Types.Authorization , "Bearer " + manager.getTokenUser())
                .setJsonObjectBody(new JsonObject())
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e != null) {
                            Toast.makeText(UserPanierActivity.this, "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: UserPanierActivity saveCommand error =  = " + e.getMessage());
                            myProgBar.setVisibility(View.GONE);
                        }else {
                            int code = result.getHeaders().code();
                            if(code == 200)
                            {
                                myProgBar.setVisibility(View.GONE);
//                                rst : passed waiting for confirmation
                                if(result.getResult().has("message"))
                                {
                                    saveUserCommandArray();
                                }
                                else
                                    Log.e(TAG, "onCompleted: command not passed even code == 200");
                            }
                            else
                                Log.e(TAG, "onCompleted: code passed commande == "+code );
                            myProgBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

    private void saveUserCommandArray(){
        Ion.with(UserPanierActivity.this)
                .load(ApiUrl.GET , ApiUrl.apiGetUserData+manager.getUserId())
                .setHeader(Types.ContentType , Types.ContentTypeValue)
                .setHeader(Types.Authorization , "Bearer " + manager.getTokenUser())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(UserPanierActivity.this, "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: saveUserCommandArray error =  = " + e.getMessage());
                            myProgBar.setVisibility(View.GONE);
                        }
                        else
                        {
//                              1-save user actual command :
//                                  command [len-1] => save in shared pref
                            Gson gson = new Gson();
                            Command[] commands = gson.fromJson(result.get("commands").getAsJsonArray().toString() , Command[].class);
                            TotalPoint[] points = gson.fromJson(result.get("totalpointsPerEntreprise").getAsJsonArray().toString() , TotalPoint[].class);
                            manager.checkAndSaveUserCommand(commands,total);
                            Log.d(TAG, "onCompleted: manager.retrieveUserCommand(); "+manager.retrieveUserCommand().size());
//                              2-remove items from basket shop
                            manager.deleteAllItemInsideBasketShop();
//                              3-update userTotal point per entreprise
                            manager.saveOrUpdateUserPointList(null);

                            Toast.makeText(UserPanierActivity.this, ""+getString(R.string.toast_passed_command), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UserPanierActivity.this , UserHistoryActivity.class));
                            finish();
                        }
                    }
                });


    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, ""+getString(R.string.toast_on_back_pressed), Toast.LENGTH_SHORT).show();
    }
}