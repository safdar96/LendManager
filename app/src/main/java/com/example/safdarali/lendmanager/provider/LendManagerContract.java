package com.example.safdarali.lendmanager.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class LendManagerContract {

    public static final String AUTHORITY = "com.example.safdarali.lendmanager";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MY_ACCOUNT = "MyAccount";
    public static final String PATH_Friends = "Friends";
    public static final String PATH_TRANSACTIONS = "Transactions";

    public static class MyAccount implements BaseColumns {
        public static final String MY_ACCOUNT_TABLE_NAME = "MyAccount";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_ACCOUNT).build();

        public static final String USER_NAME = "userName";
        public static final String USER_BALANCE = "userBalance";
    }

    public static final class Friends implements BaseColumns {
        public static final String FRIENDS_TABLE_NAME = "Friends";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_Friends).build();

        public static final String FRIEND_ID = "friendId";
        public static final String FRIEND_NAME = "friendName";
        public static final String AMOUNT = "amount";
        public static final String CURRENT_TRANSACTION_NO = "currentTransactionNo";
    }

    public static final class Transactions implements BaseColumns {
        public static final String TRANSACTIONS_TABLE_NAME = "Transactions";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTIONS).build();

        public static final String TRANSACTION_ID = "transactionID";
        public static final String FRIEND_ID = "friendId";
        public static final String CURRENT_TRANSACTION_NO = "currentTransactionNo";
        public static final String EXPENSE = "expense";
        public static final String AMOUNT = "amount";
        public static final String TIME = "time";
    }
}
