package com.example.safdarali.lendmanager.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.ChainHead;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.safdarali.lendmanager.R;
import com.example.safdarali.lendmanager.data.Friend;
import com.example.safdarali.lendmanager.data.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.TransactionItemViewHolder> {


    ArrayList<Transaction> mList;

    HashSet<Transaction> mSelectedTransactions;
    SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMM yyyy");
    TransactionSelectionListener mListener;

    public TransactionListAdapter(ArrayList<Transaction> list, TransactionSelectionListener listener) {
        mList = list;
        mSelectedTransactions = new HashSet<>();
        mListener = listener;
    }

    @NonNull
    @Override
    public TransactionItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_transaction_list, parent, false);
        return new TransactionItemViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionItemViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void add(Transaction mTransaction) {
        mList.add(mTransaction);
        notifyDataSetChanged();
    }

    public void removeTransactions(HashSet<Transaction> selectedTransactions) {
        for (Transaction txn: selectedTransactions) {
            mList.remove(txn);
        }
        notifyDataSetChanged();
        mSelectedTransactions.clear();
    }


    class TransactionItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private final Context mContext;
        TextView mExpense, mCost, mDay, mDate;
        ImageView mLendBorrowIcon, mRupeeIcon;
        CheckBox mCheckbox;
        Transaction mTransaction;

        public TransactionItemViewHolder(View itemView, Context context) {
            super(itemView);
            mExpense = itemView.findViewById(R.id.expense);
            mCost = itemView.findViewById(R.id.cost);
            mDay = itemView.findViewById(R.id.day);
            mDate = itemView.findViewById(R.id.date);
            mLendBorrowIcon = itemView.findViewById(R.id.lend_borrow_icon);
            mRupeeIcon = itemView.findViewById(R.id.rupee_icon);
            mCheckbox = itemView.findViewById(R.id.check_box);
            mContext = context;
            itemView.setOnClickListener(this);
        }

        public void bind(int pos) {
            mTransaction = mList.get(pos);
            mExpense.setText(mTransaction.getExpense());
            mCost.setText(String.valueOf(mTransaction.getCost()));

            if (!mSelectedTransactions.contains(mTransaction)) {
                mCheckbox.setVisibility(View.GONE);
            }
            String[] time = formatter.format(mTransaction.getDate()).split(",");
            mDay.setText(time[0]);
            mDate.setText(time[1]);

            if (mTransaction.getCost() < 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mCost.setTextColor(mContext.getColor(R.color.colorRed));
                } else {
                    mCost.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                }
                mCost.setText(String.valueOf(mTransaction.getCost() * -1.0));
                mRupeeIcon.setBackground(mContext.getDrawable(R.drawable.ic_borrow_rupee_icon));
                mLendBorrowIcon.setBackground(mContext.getDrawable(R.drawable.ic_sub_red_round));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mCost.setTextColor(mContext.getColor(R.color.colorGreen));
                } else {
                    mCost.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                }
                mCost.setText(String.valueOf(mTransaction.getCost()));
                mLendBorrowIcon.setBackground(mContext.getDrawable(R.drawable.ic_add_green_round));
                mRupeeIcon.setBackground(mContext.getDrawable(R.drawable.ic_lend_rupee_icon));
            }

        }

        @Override
        public void onClick(View view) {
            if (!mSelectedTransactions.contains(mTransaction)) {
                mCheckbox.setVisibility(View.VISIBLE);
                mSelectedTransactions.add(mTransaction);
                mListener.onSelectedListChanged(mSelectedTransactions);
            } else {
                mCheckbox.setVisibility(View.GONE);
                mSelectedTransactions.remove(mTransaction);
                mListener.onSelectedListChanged(mSelectedTransactions);
            }
        }

    }

    public interface TransactionSelectionListener {
        void onSelectedListChanged(HashSet<Transaction> selectedTransactions);
    }
}
