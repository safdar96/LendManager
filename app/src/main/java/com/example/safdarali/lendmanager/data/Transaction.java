package com.example.safdarali.lendmanager.data;

public class Transaction {
    private int mId;
    private String mExpense;
    private double mCost;
    private long mDate;

    public Transaction(int id, String expense, double cost, long date) {
        mId = id;
        mExpense = expense;
        mCost = cost;
        mDate = date;
    }

    public String getExpense() {
        return mExpense;
    }

    public double getCost() {
        return mCost;
    }

    public long getDate() {
        return mDate;
    }
}
