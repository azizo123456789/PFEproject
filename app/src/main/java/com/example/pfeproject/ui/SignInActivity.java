package com.example.pfeproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.model.Command;
import com.example.pfeproject.model.TotalPoint;
import com.example.pfeproject.utils.ApiUrl;
import com.example.pfeproject.utils.GlobalFunction;
import com.example.pfeproject.utils.SessionManager;
import com.example.pfeproject.utils.Types;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TAG_SignIn";
    @BindView(R.id.emailTextField)
    TextInputLayout emailInput;
    @BindView(R.id.passTextField)
    TextInputLayout passInput;
    @BindView(R.id.sign_in_btn)
    Button signIn;
//    @BindView(R.id.forget_pass_btn)
//    TextView ForgetPass;
    @BindView(R.id.sign_up_btn)
    TextView SignUp;
    @BindView(R.id.myProgBar)
    AVLoadingIndicatorView progBar;
    @BindView(R.id.message)
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        signIn.setOnClickListener(this);
//        ForgetPass.setOnClickListener(this);
        SignUp.setOnClickListener(this);
    }

    private void checkUserAccount(String email, String pass) {
        ArrayList<TextInputLayout> inputArray = new ArrayList<>(Arrays.asList(emailInput, passInput));
        GlobalFunction.disableInput(inputArray, null, signIn, false);
        progBar.setVisibility(View.VISIBLE);

        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", pass);

        Ion.with(SignInActivity.this)
                .load(ApiUrl.POST, ApiUrl.apiSignIn)
                .setHeader(Types.ContentType, Types.ContentTypeValue)
                .setJsonObjectBody(json)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e != null) {
                            Toast.makeText(SignInActivity.this, "" + getString(R.string.error_task), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: error in signIn = " + e.getMessage());
                            progBar.setVisibility(View.GONE);
                            GlobalFunction.disableInput(inputArray, null, signIn, true);
                        } else {
                            progBar.setVisibility(View.GONE);
                            int code = result.getHeaders().code();
                            Log.d(TAG, "onCompleted: code login " + code);
                            if (code == 200) {
                                String token = result.getHeaders().getHeaders().get("Authorization");
                                progBar.setVisibility(View.GONE);
                                SessionManager sessionManager = new SessionManager(SignInActivity.this);
                                sessionManager.CreateUserSession(token, result.getResult().get("Id").getAsString());
                                configureUserPoint(sessionManager);
                                startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                            } else {
                                message.setVisibility(View.VISIBLE);
                                progBar.setVisibility(View.GONE);
                                GlobalFunction.disableInput(inputArray, null, signIn, true);
                            }

//                            Log.d(TAG, "onCompleted: " + result.getHeaders().getHeaders().get("Authorization"));
                        }
                    }
                });
    }

    private void CheckUserForm() {
        String email_str = emailInput.getEditText().getText().toString();
        String pass_str = passInput.getEditText().getText().toString();
        if (email_str.isEmpty() || pass_str.isEmpty())
            Toast.makeText(SignInActivity.this, R.string.toast_complete_form, Toast.LENGTH_SHORT).show();
        else if (!GlobalFunction.isValidEmail(email_str) || pass_str.length() < 8) {
            if (!GlobalFunction.isValidEmail(email_str))
                GlobalFunction.setTextInputError(emailInput, getString(R.string.error_email));
            if (pass_str.length() < 8)
                GlobalFunction.setTextInputError(passInput, getString(R.string.error_pass));
        } else
            checkUserAccount(email_str, pass_str);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_btn:
                CheckUserForm();
                break;
            case R.id.sign_up_btn:
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                break;
//            case R.id.forget_pass_btn:
//                startActivity(new Intent(SignInActivity.this, ForgetPasswordActivity.class));
//                break;
        }

    }

    private void configureUserPoint(SessionManager manager) {
        Ion.with(SignInActivity.this)
                .load(ApiUrl.GET, ApiUrl.apiGetUserData + manager.getUserId())
                .setHeader(Types.ContentType, Types.ContentTypeValue)
                .setHeader(Types.Authorization, "Bearer " + manager.getTokenUser())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(SignInActivity.this, "" + getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: SignInActivity configureUserPoint = " + e.getMessage());
                        } else {

                            JsonArray jsonArrayPoint = result.get("totalpointsPerEntreprise").getAsJsonArray();

                            Gson gson = new Gson();

                            //                            add
                            Command[] commands = gson.fromJson(result.get("commands").getAsJsonArray().toString(), Command[].class);
                            manager.deleteUnusedCommand(commands);

                            TotalPoint[] points = gson.fromJson(jsonArrayPoint.toString(), TotalPoint[].class);
                            ArrayList<TotalPoint> pointArrayList = new ArrayList<>();
                            Collections.addAll(pointArrayList, points);
                            Log.d(TAG, "onCompleted:configureUserPoint SIGNIN pointArrayList.size()= " + pointArrayList.size());
                            manager.saveOrUpdateUserPointList(pointArrayList);


                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
    }
}