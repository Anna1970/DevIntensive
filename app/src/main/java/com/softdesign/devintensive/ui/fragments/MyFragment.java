package com.softdesign.devintensive.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.storage.models.UserDTO;

import java.util.List;

public class MyFragment extends Fragment{

    private List<UserDTO> mUserList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public void setData(List<UserDTO> userData){
        mUserList = userData;
    }

    public  List<UserDTO> getData() {
        return mUserList;
    }


}
