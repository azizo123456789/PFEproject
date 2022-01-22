package com.example.pfeproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.adapters.UserPointRecyclerViewAdapter;
import com.example.pfeproject.model.Command;
import com.example.pfeproject.model.Entreprise;
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
import java.util.Comparator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserPointActivity extends AppCompatActivity {
    private static final String TAG = "Tag_point_act";

    @BindView(R.id.user_point_list)
    RecyclerView recyclerView;
    @BindView(R.id.total_user_point)
    TextView total_user_point;

    @BindView(R.id.btn_back)
    ImageButton btn_back;

    private UserPointRecyclerViewAdapter adapter;
    private ArrayList<TotalPoint> pointsArray;

    private SessionManager manager;
    private int totalPointLength = 0;
    private int userTotalPoint = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_point);
        manager = new SessionManager(this);

        ButterKnife.bind(this);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserPointActivity.this, HomeActivity.class).putExtra(Types.Fragment, 2));
            }
        });

        configureUserPointsList();
    }

    private void configureUserPointsList() {
        pointsArray = new ArrayList<>();
        Ion.with(this)
                .load(ApiUrl.GET, ApiUrl.apiGetUserData + manager.getUserId())
                .setHeader(Types.ContentType, Types.ContentTypeValue)
                .setHeader(Types.Authorization, "Bearer " + manager.getTokenUser())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(UserPointActivity.this, "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: configureUserPointsList = " + e.getMessage());
                        } else {
                            JsonArray totalpointsPerEntrepriseArray = result.get("totalpointsPerEntreprise").getAsJsonArray();
                            Gson gson = new Gson();
//                            Point[] points = gson.fromJson(totalpointsPerEntrepriseArray.toString(),Point[].class);
                            //                            add
                            Command[] commands = gson.fromJson(result.get("commands").getAsJsonArray().toString(), Command[].class);
//                            manager.deleteUnusedCommand(commands);
                            TotalPoint[] totalPointsArray = gson.fromJson(totalpointsPerEntrepriseArray.toString(), TotalPoint[].class);
                            ArrayList<TotalPoint> totalPoints = new ArrayList<>();
                            Collections.addAll(totalPoints, totalPointsArray);
                            manager.saveOrUpdateUserPointList(totalPoints);

                            ArrayList<TotalPoint> points = manager.retrieveUserPoints();
                            for (TotalPoint p : points)
                                Log.i(TAG, "configureUserPointsList: .....................................\nE_id :" + p.getIdEntreprise() + "\ntotal :" + p.getTotalpoints() + "\nrest" + p.getRestpoints());

                            totalPointLength = points.size();
                            for (TotalPoint userPoint : points)
                                setUserPointPerEntreprise(userPoint);


                        }
                    }
                });
    }

    private void setUserPointPerEntreprise(TotalPoint pointPerEntreprise) {
        try {
            totalPointLength--;
            userTotalPoint += Integer.parseInt(pointPerEntreprise.getTotalpoints());
            Log.d(TAG, "setUserPointPerEntreprise: totalPointLength = " + totalPointLength);
            Ion.with(this)
                    .load(ApiUrl.GET, ApiUrl.apiGetEntrepriseById + pointPerEntreprise.getIdEntreprise())
                    .setHeader(Types.ContentType, Types.ContentTypeValue)
                    .setHeader(Types.Authorization, "Bearer " + manager.getTokenUser())
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null) {
                                Toast.makeText(UserPointActivity.this, "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onCompleted: setUserPointPerEntreprise = " + e.getMessage());
                            } else {
                                Entreprise entreprise = new Entreprise(result.get("id").getAsString(),
                                        result.get("imageLink").isJsonNull() ? null : result.get("imageLink").getAsString(),
                                        result.get("name").getAsString());
                                pointPerEntreprise.setEntreprise(entreprise);

                                pointsArray.add(pointPerEntreprise);
                                Log.d(TAG, "onCompleted: array size " + pointsArray.size());
                                if (totalPointLength == 0) {
                                    Collections.sort(pointsArray, new Comparator<TotalPoint>() {
                                        @Override
                                        public int compare(TotalPoint o1, TotalPoint o2) {
                                            return o1.getEntreprise().getName().toLowerCase().compareTo(o2.getEntreprise().getName().toLowerCase());
                                        }
                                    });
                                    adapter = new UserPointRecyclerViewAdapter(pointsArray);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(UserPointActivity.this, LinearLayoutManager.VERTICAL, false));
                                    recyclerView.setAdapter(adapter);

//                                total_user_point.setText(String.valueOf(userTotalPoint));
                                    total_user_point.setText(String.valueOf(manager.retrieveUserTotalRestPoint()));
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "setUserPointPerEntreprise: Exception = " + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "" + getString(R.string.toast_on_back_pressed), Toast.LENGTH_SHORT).show();
    }
}