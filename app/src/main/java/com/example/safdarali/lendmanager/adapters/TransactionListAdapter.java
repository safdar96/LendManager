package com.example.safdarali.lendmanager.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.safdarali.lendmanager.R;
import com.example.safdarali.lendmanager.data.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.TransactionItemViewHolder> {


    ArrayList<Transaction> mList;

    public TransactionListAdapter(ArrayList<Transaction> list) {
        mList = list;
    }
    SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMM yyyy");
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

    public void add(Transaction transaction) {
        mList.add(transaction);
        notifyDataSetChanged();
    }


    class TransactionItemViewHolder extends RecyclerView.ViewHolder {


        private final Context mContext;
        TextView mExpense, mCost, mDay, mDate;
        ImageView mLendBorrowIcon, mRupeeIcon;
        public TransactionItemViewHolder(View itemView, Context context) {
            super(itemView);
            mExpense = itemView.findViewById(R.id.expense);
            mCost = itemView.findViewById(R.id.cost);
            mDay = itemView.findViewById(R.id.day);
            mDate = itemView.findViewById(R.id.date);
            mLendBorrowIcon = itemView.findViewById(R.id.lend_borrow_icon);
            mRupeeIcon = itemView.findViewById(R.id.rupee_icon);
            mContext = context;
        }

        public void bind(int pos) {
            Transaction transaction = mList.get(pos);
            mExpense.setText(transaction.getExpense());
            mCost.setText(String.valueOf(transaction.getCost()));

            String[] time = formatter.format(transaction.getDate()).split(",");
            mDay.setText(time[0]);
            mDate.setText(time[1]);

            if (transaction.getCost() < 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mCost.setTextColor(mContext.getResources().getColor(R.color.colorRed, null));
                } else {
                    mCost.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                }
                mCost.setText(String.valueOf(transaction.getCost() * -1.0));
                mRupeeIcon.setBackground(mContext.getDrawable(R.drawable.ic_borrow_rupee_icon));
                mLendBorrowIcon.setBackground(mContext.getDrawable(R.drawable.ic_sub_red_round));
            } else {
                mCost.setText(String.valueOf(transaction.getCost()));
                mLendBorrowIcon.setBackground(mContext.getDrawable(R.drawable.ic_add_green_round));
                mRupeeIcon.setBackground(mContext.getDrawable(R.drawable.ic_lend_rupee_icon));
            }
        }
    }
}
