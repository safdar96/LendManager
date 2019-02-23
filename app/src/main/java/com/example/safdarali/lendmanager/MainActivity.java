package com.example.safdarali.lendmanager;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safdarali.lendmanager.adapters.FriendsListAdapter;
import com.example.safdarali.lendmanager.data.Friend;
import com.example.safdarali.lendmanager.fragments.AddNewFriendDialog;
import com.example.safdarali.lendmanager.provider.LendManagerContract;
import com.example.safdarali.lendmanager.sync.LendManagerLoader;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FirebaseAuth.AuthStateListener, FriendsListAdapter.FriendItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int FRIENDS_LIST_LOADER = 123;
    private static final String SORTING_BY = "sorting_by";
    private static final int TIME = 0;
    private static final int AMOUNT = 1;
    private static final int NAME = 2;

    ArrayList<Friend> mFriendsList;
    FriendsListAdapter mAdapter;
    LinearLayout mNoFriendsLogo;
    RecyclerView mFriendsListRv;

    SharedPreferences mSharedPreferences;
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private NavigationView mNavigationView;
    public static final String FRIEND_ID_LABEL = "friend_id";
    FloatingActionButton mFab;
    MenuItem deleteMenu;
    String[] userDetailsLabel = {
            "Lend Amount        : Rs.",
            "Borrow Amount    : Rs."
    };
    private int mSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mSortBy = mSharedPreferences.getInt(SORTING_BY, 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewFriendDialog dialog = new AddNewFriendDialog();
                dialog.passAdapter(mAdapter, mSortBy);
                dialog.show(getSupportFragmentManager(), "AddNewFriendDialog");
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mFriendsListRv = findViewById(R.id.friends_list_rv);
        mNoFriendsLogo = findViewById(R.id.no_friends_logo);
        mFriendsListRv.setLayoutManager(new LinearLayoutManager(this));
        mFriendsListRv.setHasFixedSize(true);

        mFriendsList = new ArrayList<>();
        mAdapter = new FriendsListAdapter();
        mAdapter.setOnFriendItemClickListener(this);

        getLoaderManager().initLoader(FRIENDS_LIST_LOADER, null, this);

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }


    private void addUserDetailsToNavMenu() {
        View headerView = mNavigationView.getHeaderView(0);
        TextView navHeaderSalesmanName = headerView.findViewById(R.id.user_name);
        TextView navHeaderSalesmanEmail = headerView.findViewById(R.id.user_email);

        Cursor cursor = getContentResolver().query(LendManagerContract.MyAccount.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (mUser != null) {
            navHeaderSalesmanName.setText(mUser.getDisplayName());
            navHeaderSalesmanEmail.setText(mUser.getEmail());
        }

        Menu menu = mNavigationView.getMenu();

        if (cursor != null && cursor.moveToNext()) {
            double amount = cursor.getDouble(cursor.getColumnIndex(LendManagerContract.MyAccount.USER_BALANCE));
            if (amount > 0) {
                menu.getItem(0).setTitle(userDetailsLabel[0] + amount);
                menu.getItem(1).setTitle(userDetailsLabel[1] + "0.0");
            } else {
                menu.getItem(0).setTitle(userDetailsLabel[0] + "0.0");
                menu.getItem(1).setTitle(userDetailsLabel[1] + amount);
            }
        }
        cursor.close();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        deleteMenu = menu.findItem(R.id.action_delete_friends);
        deleteMenu.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_friends) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you want to remove selected friend/s" + "!")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            HashSet<Friend> friendSet = mAdapter.getSelectedFriendsIds();
                            String[] ids = new String[friendSet.size()];
                            int index = 0;
                            for (Friend friend : friendSet) {
                                ids[index++] = friend.getId() + "";
                            }
                            getContentResolver().delete(LendManagerContract.Friends.CONTENT_URI,
                                    LendManagerContract.Friends.FRIEND_ID + " IN (" + new String(new char[ids.length - 1]).replace("\0", "?,") + "?)",
                                    ids);
                            mAdapter.removeFriends(friendSet);
                            deleteMenu.setVisible(false);
                            try {
                                Thread.sleep(100);
                                addUserDetailsToNavMenu();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
            return true;
        } else if (id == R.id.action_sort_friends) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sort by")
                    .setSingleChoiceItems(new CharSequence[]{"Time", "Amount", "Name"}, mSortBy, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putInt(SORTING_BY, i);
                            editor.apply();
                            mSortBy = i;
                            mAdapter.sortFriends(i);
                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_lend_amount) {
        } else if (id == R.id.nav_borrow_amount) {

        } else if (id == R.id.backup) {
            Toast.makeText(this, "Feature under development!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.restore) {
            Toast.makeText(this, "Feature under development!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "https://drive.google.com/file/d/1FG26me_5qx-uqkUG7jok5L_JRcgEOn_z/view?usp=drivesdk");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else if (id == R.id.sign_out) {
            mAuth.signOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        addUserDetailsToNavMenu();
        mAuth.addAuthStateListener(this);
    }

    @Override
    protected void onPause() {
        mAuth.removeAuthStateListener(this);
        super.onPause();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        mUser = firebaseAuth.getCurrentUser();
        if (mUser == null) {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setAvailableProviders(
                            Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                    .build(), RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mUser = mAuth.getCurrentUser();
                Cursor cursor = getContentResolver().query(LendManagerContract.MyAccount.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                if (cursor == null) {
                    ContentValues cv = new ContentValues();
                    cv.put(LendManagerContract.MyAccount.USER_NAME, mUser.getDisplayName());
                    cv.put(LendManagerContract.MyAccount.USER_BALANCE, 0);
                    Uri uri = getContentResolver().insert(LendManagerContract.MyAccount.CONTENT_URI, cv);
                    if (uri == null) {
                        Toast.makeText(this, "Entry failed please install app again!!!", Toast.LENGTH_SHORT).show();
                    }
                }
                cursor.close();
            } else {
                Toast.makeText(this, "Sign in not successful!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onFriendItemClick(Friend friend) {
        Intent intent = new Intent(this, FriendTransactionActivity.class);
        intent.putExtra(FRIEND_ID_LABEL, friend.getId());
        startActivity(intent);
    }

    @Override
    public void onFriendSelectItem(boolean isSomeoneSelected) {
        deleteMenu.setVisible(isSomeoneSelected);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new LendManagerLoader(this, FRIENDS_LIST_LOADER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mFriendsList = new ArrayList<>();
        if (cursor.getCount() == 0) {
            mNoFriendsLogo.setVisibility(View.VISIBLE);
        } else {
            mNoFriendsLogo.setVisibility(View.INVISIBLE);
        }
        while (cursor.moveToNext()) {
            Friend friend = new Friend(cursor.getInt(cursor.getColumnIndex(LendManagerContract.Friends.FRIEND_ID)),
                    cursor.getString(cursor.getColumnIndex(LendManagerContract.Friends.FRIEND_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(LendManagerContract.Friends.AMOUNT)));
            mFriendsList.add(friend);
        }
        mAdapter.setList(mFriendsList, mSortBy);
        mFriendsListRv.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
