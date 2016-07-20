package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.UserDb;
import com.softdesign.devintensive.data.storage.models.UserDbDao;

import com.softdesign.devintensive.util.AppConfig;
import com.softdesign.devintensive.util.ConstantManager;
import com.softdesign.devintensive.util.Helper;
import com.softdesign.devintensive.util.MessageEvent;
import com.softdesign.devintensive.util.NetworkStatusChecked;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
public class AuthActivity extends BaseActivity {

    final static String TAG = ConstantManager.TAG_PREFIX + " AuthActivity";

    @BindView(R.id.auth_button) Button mSignIn;
    @BindView(R.id.forgot_pass) TextView mRemeberPassword;
    @BindView(R.id.login_email_et) EditText mLogin;
    @BindView(R.id.login_password_et) EditText mPassword;
    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;

    private DataManager mDataManager;
    private RepositoryDao mRepositoryDao;
    private UserDbDao mUserDao;


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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.auth_activity);
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();
        mUserDao = mDataManager.getDaoSession().getUserDbDao();
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

        switch (event.message) {
            case ConstantManager.NETWORK_NOT_AVAILABLE:
                Helper.showSnackbar(mCoordinatorLayout,"В данный момент сеть не доступна, повторите попытку позже.");
                break;
            case ConstantManager.INCORRECT_LOGIN_OR_PASSWORD:
                Helper.showSnackbar(mCoordinatorLayout, "Неверный логин или пароль");
                break;
            case ConstantManager.NOT_RESPONSE:
                Helper.showSnackbar(mCoordinatorLayout, " Ошибка!!! Что-то пошло не так");
                break;
            case ConstantManager.AUTH_ERROR:
                Helper.showSnackbar(mCoordinatorLayout,"Ошибка авторизации");
                break;
            case ConstantManager.SERVER_ERROR:
                Helper.showSnackbar(mCoordinatorLayout,"Список пользователей не может быть получен");
                break;
            case ConstantManager.USERS_SUCCESS_SAVED:
                Helper.showSnackbar(mCoordinatorLayout,"Успешная авторизация");
                startMainActivity();
                break;
        }

        hideProgress();
    }

    private void remeberPassword(){
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }

    private void loginSuccess(UserModelRes userModel){

        showProgress();

        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());

        saveUserValues(userModel);
        saveUserProfileData(userModel);
        saveUserName(userModel);
        saveUserPhoto(userModel);
        saveUserAvatar(userModel);

        saveUserInDb();
    }

    private void startMainActivity(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(AuthActivity.this, MainActivity.class);
                //Intent loginIntent = new Intent(AuthActivity.this, UserListActivity.class);
                startActivity(loginIntent);
            }
        }, AppConfig.START_DELAY);

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
                        EventBus.getDefault().post(new MessageEvent(ConstantManager.INCORRECT_LOGIN_OR_PASSWORD));
                    } else {
                        EventBus.getDefault().post(new MessageEvent(ConstantManager.NOT_RESPONSE));
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    Log.e(TAG, "Ошибка при авторизации: " + t.getMessage());
                    EventBus.getDefault().post(new MessageEvent(ConstantManager.AUTH_ERROR));
                }
            });
        } else {
            EventBus.getDefault().post(new MessageEvent(ConstantManager.NETWORK_NOT_AVAILABLE));
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void saveUserValues(UserModelRes userModel){
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRaiting(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };
        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void saveUserProfileData(UserModelRes userModel){
        List<String> userProfileData = new ArrayList<>();
        userProfileData.add(userModel.getData().getUser().getContacts().getPhone());
        userProfileData.add(userModel.getData().getUser().getContacts().getEmail());
        userProfileData.add(userModel.getData().getUser().getContacts().getVk());
        userProfileData.add(userModel.getData().getUser().getRepositories().getRepo().get(0).getGit());
        userProfileData.add(userModel.getData().getUser().getPublicInfo().getBio());
        mDataManager.getPreferencesManager().saveUserProfileData(userProfileData);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void saveUserName(UserModelRes userModel){
        List<String> userName = new ArrayList<>();
        userName.add(userModel.getData().getUser().getFirstName());
        userName.add(userModel.getData().getUser().getSecondName());
        mDataManager.getPreferencesManager().saveUserName(userName);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void saveUserPhoto(UserModelRes userModel){
        Uri uri = Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto());
        mDataManager.getPreferencesManager().saveUserPhoto(uri);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void saveUserAvatar(UserModelRes userModel){
        Uri uri = Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar());
        mDataManager.getPreferencesManager().saveUserAvatar(uri);
    }

    private void saveUserInDb() {

        if (NetworkStatusChecked.isNetworkAvailable(this)) {

            Call<UserListRes> call = mDataManager.getUserListFromNetwork();
            call.enqueue(new Callback<UserListRes>() {

                @Override
                public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                    try {
                        if (response.code() == 200) {

                            List<Repository> allRepositories = new ArrayList<Repository>();
                            List<UserDb> allUsers = new ArrayList<UserDb>();

                            for (UserListRes.UserData userRes : response.body().getData()) {
                                allRepositories.addAll(getRepoListFromUserRes(userRes));
                                allUsers.add(new UserDb(userRes));
                            }

                            mRepositoryDao.insertOrReplaceInTx(allRepositories);
                            mUserDao.insertOrReplaceInTx(allUsers);

                            EventBus.getDefault().post(new MessageEvent(ConstantManager.USERS_SUCCESS_SAVED));

                        } else {
                            Log.e(TAG, "onResponse: " + String.valueOf(response.errorBody().source()));
                            //);
                            EventBus.getDefault().post(new MessageEvent(ConstantManager.SERVER_ERROR));
                        }

                    } catch (NullPointerException e) {
                        Log.e(TAG, e.toString());
                        EventBus.getDefault().post(new MessageEvent(ConstantManager.NOT_RESPONSE));
                    }
                }

                @Override
                public void onFailure(Call<UserListRes> call, Throwable t) {

                }
            });
        } else {
            EventBus.getDefault().post(new MessageEvent(ConstantManager.NETWORK_NOT_AVAILABLE));
        }

    }

    private List<Repository> getRepoListFromUserRes(UserListRes.UserData userData) {
        final String usedId = userData.getId();
        List<Repository> repositories = new ArrayList<>();

        for (UserModelRes.Repo repositoryRes : userData.getRepositories().getRepo()){
            repositories.add(new Repository(repositoryRes, usedId));
        }

        return repositories;
    }

}