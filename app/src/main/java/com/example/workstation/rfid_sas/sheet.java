package com.example.workstation.rfid_sas;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class sheet extends AppCompatActivity {

    SASCookieStore store;
    String domain_name;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);
        domain_name = getResources().getString(R.string.domain_name);
        store = new SASCookieStore(getApplicationContext());
        //CookieManager WVCookieManager = CookieManager.getInstance();
        WebView WV = (WebView) findViewById(R.id.WV);
        //URI uri = null;
        //try {
        //   uri = new URI(domain_name);
        //} catch (URISyntaxException e) {
        //    e.printStackTrace();
        //}
        //for(HttpCookie H:store.get(uri))
        //{WVCookieManager.setCookie("https://attendence-system-akshay-chhajed.c9users.io/teacher/dashboard/sheets",H.getValue());
        //System.out.println(H.getValue());}
        //WVCookieManager.flush();
        WV.getSettings().setJavaScriptEnabled(true);
        //WV.loadUrl("https://attendence-system-akshay-chhajed.c9users.io/teacher/dashboard/sheets");
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //TableLayout T = (TableLayout) findViewById(R.id.TableLayout);
        //TableRow Row = new TableRow(this);
        //Row.setLayoutParams(params);
        //Row.setBackgroundColor(getResources().getColor(R.color.blue_intheme));
        //T.addView(Row);
        //this.addContentView(T,params);

        AsyncRequest P = new AsyncRequest("POST",getApplicationContext());
        P.setUrl("https://attendence-system-akshay-chhajed.c9users.io/attendance/fetch/");
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
        String Dat = SDF.format(new Date());
        P.setRequestBody("date="+Dat+"&Class=1&subject=7");
        P.start();
        try {
            P.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String HTML = "<html><body>"+P.getResponseBody()+"<script></script></body></html>";
        Log.i("table", "onCreate: "+P.getResponseBody());
        WV.loadData(P.getResponseBody(),"text","UTF-8");
    }
}
