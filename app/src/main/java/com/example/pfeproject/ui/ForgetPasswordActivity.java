package com.example.pfeproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgetPasswordActivity extends AppCompatActivity {
    @BindView(R.id.emailTextField)
    TextInputLayout emailInput;

    @BindView(R.id.forget_pass_btn)
    Button forgetPassBtn;

    @BindView(R.id.back_btn)
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        ButterKnife.bind(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPasswordActivity.this, SignInActivity.class));
            }
        });

//        forgetPassBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email_str = emailInput.getEditText().getText().toString();
//                if(email_str.isEmpty())
//                    Toast.makeText(ForgetPasswordActivity.this, R.string.toast_complete_form, Toast.LENGTH_SHORT).show();
//                else
//                    CheckUserEmail(email_str);
//            }
//        });
    }

    private void CheckUserEmail(String email)
    {
        startActivity(new Intent(ForgetPasswordActivity.this,SignInActivity.class));
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, ""+getString(R.string.toast_on_back_pressed), Toast.LENGTH_SHORT).show();
    }
}