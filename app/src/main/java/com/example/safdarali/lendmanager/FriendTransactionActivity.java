package com.example.safdarali.lendmanager;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.service.voice.AlwaysOnHotwordDetector;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safdarali.lendmanager.adapters.TransactionListAdapter;
import com.example.safdarali.lendmanager.data.Transaction;
import com.example.safdarali.lendmanager.provider.LendManagerContract;
import com.example.safdarali.lendmanager.sync.LendManagerLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class FriendTransactionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, TransactionListAdapter.TransactionSelectionListener {

    private int mFriendId;
    ImageButton mAddTranBtn;
    EditText mExpense, mCost, mDate;
    RecyclerView mTransactionsRv;
    TransactionListAdapter mAdapter;
    public static final int FRIEND_TRANSACTIONS_LOADER = 345;
    MenuItem delete_menu;
    ArrayList<Transaction> mTransactionList;
    HashSet<Transaction> mSelectedTransactions;
    TextView mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_transaction);

        Intent intent = getIntent();
        mFriendId = intent.getIntExtra(MainActivity.FRIEND_ID_LABEL, -1);

        mExpense = findViewById(R.id.expense_edit_tv);
        mCost = findViewById(R.id.cost_edit_tv);
        mTransactionsRv = findViewById(R.id.transactions);
        mTransactionsRv.setLayoutManager(new LinearLayoutManager(FriendTransactionActivity.this));
        mTransactionsRv.setHasFixedSize(true);
        mAddTranBtn = findViewById(R.id.add_transaction);
        mSelectedTransactions = new HashSet<Transaction>();
        final InputMethodManager inputManager = (InputMethodManager) FriendTransactionActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        mAddTranBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expense = mExpense.getText().toString();
                String costString = mCost.getText().toString();
                if (costString.isEmpty() || expense.isEmpty()) {
                    Toast.makeText(FriendTransactionActivity.this, "Please enter some valid values!", Toast.LENGTH_SHORT).show();
                    return;
                }
                expense = expense.substring(0, 1).toUpperCase() +
                        expense.substring(1);

                final double[] cost = {Double.parseDouble(costString)};
                if (cost[0] == 0) {
                    Toast.makeText(FriendTransactionActivity.this, "Expense can't be 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ContentValues cv = new ContentValues();
                cv.put(LendManagerContract.Transactions.FRIEND_ID, mFriendId);
                cv.put(LendManagerContract.Transactions.EXPENSE, expense);
                cv.put(LendManagerContract.Transactions.TIME, new Date().getTime());

                final int[] lendOrBorrow = new int[1];
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendTransactionActivity.this);
                final String finalExpense = expense;
                builder.setTitle("Is it a lend or borrow")
                        .setSingleChoiceItems(R.array.lend_or_borrow, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                lendOrBorrow[0] = i;
                            }
                        })
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (lendOrBorrow[0] == 1) {
                                    cost[0] = -cost[0];
                                }
                                cv.put(LendManagerContract.Transactions.AMOUNT, cost[0]);
                                Uri uri = FriendTransactionActivity.this.getContentResolver().
                                        insert(LendManagerContract.Transactions.CONTENT_URI, cv);
                                mExpense.setText("");
                                mCost.setText("");
                                Transaction transaction = new Transaction(Integer.parseInt(uri.getLastPathSegment()),
                                        finalExpense, cost[0], new Date().getTime());
                                mAdapter.add(transaction);
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                updateFriendAmount();
                            }
                        })
                        .show();
            }
        });
        getLoaderManager().restartLoader(FRIEND_TRANSACTIONS_LOADER, null, this).forceLoad();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transactions_menu, menu);
        mAmount = new TextView(this);
        mAmount.setText("0");
        mAmount.setPadding(0, 0, 0, 0);
        mAmount.setTypeface(Typeface.DEFAULT_BOLD);
        mAmount.setTextSize(24);
        mAmount.setTextColor(Color.WHITE);
        menu.add("Amount").setActionView(mAmount).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        updateFriendAmount();
        delete_menu = menu.findItem(R.id.delete_txn);
        delete_menu.setVisible(false);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new LendManagerLoader(this, FRIEND_TRANSACTIONS_LOADER, mFriendId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTransactionList = new ArrayList<>();
        while (data.moveToNext()) {
            int id = data.getInt(data.getColumnIndex(LendManagerContract.Transactions.TRANSACTION_ID));
            String expense = data.getString(data.getColumnIndex(LendManagerContract.Transactions.EXPENSE));
            double cost = data.getDouble(data.getColumnIndex(LendManagerContract.Transactions.AMOUNT));
            long date = data.getLong(data.getColumnIndex(LendManagerContract.Transactions.TIME));
            Transaction transaction = new Transaction(id, expense, cost, date);
            mTransactionList.add(transaction);
        }
        mAdapter = new TransactionListAdapter(mTransactionList, this);
        mTransactionsRv.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onSelectedListChanged(HashSet<Transaction> selectedTransactions) {
        mSelectedTransactions = selectedTransactions;
        if (mSelectedTransactions.size() == 0) {
            delete_menu.setVisible(false);
        } else delete_menu.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_txn) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(FriendTransactionActivity.this);
            builder.setTitle("Do you want to delete " + mSelectedTransactions.size() + " transactions!")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String[] ids = new String[mSelectedTransactions.size()];
                            int index = 0;
                            for (Transaction txn : mSelectedTransactions) {
                                ids[index++] = txn.getId() + "";
                            }
                            getContentResolver().delete(LendManagerContract.Transactions.CONTENT_URI,
                                    LendManagerContract.Transactions.TRANSACTION_ID + " IN (" + new String(new char[ids.length - 1]).replace("\0", "?,") + "?)",
                                    ids);
                            mAdapter.removeTransactions(mSelectedTransactions);
                            delete_menu.setVisible(false);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            updateFriendAmount();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateFriendAmount() {
        Cursor cur = getContentResolver().query(LendManagerContract.Friends.CONTENT_URI,
                null,
                LendManagerContract.Friends.FRIEND_ID + " = " + mFriendId,
                null,
                null,
                null);
        if (cur != null && cur.moveToNext()) {
            String friendName = cur.getString(cur.getColumnIndex(LendManagerContract.Friends.FRIEND_NAME));
            setTitle(friendName);
            mAmount.setText(cur.getFloat(cur.getColumnIndex(LendManagerContract.Friends.AMOUNT))+"");
        }
        cur.close();
    }
}
