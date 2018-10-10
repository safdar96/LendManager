package com.example.safdarali.lendmanager.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.safdarali.lendmanager.fragments.FriendTransactionsFragment;
import com.example.safdarali.lendmanager.fragments.FriendsListFragment;
import com.example.safdarali.lendmanager.provider.LendManagerContract;

public class LendManagerLoader extends android.support.v4.content.AsyncTaskLoader<Cursor> {
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
        if (mLoaderId == FriendsListFragment.FRIENDS_LIST_LOADER) {
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
