package com.softdesign.devintensive.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.softdesign.devintensive.data.storage.models.UserDb;

import java.util.List;

public class MyFragment extends Fragment{

    private List<UserDb> mUserList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public void setData(List<UserDb> userData){
        mUserList = userData;
    }

    public  List<UserDb> getData() {
        return mUserList;
    }


}
