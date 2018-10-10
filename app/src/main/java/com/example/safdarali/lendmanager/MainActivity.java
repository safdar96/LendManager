package com.example.safdarali.lendmanager;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safdarali.lendmanager.adapters.FriendsListAdapter;
import com.example.safdarali.lendmanager.data.Friend;
import com.example.safdarali.lendmanager.fragments.AddNewFriendDialog;
import com.example.safdarali.lendmanager.fragments.DeleteAlertDialogFragment;
import com.example.safdarali.lendmanager.fragments.FriendTransactionsFragment;
import com.example.safdarali.lendmanager.fragments.FriendsListFragment;
import com.example.safdarali.lendmanager.provider.LendManagerContract;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FirebaseAuth.AuthStateListener, FriendsListAdapter.FriendItemClickListener {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private NavigationView mNavigationView;
    FriendsListFragment mFriendsListFragment;
    FriendTransactionsFragment mFriendTransactionsFragment;
    Fragment mCurrentFragment;
    FloatingActionButton mFab;
    MenuItem deleteMenu;
    String[] userDetailsLabel = {
            "Lend Amount        : Rs.",
            "Borrow Amount    : Rs."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewFriendDialog dialog = new AddNewFriendDialog();
                dialog.show(getSupportFragmentManager(), "AddNewFriendDialog");
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        addUserDetailsToNavMenu();

        mFriendsListFragment = new FriendsListFragment();
        mFriendsListFragment.passListener(this);
        mFriendTransactionsFragment = new FriendTransactionsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.main_container, mFriendsListFragment).commit();
        mCurrentFragment = mFriendsListFragment;
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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mCurrentFragment == mFriendTransactionsFragment) {
            mFab.setVisibility(View.VISIBLE);
            getSupportFragmentManager().
                    beginTransaction().replace(R.id.main_container, mFriendsListFragment).commit();
            mCurrentFragment = mFriendsListFragment;
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
            DeleteAlertDialogFragment dialogFragment = new DeleteAlertDialogFragment();
            dialogFragment.setSelectedList(mFriendsListFragment.getSelectedFriends());
            dialogFragment.show(getSupportFragmentManager(), "Delete Alert");
            deleteMenu.setVisible(false);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_lend_amount) {
        } else if (id == R.id.nav_borrow_amount) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

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
        } else {

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
                if (cursor != null && cursor.moveToNext()) {
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(LendManagerContract.MyAccount.USER_NAME, mUser.getDisplayName());
                    cv.put(LendManagerContract.MyAccount.USER_BALANCE, 0);
                    Uri uri = getContentResolver().insert(LendManagerContract.MyAccount.CONTENT_URI, cv);
                    if (uri == null) {
                        Toast.makeText(this, "Entry failed please install app again!!!", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                Toast.makeText(this, "Sign in not successful!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onFriendItemClick(Friend friend) {
        mFriendTransactionsFragment.setFriend(friend);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mFriendTransactionsFragment, "").commit();
        mCurrentFragment = mFriendTransactionsFragment;
        mFab.setVisibility(View.GONE);
    }

    @Override
    public void onFriendSelectItem(boolean isSomeoneSelected) {
        deleteMenu.setVisible(isSomeoneSelected);
    }
}
