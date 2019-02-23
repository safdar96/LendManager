package com.example.safdarali.lendmanager.sync;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import com.example.safdarali.lendmanager.MainActivity;
import com.example.safdarali.lendmanager.provider.LendManagerContract;

public class LendManagerLoader extends AsyncTaskLoader<Cursor> {
    private int mFriendId;
    int mLoaderId;
    public LendManagerLoader(Context context, int loaderId) {
        super(context);
        mLoaderId = loaderId;
    }

    public LendManagerLoader(Context context, int loaderId, int id) {
        super(context);
        mLoaderId = loaderId;
        mFriendId = id;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }
    
    @Override
    public Cursor loadInBackground() {
        if (mLoaderId == MainActivity.FRIENDS_LIST_LOADER) {
            return getContext().getContentResolver().query(LendManagerContract.Friends.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        } else {
            return getContext().getContentResolver().query(LendManagerContract.Transactions.CONTENT_URI,
                    null,
                    LendManagerContract.Transactions.FRIEND_ID + " = " + mFriendId,
                    null,
                    null);
        }
    }
}
