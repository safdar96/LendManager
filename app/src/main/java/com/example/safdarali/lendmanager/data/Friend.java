package com.example.safdarali.lendmanager.data;

public class Friend {
    private int mId;
    private String mName;
    private double mAmount;

    public String getName() {
        return mName;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setAmount(double amount) {
        mAmount = amount;
    }

    public int getId() {
        return mId;
    }
}
