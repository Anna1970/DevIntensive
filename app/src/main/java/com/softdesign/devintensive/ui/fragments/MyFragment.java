package com.softdesign.devintensive.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.softdesign.devintensive.data.network.res.UserListRes;

import java.util.List;

public class MyFragment extends Fragment{

    private List<UserListRes.UserData> mUserList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public void setData(List<UserListRes.UserData> userData){
        mUserList = userData;
    }

    public  List<UserListRes.UserData> getData() {
        return mUserList;
    }


}
