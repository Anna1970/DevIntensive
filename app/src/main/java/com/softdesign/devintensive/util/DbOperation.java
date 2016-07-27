package com.softdesign.devintensive.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;

import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.UserDb;
import java.util.List;

public final class DbOperation  extends ChronosOperation<List<UserDb>> {

    private String mQuery;

    public DbOperation() {
    }

    public DbOperation(String query) {
        mQuery = query;
    }

    @Nullable
    @Override
    public List<UserDb> run() {
        final List<UserDb> result;

        if (mQuery == null){
            result = DataManager.getInstance().getUserListFromDb();
        } else {
            result = DataManager.getInstance().getUserListByName(mQuery);
        }
        return result;
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<List<UserDb>>> getResultClass() {
        return Result.class;
    }

    public final static class Result extends ChronosOperationResult<List<UserDb>> {

    }

}
