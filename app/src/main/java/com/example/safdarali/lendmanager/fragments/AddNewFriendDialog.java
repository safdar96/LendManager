package com.example.safdarali.lendmanager.fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.safdarali.lendmanager.R;
import com.example.safdarali.lendmanager.provider.LendManagerContract;

public class AddNewFriendDialog extends android.support.v4.app.DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.add_new_friend_dialog, null);
        builder.setView(view);

        Button addFriend = view.findViewById(R.id.add_friend);
        final EditText friendNameEditTv = view.findViewById(R.id.friend_name_edit_tv);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = friendNameEditTv.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(getContext(), "Enter some name please!", Toast.LENGTH_SHORT).show();
                } else {
                    name.trim();
                    name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());
                    ContentValues cv = new ContentValues();
                    cv.put(LendManagerContract.Friends.FRIEND_NAME, name);
                    cv.put(LendManagerContract.Friends.AMOUNT, 0);
                    Uri uri = getContext().getContentResolver().insert(LendManagerContract.Friends.CONTENT_URI, cv);
                    if (uri == null) {
                        Toast.makeText(getContext(), "entry failed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), name + " is added", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                }
            }
        });
        Button cancel = view.findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return builder.create();
    }
}
