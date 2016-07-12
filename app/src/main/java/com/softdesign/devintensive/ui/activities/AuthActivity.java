package com.softdesign.devintensive.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.util.Helper;
import com.softdesign.devintensive.util.NetworkStatusChecked;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Показывает форму авторизации
 */
public class AuthActivity extends Activity {

    @BindView(R.id.auth_button) Button mSignIn;
    @BindView(R.id.forgot_pass) TextView mRemeberPassword;
    @BindView(R.id.login_email_et) EditText mLogin;
    @BindView(R.id.login_password_et) EditText mPassword;
    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;

    private DataManager mDataManager;

    @OnClick({R.id.auth_button, R.id.forgot_pass})
    public void onClickButton (View view){
        switch (view.getId()){
            case R.id.auth_button:
                setSignIn();
                break;
            case R.id.forgot_pass:
                remeberPassword();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity);

        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

    }

    private void remeberPassword(){
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }

    private void loginSuccess(UserModelRes userModel){

        Helper.showSnackbar(mCoordinatorLayout,userModel.getData().getToken());

        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());

        saveUserValues(userModel);
        saveUserProfileData(userModel);
        saveUserName(userModel);
        saveUserPhoto(userModel);
        saveUserAvatar(userModel);

        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }

    private void setSignIn() {
        if (NetworkStatusChecked.isNetworkAvailable(this)) {

            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString(),
                    mPassword.getText().toString()));

            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        loginSuccess(response.body());
                    } else if (response.code() == 404) {
                        Helper.showSnackbar(mCoordinatorLayout, "Неверный логин или пароль");
                    } else {
                        Helper.showSnackbar(mCoordinatorLayout, " Ошибка!!! ");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    Log.d("AuthActivity", "Ошибка при авторизации: " + t.getMessage());
                }
            });
        } else {
            Helper.showSnackbar(mCoordinatorLayout,"В данный момент сеть не доступна, повторите попытку позже.");
        }
    }

    private void saveUserValues(UserModelRes userModel){
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRaiting(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };

        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    private void saveUserProfileData(UserModelRes userModel){
        List<String> userProfileData = new ArrayList<>();
        userProfileData.add(userModel.getData().getUser().getContacts().getPhone());
        userProfileData.add(userModel.getData().getUser().getContacts().getEmail());
        userProfileData.add(userModel.getData().getUser().getContacts().getVk());
        userProfileData.add(userModel.getData().getUser().getRepositories().getRepo().get(0).getGit());
        userProfileData.add(userModel.getData().getUser().getPublicInfo().getBio());
        mDataManager.getPreferencesManager().saveUserProfileData(userProfileData);
    }

    private void saveUserName(UserModelRes userModel){
        List<String> userName = new ArrayList<>();
        userName.add(userModel.getData().getUser().getFirstName());
        userName.add(userModel.getData().getUser().getSecondName());
        mDataManager.getPreferencesManager().saveUserName(userName);
    }

    private void saveUserPhoto(UserModelRes userModel){
        Uri uri = Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto());
        mDataManager.getPreferencesManager().saveUserPhoto(uri);
    }

    private void saveUserAvatar(UserModelRes userModel){
        Uri uri = Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar());
        mDataManager.getPreferencesManager().saveUserAvatar(uri);
    }
}