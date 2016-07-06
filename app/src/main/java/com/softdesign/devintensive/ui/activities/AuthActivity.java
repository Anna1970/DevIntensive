package com.softdesign.devintensive.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import com.softdesign.devintensive.R;

/**
 * Показывает форму авторизации
 */
public class AuthActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity);
    }
}
