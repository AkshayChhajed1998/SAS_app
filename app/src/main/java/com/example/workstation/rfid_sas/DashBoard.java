package com.example.workstation.rfid_sas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DashBoard extends AppCompatActivity {


   // boolean flag=true;
    String domain_name;
    static boolean flag = false;
    static Bitmap B;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        this.domain_name =this.getResources().getString(R.string.domain_name);

       // TextView DashBoardHeader = (TextView) findViewById(R.id.DashBoardHeader);
       // RelativeLayout DataLayout = (RelativeLayout) findViewById(R.id.DataLayout);

        Intent DataIntent = getIntent();

        JSONArray obj = new JSONArray();
        try {
            obj = new JSONArray(DataIntent.getStringExtra("displayData"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Log.i("Json", "SignIN: "+((JSONObject)((JSONObject)obj.get(0)).get("fields")).get("username"));
        } catch (JSONException e) {

            e.printStackTrace();
        }
        TextView username = (TextView) findViewById(R.id.username);
        TextView firstname = (TextView) findViewById(R.id.firstname);
        TextView lastname = (TextView) findViewById(R.id.lastname);
        TextView email = (TextView) findViewById(R.id.email);

        BitmapD B=new BitmapD();

        B.setListner(new BitmapD.myListner() {
            @Override
            public void onCompletion(final Bitmap b) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ImageView) findViewById(R.id.ProfilePic)).setImageBitmap(b);
                    }
                });

            }
        });

        try
        {

                username.setText("username : " + ((JSONObject) ((JSONObject) obj.get(0)).get("fields")).get("username"));
                lastname.setText("" + ((JSONObject) ((JSONObject) obj.get(0)).get("fields")).get("last_name"));
                firstname.setText("" + ((JSONObject) ((JSONObject) obj.get(0)).get("fields")).get("first_name"));
                email.setText("Email : " + ((JSONObject) ((JSONObject) obj.get(0)).get("fields")).get("email"));
                B.setUrl(domain_name + "/media/" + ((JSONObject) ((JSONObject) obj.get(1)).get("fields")).get("image"));
                B.start();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*Intent DataIntent = getIntent();
        DataIntent.getStringExtra("displayData");
        WebView WV = (WebView) findViewById(R.id.GV);
        WebView WV1 = (WebView) findViewById(R.id.GV1);
        WV.getSettings().setJavaScriptEnabled(true);
        WV1.getSettings().setJavaScriptEnabled(true);
        WV.loadData(DataIntent.getStringExtra("displayData"),"text/html",null);
        WV1.loadData(DataIntent.getStringExtra("displayData"),"text/html",null);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbardb, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void Close(MenuItem i)
    {
        this.finish();
    }

    public void LogOut(MenuItem i) throws InterruptedException {
        AsyncRequest P=new AsyncRequest("GET",this);
        P.setUrl(domain_name+this.getResources().getString(R.string.Logout));
        P.start();
        P.join();
        this.finish();
    }

}
