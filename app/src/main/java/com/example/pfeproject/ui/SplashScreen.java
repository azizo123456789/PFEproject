package com.example.pfeproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Command;
import com.example.pfeproject.model.TotalPoint;
import com.example.pfeproject.utils.ApiUrl;
import com.example.pfeproject.utils.SessionManager;
import com.example.pfeproject.utils.Types;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "TAG_SplashScreen";
    private static final int SPLASH_DISPLAY_LENGTH = 3000;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        manager = new SessionManager(this);
        InitSplashCount();

    }

    private void InitSplashCount()
    {
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(checkUser())
                {
                    startActivity( new Intent(SplashScreen.this, HomeActivity.class) );
                    finish();
                }
                else
                {
                    startActivity( new Intent(SplashScreen.this, SignInActivity.class) );
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void configureUserPoint(){
        Ion.with(SplashScreen.this)
                .load(ApiUrl.GET,ApiUrl.apiGetUserData+manager.getUserId())
                .setHeader(Types.ContentType, Types.ContentTypeValue)
                .setHeader(Types.Authorization, "Bearer " + manager.getTokenUser())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(SplashScreen.this, "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: configureUserPoint = " + e.getMessage());
                        }
                        else
                        {
                            JsonArray jsonArrayPoint = result.get("totalpointsPerEntreprise").getAsJsonArray();

                            Gson gson = new Gson();

                            Command[] commands = gson.fromJson(result.get("commands").getAsJsonArray().toString() , Command[].class);
                            manager.deleteUnusedCommand(commands);

                            TotalPoint[] points = gson.fromJson(jsonArrayPoint.toString(),TotalPoint[].class);
                            ArrayList<TotalPoint> pointArrayList = new ArrayList<>();
                            Collections.addAll(pointArrayList,points);
                            Log.d(TAG, "onCompleted:configureUserPoint SPLASH SCREEN pointArrayList.size()= "+pointArrayList.size());

                            manager.saveOrUpdateUserPointList(pointArrayList);



                        }
                    }
                });

    }

    private boolean checkUser() {
        if(!manager.FirstLoggedIn()) {
            manager.InitiateNotLoggedUser();
            return false;
        }
        else
            return manager.isLoggedIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkUser())
            configureUserPoint();
    }

    @Override
    public void onBackPressed() {
    }
}