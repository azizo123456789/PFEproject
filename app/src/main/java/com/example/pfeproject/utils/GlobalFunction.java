package com.example.pfeproject.utils;

import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.lamudi.phonefield.PhoneInputLayout;

import java.util.ArrayList;

public class GlobalFunction {
    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static void setTextInputError(TextInputLayout inputLayout, String error) {
        inputLayout.setError(error);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                inputLayout.setError("");
            }
        }, 2500);
    }

    public static void setPhoneInputError(PhoneInputLayout phoneInputLayout, String error) {
        phoneInputLayout.setError(error);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                phoneInputLayout.setError("");
            }
        }, 2500);
    }

    public static void disableInput(ArrayList<TextInputLayout> textInputLayoutList , PhoneInputLayout phoneInputLayout, Button btn , boolean enable)
    {
        for(TextInputLayout input : textInputLayoutList)
            input.setEnabled(enable);

        if(phoneInputLayout != null)
            phoneInputLayout.setEnabled(enable);
        if (btn != null)
            btn.setEnabled(enable);
    }
}
