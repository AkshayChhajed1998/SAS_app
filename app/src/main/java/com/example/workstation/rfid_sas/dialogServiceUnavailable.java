package com.example.workstation.rfid_sas;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class dialogServiceUnavailable extends DialogFragment
{
    String msg;
    int Code;
    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceState)
    {
        AlertDialog.Builder AD = new AlertDialog.Builder(getActivity());
        AD.setMessage("Response Code:-"+getArguments().getInt("Code")+"\nResponse Message:-"+getArguments().getString("msg")).setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                getActivity().finish();
            }
        });

        return AD.create();
    }
}
