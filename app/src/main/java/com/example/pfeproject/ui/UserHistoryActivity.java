package com.example.pfeproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.adapters.HistoryRecyclerViewAdapter;
import com.example.pfeproject.adapters.PubRecyclerViewAdapter;
import com.example.pfeproject.model.Command;
import com.example.pfeproject.model.ModelPublicity;
import com.example.pfeproject.utils.ApiUrl;
import com.example.pfeproject.utils.SessionManager;
import com.example.pfeproject.utils.Types;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHistoryActivity extends AppCompatActivity {
    private static final String TAG = "TAG_History_Act";
    @BindView(R.id.recycler_history)
    RecyclerView recyclerView;
    @BindView(R.id.btn_back)
    ImageButton btn_back;
    @BindView(R.id.myProgBar)
    AVLoadingIndicatorView progBar;
    @BindView(R.id.empty_recycler)
    TextView empty_recycler;
    PubRecyclerViewAdapter pubAdapter;
    ArrayList<ModelPublicity> pubArray;
    private ArrayList<Command> commands;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);
        ButterKnife.bind(this);
        manager = new SessionManager(this);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserHistoryActivity.this, HomeActivity.class).putExtra(Types.Fragment, 2));
                finish();
            }
        });

        configureHistoryList();

    }

    private void configureHistoryList() {
        commands = new ArrayList<>();
        progBar.setVisibility(View.VISIBLE);
        Ion.with(this)
                .load(ApiUrl.GET, ApiUrl.apiGetUserData + manager.getUserId())
                .setHeader(Types.ContentType, Types.ContentTypeValue)
                .setHeader(Types.Authorization, "Bearer " + manager.getTokenUser())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(UserHistoryActivity.this, "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: setUserPointPerEntreprise = " + e.getMessage());
                        } else {
                            JsonArray commandsJson = result.get("commands").getAsJsonArray();
                            Gson gson = new Gson();
                            Command[] commandsArray = gson.fromJson(commandsJson.toString(), Command[].class);
                            progBar.setVisibility(View.GONE);
                            if (commandsArray.length > 0) {
                                Collections.addAll(commands, commandsArray);
                                Collections.sort(commands);
//                                Collections.reverse(commands);
                                HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(commands);
                                recyclerView.setLayoutManager(new LinearLayoutManager(UserHistoryActivity.this));
                                recyclerView.setAdapter(adapter);
                            } else
                                empty_recycler.setVisibility(View.VISIBLE);

                        }
                    }
                });

    }

    private void ConfigureHistoryRecycler() {
//        cmdArrayList = new ArrayList<>();
//        cmdArrayList.add(new ModelCmd("#15478","12-12-2015","15000",Types.InProgress));
//        cmdArrayList.add(new ModelCmd("#15479","12-12-2015","15000",Types.Cancled));
//        cmdArrayList.add(new ModelCmd("#15475","12-12-2015","15000",Types.InProgress));
//        cmdArrayList.add(new ModelCmd("#15473","12-12-2015","15000",Types.Confirmed));
//        cmdArrayList.add(new ModelCmd("#154781","12-12-2015","15000",Types.InProgress));
//
//        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(cmdArrayList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
    }

    private void staticInitHistoryList() {
        progBar.setVisibility(View.VISIBLE);
        Ion.with(UserHistoryActivity.this)
                .load(ApiUrl.GET, ApiUrl.apiGetAllCmd)
                .setHeader(Types.ContentType, Types.ContentTypeValue)
                .setHeader(Types.Authorization, "Bearer " + manager.getTokenUser())
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {
                            Toast.makeText(UserHistoryActivity.this, "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: error in history act load data = " + e.getMessage());

                        } else {
                            Log.d(TAG, "onCompleted: load data good ... len : " + result.size());
                            progBar.setVisibility(View.GONE);
                            if (result.size() == 0)
                                empty_recycler.setVisibility(View.VISIBLE);
                            else {
                                ConfigureHistoryRecycler();
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, ""+getString(R.string.toast_on_back_pressed), Toast.LENGTH_SHORT).show();
    }
}