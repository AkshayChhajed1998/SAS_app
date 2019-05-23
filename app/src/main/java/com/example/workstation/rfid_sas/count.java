package com.example.workstation.rfid_sas;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;

public class count extends AppCompatActivity {

    String csrf_token=new String();
    String domain_name;
    SASCookieStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);
        TextView T = (TextView) findViewById(R.id.TVC);
        T.setText(getIntent().getStringExtra("msg"));
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        domain_name = getResources().getString(R.string.domain_name);
        String FCMToken = FirebaseInstanceId.getInstance().getToken();
        store = new SASCookieStore(getApplicationContext());
        if(isConnected) {
            AsyncRequest P = new AsyncRequest("GET", this);
            P.setUrl(getIntent().getStringExtra("url"));
            P.start();
            ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Loading. Please wait...", true);
            try {
                P.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dialog.cancel();
            if(P.getResponseCode()/100 == 5 || P.getResponseCode()/100==4)
            {
                Toast.makeText(this,"Service Unavailable",Toast.LENGTH_LONG).show();
                DialogFragment D = new dialogServiceUnavailable();
                Bundle B = new Bundle();
                B.putInt("Code",P.getResponseCode());
                B.putString("msg",P.getResponseMsg());
                D.setArguments(B);
                D.show(getFragmentManager(), "ServiceUnavailable");
            }
            else
            {
                if (P.getResponseBody().substring(1, 5).equals("form"))
                {
                    csrf_token = P.getResponseBody().substring(66, 66 + 64);
                    try {
                        store.add(new URI(domain_name), HttpCookie.parse(P.getResponseHeader().get("set-cookie").get(0)).get(0));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this,"Sign IN required!!!",Toast.LENGTH_LONG).show();
                    this.finish();
                }
            }
        }
        else
        {
            Toast.makeText(this,"Internet Connectivity Not Found",Toast.LENGTH_LONG).show();
            DialogFragment d = new dialogNetwork();
            d.show(getFragmentManager(),"NETWORK");
        }
    }

    public void send(View B) throws InterruptedException {
        EditText V = (EditText) findViewById(R.id.EVC);
        AsyncRequest P = new AsyncRequest("POST",getApplicationContext());
        P.setUrl(getIntent().getStringExtra("url"));
        P.setRequestBody("count="+Integer.parseInt(V.getText().toString())+"&csrfmiddlewaretoken="+csrf_token);
        P.start();
        P.join();
        this.finish();
    }
}
