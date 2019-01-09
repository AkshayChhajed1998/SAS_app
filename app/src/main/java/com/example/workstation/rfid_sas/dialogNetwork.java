package com.example.workstation.rfid_sas;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class dialogNetwork extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceState)
    {
        AlertDialog.Builder AD = new AlertDialog.Builder(getActivity());
        AD.setMessage("Network Connectivity Not Available").setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                getActivity().finish();
            }
        });

        return AD.create();
    }
}
