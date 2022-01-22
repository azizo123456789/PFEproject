package com.example.pfeproject.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Client;
import com.example.pfeproject.model.Command;
import com.example.pfeproject.model.Point;
import com.example.pfeproject.model.TotalPoint;
import com.example.pfeproject.utils.ApiUrl;
import com.example.pfeproject.utils.GlobalFunction;
import com.example.pfeproject.utils.SessionManager;
import com.example.pfeproject.utils.Types;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.lamudi.phonefield.PhoneInputLayout;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAGProfile_Act";
    @BindView(R.id.btn_back)
    ImageButton btn_back;
    @BindView(R.id.btn_update)
    ImageButton btn_update;
    @BindView(R.id.phone_input_layout)
    PhoneInputLayout phoneInputLayout;
    @BindView(R.id.FirstNameTextField)
    TextInputLayout firstName;
    @BindView(R.id.LastNameTextField)
    TextInputLayout lastName;
    @BindView(R.id.emailTextField)
    TextInputLayout email;
    @BindView(R.id.addressTextField)
    TextInputLayout address;
    @BindView(R.id.myProgBar)
    AVLoadingIndicatorView progBar;
    private SessionManager manager;

    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        manager = new SessionManager(this);

        configurePhoneInputUI();

        setUserData();

        btn_update.setOnClickListener(this);
        btn_back.setOnClickListener(this);

    }

    private void configurePhoneInputUI() {
        phoneInputLayout.setHint(R.string.phone_input);
        phoneInputLayout.setDefaultCountry("TN");
        phoneInputLayout.getTextInputLayout().setBoxBackgroundColor(Color.parseColor("#FFFFFFFF"));
        phoneInputLayout.getTextInputLayout().setBoxStrokeColor(Color.parseColor("#253044"));
    }

    private void setUserData() {
        ArrayList<TextInputLayout> inputArray = new ArrayList<>(Arrays.asList(email, address, firstName, lastName));
        GlobalFunction.disableInput(inputArray, phoneInputLayout, null, false);
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
                            Toast.makeText(UserProfileActivity.this, "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: error in profile ACTIVITY = " + e.getMessage());
                        } else {
                            Gson gson = new Gson();
                            client = new Client(result.get("email").getAsString(), result.get("phone").getAsString(),
                                    result.get("imageLink").isJsonNull() ? null : result.get("imageLink").getAsString(),
                                    result.get("firstName").getAsString(), result.get("lastName").getAsString(),
                                    result.get("adress").getAsString());
                            client.setPassword(result.get("password").getAsString());
                            client.setId(result.get("id").getAsString());
                            client.setCommands(gson.fromJson(result.get("commands").getAsJsonArray().toString(), Command[].class));
                            client.setPoints(gson.fromJson(result.get("points").getAsJsonArray().toString(), Point[].class));
                            client.setTotalpointsPerEntreprise(gson.fromJson(result.get("totalpointsPerEntreprise").getAsJsonArray().toString(), TotalPoint[].class));

                            firstName.getEditText().setText(client.getFirstName());
                            lastName.getEditText().setText(client.getLastName());
                            email.getEditText().setText(client.getEmail());
                            phoneInputLayout.getEditText().setText(client.getPhone());
                            address.getEditText().setText(client.getAdress());

                            GlobalFunction.disableInput(inputArray, phoneInputLayout, null, true);
                            progBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                startActivity(new Intent(UserProfileActivity.this, HomeActivity.class).putExtra(Types.Fragment, 2));
                finish();
                break;

            case R.id.btn_update:
                CheckUserData();
                break;
        }

    }

    private void CheckUserData() {
        String str_firstName = firstName.getEditText().getText().toString();
        String str_lastName = lastName.getEditText().getText().toString();
        String str_email = email.getEditText().getText().toString();
        String str_address = address.getEditText().getText().toString();
        String str_phone = phoneInputLayout.getPhoneNumber();
        boolean valid = true;
        if (str_firstName.isEmpty() || str_lastName.isEmpty() || str_email.isEmpty() || str_address.isEmpty() || str_phone.isEmpty())
            Toast.makeText(this, "" + getString(R.string.toast_complete_form), Toast.LENGTH_SHORT).show();
        else if (!GlobalFunction.isValidEmail(str_email) || !phoneInputLayout.isValid()) {
            if (!GlobalFunction.isValidEmail(str_email))
                GlobalFunction.setTextInputError(email, getString(R.string.error_email));
            if (!phoneInputLayout.isValid()) {
                GlobalFunction.setPhoneInputError(phoneInputLayout, getString(R.string.error_phone));
                valid = false;
            }
        } else {
//            Client user = new Client(str_email, str_phone, str_firstName, str_lastName, str_address);
            client.setEmail(str_email);
            client.setPhone(str_phone);
            client.setFirstName(str_firstName);
            client.setLastName(str_lastName);
            client.setAdress(str_address);
            UpdateUserData(client);
        }

    }

    private void UpdateUserData(Client user) {
        ArrayList<TextInputLayout> inputArray = new ArrayList<>(Arrays.asList(email, address, firstName, lastName));
        GlobalFunction.disableInput(inputArray, phoneInputLayout, null, false);
        progBar.setVisibility(View.VISIBLE);

//        JsonObject json = new JsonObject();
//        json.addProperty("id", manager.getUserId());
//        json.addProperty("email", user.getEmail());
//        json.addProperty("password", user.getPassword());
//        json.addProperty("firstName", user.getFirstName());
//        json.addProperty("lastName", user.getLastName());
//        json.addProperty("phone", user.getPhone());
//        json.addProperty("adress", user.getAdress());
//
//        json.addProperty("role", user.role);
//        json.addProperty("imageLink", user.getImageLink());

//        json.addProperty("commands",commandArray);
//        json.addProperty("points",String.valueOf(pointArray));
//        json.addProperty("totalpointsPerEntreprise",String.valueOf(totalpointsPerEntrepriseArray));


        try {
            JSONObject object = new JSONObject();
            object.put("id", manager.getUserId());
            object.put("email", user.getEmail());
            object.put("password", user.getPassword());
            object.put("firstName", user.getFirstName());
            object.put("lastName", user.getLastName());
            object.put("phone", user.getPhone());
            object.put("adress", user.getAdress());
            object.put("role", user.role);
            object.put("imageLink", user.getImageLink());

            JsonParser jsonParser = new JsonParser();
            JsonObject gsonObject = (JsonObject) jsonParser.parse(object.toString());

            Ion.with(this)
                    .load(ApiUrl.PUT, ApiUrl.apiUpdateUserData)
                    .setHeader(Types.ContentType, Types.ContentTypeValue)
                    .setHeader(Types.Authorization, "Bearer " + manager.getTokenUser())
                    .setJsonObjectBody(gsonObject)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null) {
                                Toast.makeText(UserProfileActivity.this, "" + getString(R.string.error_task), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onCompleted: error in profile ACTIVITY update method = " + e.getMessage());
                                GlobalFunction.disableInput(inputArray, phoneInputLayout, null, true);
                                progBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(UserProfileActivity.this, "" + getString(R.string.toast_success_update_profile), Toast.LENGTH_SHORT).show();
                                progBar.setVisibility(View.GONE);
                                startActivity(new Intent(UserProfileActivity.this, HomeActivity.class).putExtra(Types.Fragment, 2));
                                finish();
                            }
                        }
                    });

        } catch (Exception ex) {
            Log.e(TAG, "UpdateUserData: JsonException " + ex.getMessage());
        }



    }


    @Override
    public void onBackPressed() {
        Toast.makeText(this, ""+getString(R.string.toast_on_back_pressed), Toast.LENGTH_SHORT).show();
    }
}