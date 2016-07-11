package com.softdesign.devintensive.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.softdesign.devintensive.R;

/**
 * реализация метода TextWatcher
 */
public class DevTextWatcher implements TextWatcher {

    private EditText mEditText;

    public DevTextWatcher (EditText editText){
        mEditText = editText;
    }

    @Override
    public void afterTextChanged(Editable s) {
        switch (mEditText.getId()) {
            case R.id.phone:
                ValidationEditText.isPhoneNumber(mEditText, false);
                break;
            case R.id.e_mail:
                ValidationEditText.isEmailAddress(mEditText,false);
                break;
            case R.id.vk:
                ValidationEditText.isVkProfile(mEditText,false);
                break;
            case R.id.git:
                ValidationEditText.isGitProfile(mEditText,false);
                break;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
}
