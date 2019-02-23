package com.example.safdarali.lendmanager.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendsListViewHolder> {

    ArrayList<Friend> mList;

    private FriendItemClickListener mListener;

    HashSet<Friend> mSelectedFriends;

    public void setOnFriendItemClickListener(FriendItemClickListener listener) {
        mListener = listener;
        mSelectedFriends = new HashSet<>();
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

    public void setList(ArrayList<Friend> list, int sortingBy) {
        mList = list;
        sortFriends(sortingBy);
    }

    public HashSet<Friend> getSelectedFriendsIds() {
        return mSelectedFriends;
    }

    public void removeFriends(HashSet<Friend> friendSet) {
        for (Friend friend : friendSet) {
            mList.remove(friend);
        }
        notifyDataSetChanged();
        mSelectedFriends.clear();
    }

    public void addFriend(Friend friend) {
        mList.add(friend);
        notifyDataSetChanged();
    }

    public void sortFriends(int i) {
        switch (i) {
            case 0:
                Collections.sort(mList, new Comparator<Friend>() {
                    @Override
                    public int compare(Friend f1, Friend f2) {
                        if (f1.getId() == f2.getId()) return 0;
                        else if (f1.getId() > f2.getId()) return 1;
                        else return -1;
                    }
                });
                notifyDataSetChanged();
                return;
            case 1:
                Collections.sort(mList, new Comparator<Friend>() {
                    @Override
                    public int compare(Friend f1, Friend f2) {
                        if (f1.getAmount() == f2.getAmount()) return 0;
                        else if (f1.getAmount() > f2.getAmount()) return -1;
                        else return 1;
                    }
                });
                notifyDataSetChanged();
                return;
            case 2:
                Collections.sort(mList, new Comparator<Friend>() {
                    @Override
                    public int compare(Friend f1, Friend f2) {
                        return f1.getName().compareTo(f2.getName());
                    }
                });
                notifyDataSetChanged();
                return;
        }
    }

    class FriendsListViewHolder extends RecyclerView.ViewHolder {

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
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSelectedFriends.size() == 0) mListener.onFriendItemClick(mFriend);
                    else {
                        selectOrDeselectFriend();
                    }
                }
            });
        }

        public void bind(int pos) {
            mFriend = mList.get(pos);
            mView.setTag(mFriend.getId());
            mName.setText(mFriend.getName());
            if (!mSelectedFriends.contains(mFriend)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackground, null));
                } else {
                    mView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackground));
                }
            }
            if (mFriend.getAmount() == 0) {
                mLendBorrowIcon.setVisibility(View.GONE);
                mRupeeIcon.setBackground(mContext.getDrawable(R.drawable.ic_lend_rupee_icon));
                mAmount.setText("0");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mAmount.setTextColor(mContext.getColor(R.color.colorGreen));
                } else {
                    mAmount.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                }
            } else if (mFriend.getAmount() < 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mAmount.setTextColor(mContext.getColor(R.color.colorRed));
                } else {
                    mAmount.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                }
                mLendBorrowIcon.setVisibility(View.VISIBLE);
                mAmount.setText(String.valueOf(mFriend.getAmount() * -1.0));
                mRupeeIcon.setBackground(mContext.getDrawable(R.drawable.ic_borrow_rupee_icon));
                mLendBorrowIcon.setBackground(mContext.getDrawable(R.drawable.ic_sub_red_round));
            } else {
                mAmount.setText(String.valueOf(mFriend.getAmount()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mAmount.setTextColor(mContext.getColor(R.color.colorGreen));
                } else {
                    mAmount.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                }
                mLendBorrowIcon.setVisibility(View.VISIBLE);
                mLendBorrowIcon.setBackground(mContext.getDrawable(R.drawable.ic_add_green_round));
                mRupeeIcon.setBackground(mContext.getDrawable(R.drawable.ic_lend_rupee_icon));
            }
            ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
            int color = colorGenerator.getColor(mFriend.getName() + "" + mFriend.getId());
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(mFriend.getName().substring(0, 1), color);
            mAlphabetIcon.setBackground(drawable);

            mAlphabetIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectOrDeselectFriend();
                }
            });
        }

        private void selectOrDeselectFriend() {
            if (!mSelectedFriends.contains(mFriend)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mView.setBackgroundColor(mContext.getResources().getColor(R.color.color_selection, null));
                } else {
                    mView.setBackgroundColor(mContext.getResources().getColor(R.color.browser_actions_bg_grey));
                }
                mSelectedFriends.add(mFriend);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackground, null));
                } else {
                    mView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackground));
                }
                mSelectedFriends.remove(mFriend);
            }
            if (mSelectedFriends.size() == 0) {
                mListener.onFriendSelectItem(false);
            } else {
                mListener.onFriendSelectItem(true);
            }
        }
    }

    public interface FriendItemClickListener {
        void onFriendItemClick(Friend friend);

        void onFriendSelectItem(boolean isSomeoneSelected);
    }
}
