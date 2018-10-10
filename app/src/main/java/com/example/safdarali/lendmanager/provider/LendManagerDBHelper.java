package com.example.safdarali.lendmanager.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.safdarali.lendmanager.data.Transaction;

import static com.example.safdarali.lendmanager.provider.LendManagerContract.*;
import static com.example.safdarali.lendmanager.provider.LendManagerContract.Transactions.*;
import static com.example.safdarali.lendmanager.provider.LendManagerContract.Friends.*;

public class LendManagerDBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "lendManager.db";

    private static final int VERSION = 5;

    public LendManagerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE1 = "CREATE TABLE " + MyAccount.MY_ACCOUNT_TABLE_NAME + " (" +
                LendManagerContract.MyAccount.USER_NAME + " VARCHAR(40) NOT NULL, " +
                MyAccount.USER_BALANCE + " REAL " +
                ")";
        final String CREATE_TABLE2 = "CREATE TABLE " + FRIENDS_TABLE_NAME + " (" +
                Friends.FRIEND_ID + " INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT, " +
                Friends.FRIEND_NAME + " VARCHAR(40) NOT NULL, " +
                Friends.AMOUNT + " REAL, " +
                Friends.CURRENT_TRANSACTION_NO + " REAL " +
                ")";

        final String CREATE_TABLE3 = "CREATE TABLE " + TRANSACTIONS_TABLE_NAME + "(" +
                Transactions.TRANSACTION_ID + " INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT, " +
                Transactions.FRIEND_ID + " INTEGER, " +
                Transactions.CURRENT_TRANSACTION_NO + " REAL, " +
                Transactions.EXPENSE + " TEXT, " +
                Transactions.AMOUNT + " REAL NOT NULL, " +
                Transactions.TIME + " INTEGER NOT NULL" +
                ")";

        sqLiteDatabase.execSQL(CREATE_TABLE1);
        sqLiteDatabase.execSQL(CREATE_TABLE2);
        sqLiteDatabase.execSQL(CREATE_TABLE3);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyAccount.MY_ACCOUNT_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Friends.FRIENDS_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Transactions.TRANSACTIONS_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
