package com.example.safdarali.lendmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.safdarali.lendmanager.MainActivity;
import com.example.safdarali.lendmanager.R;
import com.example.safdarali.lendmanager.provider.LendManagerContract;

import java.util.HashSet;

public class DeleteAlertDialogFragment extends android.support.v4.app.DialogFragment {


    private HashSet<Integer> mList;

    public void setSelectedList(HashSet<Integer> list) {
        mList = list;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_delete_alert_dialog, null);
        builder.setView(view);

        Button acceptDelete = view.findViewById(R.id.accept_delete);
        Button declineDelete = view.findViewById(R.id.decline_delete);

        acceptDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                for (Integer x : mList) {
                    int y = getContext().getContentResolver().delete(LendManagerContract.Friends.CONTENT_URI,
                            LendManagerContract.Friends.FRIEND_ID + " = " + x,
                            null);
                }
                Toast.makeText(getContext(), "Friends deleted reopen the app sorry for inconviniece", Toast.LENGTH_SHORT).show();
            }
        });

        declineDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return builder.create();
    }
}
