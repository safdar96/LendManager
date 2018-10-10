package com.example.safdarali.lendmanager.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.safdarali.lendmanager.R;
import com.example.safdarali.lendmanager.adapters.TransactionListAdapter;
import com.example.safdarali.lendmanager.data.Friend;
import com.example.safdarali.lendmanager.data.Transaction;
import com.example.safdarali.lendmanager.provider.LendManagerContract;
import com.example.safdarali.lendmanager.sync.LendManagerLoader;

import java.util.ArrayList;
import java.util.Date;

public class FriendTransactionsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int FRIEND_TRANSACTIONS_LOADER = 345;
    Friend mFriend;
    ImageButton mAddTranBtn;
    EditText mExpense, mCost, mDate;
    RecyclerView mTransactionsRv;
    TransactionListAdapter mAdapter;

    ArrayList<Transaction> mTransactionList;
    public FriendTransactionsFragment() {
        // Required empty public constructor
    }

    public void setFriend(Friend friend) {
        mFriend = friend;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_transactions, container, false);
        mExpense = view.findViewById(R.id.expense_edit_tv);
        mCost = view.findViewById(R.id.cost_edit_tv);
        mTransactionsRv = view.findViewById(R.id.transactions);
        mTransactionsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mTransactionsRv.setHasFixedSize(true);
        mAddTranBtn = view.findViewById(R.id.add_transaction);
        final InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        mAddTranBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expense = mExpense.getText().toString();
                String costString = mCost.getText().toString();
                if (costString.isEmpty() || expense.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter some valid values!", Toast.LENGTH_SHORT).show();
                    return;
                }
                expense = expense.substring(0, 1).toUpperCase() +
                        expense.substring(1, expense.length());

                /*inputManager.hideSoftInputFromWindow(getActivity()
                        .getCurrentFocus()
                        .getWindowToken(),
                         InputMethodManager.HIDE_NOT_ALWAYS);*/
                final double[] cost = {Double.parseDouble(costString)};
                final ContentValues cv = new ContentValues();
                cv.put(LendManagerContract.Transactions.FRIEND_ID, mFriend.getId());
                cv.put(LendManagerContract.Transactions.EXPENSE, expense);
                cv.put(LendManagerContract.Transactions.TIME, new Date().getTime());

                final int[] lendOrBorrow = new int[1];
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                getContext().getContentResolver().
                                        insert(LendManagerContract.Transactions.CONTENT_URI, cv);
                                mExpense.setText("");
                                mCost.setText("");
                                Transaction transaction = new Transaction(0, finalExpense, cost[0], new Date().getTime());
                                mAdapter.add(transaction);
                            }
                        })
                        .show();
            }
        });
        getLoaderManager().restartLoader(FRIEND_TRANSACTIONS_LOADER, null, this).forceLoad();
        return view;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new LendManagerLoader(getContext(), FRIEND_TRANSACTIONS_LOADER, mFriend.getId());
    }



    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mTransactionList = new ArrayList<>();
        while (data.moveToNext()) {
            int id = data.getInt(data.getColumnIndex(LendManagerContract.Transactions.TRANSACTION_ID));
            String expense = data.getString(data.getColumnIndex(LendManagerContract.Transactions.EXPENSE));
            double cost = data.getDouble(data.getColumnIndex(LendManagerContract.Transactions.AMOUNT));
            long date = data.getLong(data.getColumnIndex(LendManagerContract.Transactions.TIME));
            Transaction transaction = new Transaction(id, expense, cost, date);
            mTransactionList.add(transaction);
        }
        mAdapter = new TransactionListAdapter(mTransactionList);
        mTransactionsRv.setAdapter(mAdapter);
    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
