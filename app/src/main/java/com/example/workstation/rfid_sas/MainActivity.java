package com.example.workstation.rfid_sas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    String domain_name;
    SASCookieStore store;
    String csrf_token=new String();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        domain_name=getResources().getString(R.string.domain_name);

        store = new SASCookieStore(this);
        AsyncRequest P=new AsyncRequest("GET",this);
        P.setUrl(domain_name+this.getResources().getString(R.string.Login));
        P.start();
        try
        {
            P.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if(P.getResponseBody().substring(1,5).equals("form"))
        {
            csrf_token = P.getResponseBody().substring(66, 66 + 64);
            try {
                store.add(new URI(domain_name), HttpCookie.parse(P.getResponseHeader().get("set-cookie").get(0)).get(0));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Intent dashBoard = new Intent(this,DashBoard.class);
            this.finish();
            dashBoard.putExtra("displayData",P.getResponseBody());
            Log.i("JSON:::", "onCreate: "+P.getResponseBody());
            // dashBoard.putExtra("displayData","<html><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>"+P.getResponseBody()+"</html>");
            store.loadAllCookies();
            Log.i("VC", "SignIN: "+store.getCookies());
            startActivity(dashBoard);
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