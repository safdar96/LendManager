package com.example.safdarali.lendmanager.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.safdarali.lendmanager.R;
import com.example.safdarali.lendmanager.adapters.FriendsListAdapter;
import com.example.safdarali.lendmanager.data.Friend;
import com.example.safdarali.lendmanager.provider.LendManagerContract;
import com.example.safdarali.lendmanager.sync.LendManagerLoader;

import java.util.ArrayList;
import java.util.HashSet;


public class FriendsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int FRIENDS_LIST_LOADER = 123;
    ArrayList<Friend> mFriendsList;
    FriendsListAdapter mAdapter;
    LinearLayout mNoFriendsLogo;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    RecyclerView mFriendsListRv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list_frament, container, false);
        mFriendsListRv = view.findViewById(R.id.friends_list_rv);
        mNoFriendsLogo = view.findViewById(R.id.no_friends_logo);
        mFriendsListRv.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        mFriendsListRv.setHasFixedSize(true);
        mFriendsList = new ArrayList<>();
        getLoaderManager().initLoader(FRIENDS_LIST_LOADER, null, this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new LendManagerLoader(getContext(), FRIENDS_LIST_LOADER);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mFriendsList = new ArrayList<>();
        if (cursor.getCount() == 0) {
            mNoFriendsLogo.setVisibility(View.VISIBLE);
        } else {
            mNoFriendsLogo.setVisibility(View.INVISIBLE);
        }
        while (cursor.moveToNext()) {
            Friend friend = new Friend();
            friend.setId(cursor.getInt(cursor.getColumnIndex(LendManagerContract.Friends.FRIEND_ID)));
            friend.setName(cursor.getString(cursor.getColumnIndex(LendManagerContract.Friends.FRIEND_NAME)));
            friend.setAmount(cursor.getDouble(cursor.getColumnIndex(LendManagerContract.Friends.AMOUNT)));
            mFriendsList.add(friend);
        }
        mAdapter.setList(mFriendsList);
        mFriendsListRv.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    public void passListener(FriendsListAdapter.FriendItemClickListener listener) {
        mAdapter = new FriendsListAdapter();
        mAdapter.setOnFriendItemClickListener(listener);
    }

    public HashSet<Integer> getSelectedFriends() {
        return mAdapter.getHashSet();
    }
}
