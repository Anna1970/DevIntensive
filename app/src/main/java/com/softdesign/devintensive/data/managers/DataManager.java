package com.softdesign.devintensive.data.managers;

import android.content.Context;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

import com.softdesign.devintensive.data.network.PicassoCache;
import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.UserDb;
import com.softdesign.devintensive.data.storage.models.UserDbDao;
import com.softdesign.devintensive.util.DevIntensiveApplication;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static DataManager INSTANCE = null;
    private Picasso mPicasso;

    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DaoSession mDaoSession;

    public DataManager() {

        this.mPreferencesManager = new PreferencesManager();
        this.mContext = DevIntensiveApplication.getContext();
        this.mRestService = ServiceGenerator.createService(RestService.class);
        this.mPicasso = new PicassoCache(mContext).getPicassoInstance();
        this.mDaoSession = DevIntensiveApplication.getDaoSession();
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }

        return INSTANCE;
    }
    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    public Context getContext(){
        return mContext;
    }

    public Picasso getPicasso(){
        return mPicasso;
    }
//region =========== Network ===========

    public Call<UserModelRes> loginUser( UserLoginReq userLoginReq){
        return mRestService.loginUser(userLoginReq);
    }

    public Call<ResponseBody> uploadPhoto(String userId, File imageFile){
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),imageFile);
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo",imageFile.getName(),requestBody);
        return mRestService.uploadPhoto(userId, part);
    }

    public Call<UserListRes> getUserListFromNetwork() {
        return mRestService.getUserList();
    }

//endregion

// region =========== Database ===========

    public List<UserDb> getUserListFromDb() {
        List<UserDb> userList = new ArrayList<>();

        try {
            userList = mDaoSession.queryBuilder(UserDb.class)
                    .where(UserDbDao.Properties.CodeLines.gt(0))
                    .orderDesc(UserDbDao.Properties.CodeLines)
                    .build()
                    .list();
        } catch (Exception e){
            e.printStackTrace();
        }

        return userList;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }


    public List<UserDb> getUserListByName(String query){

        List<UserDb> userList = new ArrayList<>();
        try {
            userList = mDaoSession.queryBuilder(UserDb.class)
                    .where(UserDbDao.Properties.Rating.gt(0), UserDbDao.Properties.SearchName.like("%" + query.toUpperCase() +"%"))
                    .orderDesc(UserDbDao.Properties.CodeLines)
                    .build()
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }


    //endregion

}
