package com.example.pfeproject.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.utils.ApiUrl;
import com.example.pfeproject.utils.GlobalFunction;
import com.example.pfeproject.utils.Types;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.lamudi.phonefield.PhoneInputLayout;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "TAG_SignIn";
    @BindView(R.id.sign_up)
    Button signUp;
    @BindView(R.id.back_btn)
    ImageButton BackBtn;
    @BindView(R.id.emailTextField)
    TextInputLayout emailInput;
    @BindView(R.id.passTextField)
    TextInputLayout pass;
    @BindView(R.id.FirstNameTextField)
    TextInputLayout firstName;
    @BindView(R.id.LastNameTextField)
    TextInputLayout lastName;
    @BindView(R.id.phoneTextField)
    PhoneInputLayout phoneInputLayout;
    @BindView(R.id.addressTextField)
    TextInputLayout address;
    @BindView(R.id.myProgBar)
    AVLoadingIndicatorView progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        configurePhoneInputLayout();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first_str = firstName.getEditText().getText().toString();
                String last_str = lastName.getEditText().getText().toString();
                String email_str = emailInput.getEditText().getText().toString();
                String pass_str = pass.getEditText().getText().toString();
                String phone_str = phoneInputLayout.getPhoneNumber();
                String address_str = address.getEditText().getText().toString();
                boolean valid = true;

                if (first_str.isEmpty() || last_str.isEmpty() || email_str.isEmpty() || pass_str.isEmpty() || address_str.isEmpty() || phone_str.isEmpty())
                    Toast.makeText(SignUpActivity.this, R.string.toast_complete_form, Toast.LENGTH_SHORT).show();
                else if (!GlobalFunction.isValidEmail(email_str) || pass_str.length() < 8 || !phoneInputLayout.isValid()) {
                    if(!GlobalFunction.isValidEmail(email_str))
                        GlobalFunction.setTextInputError(emailInput,getString(R.string.error_email));
                    if(pass_str.length() < 8)
                        GlobalFunction.setTextInputError(pass,getString(R.string.error_pass));
                    if(!phoneInputLayout.isValid()) {
                        GlobalFunction.setPhoneInputError(phoneInputLayout,getString(R.string.error_phone));
                        valid = false;
                    }
                } else
                    CheckUserAccount(first_str, last_str, email_str, pass_str, phone_str, address_str);
            }
        });

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });
    }

    private void configurePhoneInputLayout() {
        phoneInputLayout.setHint(R.string.phone_input);
        phoneInputLayout.setDefaultCountry("TN");
        phoneInputLayout.getTextInputLayout().setBoxBackgroundColor(Color.parseColor("#FFFFFFFF"));
        phoneInputLayout.getTextInputLayout().setBoxStrokeColor(Color.parseColor("#253044"));
    }

    private void CheckUserAccount(String first, String last, String email, String pass, String phone, String address) {
        ArrayList<TextInputLayout> inputArray = new ArrayList<>(Arrays.asList(emailInput,this.pass,this.address,this.firstName,this.lastName));
        GlobalFunction.disableInput(inputArray,this.phoneInputLayout,this.signUp,false);
        progBar.setVisibility(View.VISIBLE);

        JsonObject UserJson = new JsonObject();
        UserJson.addProperty("email", email);
        UserJson.addProperty("password", pass);
        UserJson.addProperty("phone", phone);
        UserJson.addProperty("role", "client");
        UserJson.addProperty("firstName", first);
        UserJson.addProperty("lastName", last);
        UserJson.addProperty("adress", address);

        Ion.with(SignUpActivity.this)
                .load(ApiUrl.POST , ApiUrl.apiSignUp)
                .setHeader(Types.ContentType, Types.ContentTypeValue)
                .setJsonObjectBody(UserJson)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(SignUpActivity.this, "" + getString(R.string.error_task), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCompleted: error in sign up = " + e.getMessage());
                            progBar.setVisibility(View.GONE);
                            GlobalFunction.disableInput(inputArray,phoneInputLayout,signUp,true);
                        } else {
                            if (result.has("error")) {
                                Log.e(TAG, "onCompleted: error ... : " + result.get("error"));
                                GlobalFunction.setTextInputError(emailInput, getString(R.string.error_duplicated_email));
                                progBar.setVisibility(View.GONE);
                                GlobalFunction.disableInput(inputArray,phoneInputLayout,signUp,true);
                            } else {
                                Log.d(TAG, "onCompleted: good .... : " + result.get("message"));
                                progBar.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, "" + getString(R.string.toast_success_create_account), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
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