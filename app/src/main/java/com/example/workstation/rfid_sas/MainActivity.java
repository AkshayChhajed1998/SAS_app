package com.example.workstation.rfid_sas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    String domain_name;
    SASCookieStore store;
    String csrf_token=new String();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        domain_name = getResources().getString(R.string.domain_name);
        String FCMToken = FirebaseInstanceId.getInstance().getToken();
        store = new SASCookieStore(this);
        if(isConnected) {
            AsyncRequest P = new AsyncRequest("GET", this);
            P.setUrl(domain_name + this.getResources().getString(R.string.Login));
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
                    Intent dashBoard = new Intent(this, DashBoard.class);
                    this.finish();
                    dashBoard.putExtra("displayData", P.getResponseBody());
                    dashBoard.putExtra("flag",false);
                    Log.i("JSON:::", "onCreate: " + P.getResponseBody());
                    // dashBoard.putExtra("displayData","<html><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>"+P.getResponseBody()+"</html>");
                    store.loadAllCookies();
                    Log.i("VC", "SignIN: " + store.getCookies());
                    startActivity(dashBoard);
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

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    void SignIN(View B) throws MalformedURLException, URISyntaxException, InterruptedException, JSONException {
        Button b = (Button)B;
        EditText usernameView = (EditText) findViewById(R.id.Email);
        EditText passwordView = (EditText) findViewById(R.id.Password);

        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        if(!username.isEmpty() && !password.isEmpty())
        {
            b.setClickable(false);
            AsyncRequest P = new AsyncRequest("POST",this);
            P.setUrl(domain_name+this.getResources().getString(R.string.Login));
            P.setRequestBody("username="+username+"&password="+password+"&csrfmiddlewaretoken="+csrf_token+"&LOGIN=LOGIN");
            P.start();
            P.join();
            JSONArray ResBody = new JSONArray(P.getResponseBody());
            Log.i("JSON:---", "SignIN: "+ResBody);
            if(!((JSONObject)ResBody.get(0)).has("ERROR"))
            {
                Intent dashBoard = new Intent(this, DashBoard.class);
                dashBoard.putExtra("displayData", P.getResponseBody());
                dashBoard.putExtra("flag",false);
                store.loadAllCookies();
                this.finish();
                startActivity(dashBoard);
            }
            else
            {
                Toast.makeText(this,((JSONObject)ResBody.get(0)).getString("ERROR"),Toast.LENGTH_LONG).show();
                usernameView.setText("");
                passwordView.setText("");
                this.recreate();
            }
        }


    }



}
