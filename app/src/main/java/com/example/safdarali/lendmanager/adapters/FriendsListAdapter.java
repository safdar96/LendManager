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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.safdarali.lendmanager.R;
import com.example.safdarali.lendmanager.data.Friend;

import java.util.ArrayList;
import java.util.HashSet;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendsListViewHolder> {

    ArrayList<Friend> mList;

    private FriendItemClickListener mListener;

    HashSet<Integer> mSelectedFriendsId;
    public void setOnFriendItemClickListener(FriendItemClickListener listener) {
        mListener = listener;
        mSelectedFriendsId = new HashSet<>();
    }

    @NonNull
    @Override
    public FriendsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_users_list, parent, false);
        return new FriendsListViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsListViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setList(ArrayList<Friend> list) {
        mList = list;
    }

    public HashSet<Integer> getHashSet() {
        return mSelectedFriendsId;
    }

    class FriendsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mName, mAmount;
        ImageView mAlphabetIcon, mRupeeIcon, mLendBorrowIcon;
        Context mContext;
        View mView;
        Friend mFriend;

        public FriendsListViewHolder(View itemView, Context context) {
            super(itemView);
            mView = itemView;
            mName = mView.findViewById(R.id.user_name);
            mAmount = mView.findViewById(R.id.amount);
            mAlphabetIcon = mView.findViewById(R.id.alphabet_icon);
            mRupeeIcon = mView.findViewById(R.id.rupee_icon);
            mLendBorrowIcon = mView.findViewById(R.id.lend_borrow_icon);
            mContext = context;
        }

        public void bind(int pos) {
            mFriend = mList.get(pos);
            mView.setTag(mFriend.getId());
            mName.setText(mFriend.getName());
            if (mFriend.getAmount() == 0) {
                mLendBorrowIcon.setVisibility(View.GONE);
                mRupeeIcon.setBackground(mContext.getDrawable(R.drawable.ic_lend_rupee_icon));
            } else if (mFriend.getAmount() < 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mAmount.setTextColor(mContext.getResources().getColor(R.color.colorRed, null));
                } else {
                    mAmount.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                }
                mAmount.setText(String.valueOf(mFriend.getAmount() * -1.0));
                mRupeeIcon.setBackground(mContext.getDrawable(R.drawable.ic_borrow_rupee_icon));
                mLendBorrowIcon.setBackground(mContext.getDrawable(R.drawable.ic_sub_red_round));
            } else {
                mAmount.setText(String.valueOf(mFriend.getAmount()));
                mLendBorrowIcon.setBackground(mContext.getDrawable(R.drawable.ic_add_green_round));
                mRupeeIcon.setBackground(mContext.getDrawable(R.drawable.ic_lend_rupee_icon));
            }
            ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
            int color = colorGenerator.getColor(mFriend.getName() + "" +mFriend.getId());
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(mFriend.getName().substring(0,1), color);
            mAlphabetIcon.setBackground(drawable);

            mAlphabetIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mSelectedFriendsId.contains(mFriend.getId())) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mView.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed, null));
                        } else {
                            mView.setBackgroundColor(mContext.getResources().getColor(R.color.browser_actions_bg_grey));
                        }
                        mView.setOnClickListener(null);
                        mSelectedFriendsId.add(new Integer(mFriend.getId()));
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mView.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed, null));
                        } else {
                            mView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackground));
                        }
                        mView.setOnClickListener(FriendsListViewHolder.this);
                        mSelectedFriendsId.remove(new Integer(mFriend.getId()));
                    }
                    if (mSelectedFriendsId.size() == 0) {
                        mListener.onFriendSelectItem(false);
                    } else {
                        mListener.onFriendSelectItem(true);
                    }
                }
            });
            mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onFriendItemClick(mFriend);
        }
    }

    public interface FriendItemClickListener {
        public void onFriendItemClick(Friend friend);
        public void onFriendSelectItem(boolean isSomeoneSelected);
    }
}
