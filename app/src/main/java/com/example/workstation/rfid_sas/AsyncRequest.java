package com.example.workstation.rfid_sas;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class AsyncRequest extends Thread
{
    final String TAG = AsyncRequest.class.getSimpleName();

    final String domain_name;
    SASCookieStore store;

    String ResponseMsg = new String();
    int  ResponseCode;
    String ResponseBody = new String();
    Map<String,List<String>> ResponseHeader;


    String Url = new String();
    String RequestBody = new String();
    final String RequestType;


    AsyncRequest(String requestType,Context context) {
        RequestType = requestType;store=new SASCookieStore(context);
        domain_name=context.getResources().getString(R.string.domain_name);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void run()
    {
        try
        {
            URL url = new URL(Url);
            URI uri = new URI(Url);
            HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
            httpconn.setInstanceFollowRedirects(false);
            HttpsURLConnection.setFollowRedirects(false);
            httpconn.setRequestMethod(RequestType);
            String S="";
            for(HttpCookie H:store.get(new URI(domain_name)))
                S+=H+"; ";
            httpconn.setRequestProperty("Cookie",S);

            if(RequestType=="POST")
            {
                DataOutputStream output=new DataOutputStream(httpconn.getOutputStream());
                output.writeBytes(RequestBody);
                output.flush();
                output.close();
            }

            boolean redirect = false;


            // normally, 3xx is redirect
            int status = httpconn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            System.out.println("Response Code ... " + status);

            if(redirect) {
                // get redirect url from "location" header field
                String newUrl = httpconn.getHeaderField("Location");

                // get the cookie if need, for login
                List<String> cookiesL =httpconn.getHeaderFields().get("set-cookie");
                Log.i(TAG, "run: "+httpconn.getHeaderFields());
                if(cookiesL != null)
                    for(String x:cookiesL)
                        store.add(new URI(domain_name),HttpCookie.parse(x).get(0));

                // open the new connnection again
                url = new URL(domain_name+newUrl);
                uri = new URI(domain_name+newUrl);
                Log.i(TAG, "run: "+url);
                httpconn.disconnect();
                httpconn = (HttpURLConnection) url.openConnection();
                httpconn.setInstanceFollowRedirects(false);
                HttpURLConnection.setFollowRedirects(false);
                httpconn.setRequestMethod("GET");
                S="";
                for(HttpCookie H:store.get(new URI(domain_name)))
                    S+=H+"; ";
                httpconn.setRequestProperty("Cookie",S);
                Log.i(TAG, "CookiesSession--: "+S);


            }

            Log.i(TAG, "run: " + httpconn);

            this.ResponseMsg = httpconn.getResponseMessage();
            this.ResponseCode = httpconn.getResponseCode();
            this.ResponseHeader = httpconn.getHeaderFields();
            byte[] b = new byte[1024 * 1024];
            int len;
            len = (new DataInputStream(httpconn.getInputStream())).read(b);
            Log.i(TAG, "run: "+b.toString());
            this.ResponseBody = new String(b, 0, len);
            httpconn.disconnect();
        }
        catch(IOException e)
        {
            Log.e(TAG, "run: ",e );
        }
        catch (URISyntaxException e)
        {
            Log.e(TAG, "run: ",e );
        }

    }
    void setUrl(String Url)
    {
        this.Url=Url;
    }


    void setRequestBody(String RequestBody)
    {
        this.RequestBody=RequestBody;
    }

    String getResponseMsg()
    {
        return ResponseMsg;
    }

    String getResponseBody()
    {
        return ResponseBody;
    }

    int getResponseCode(){return ResponseCode;}

    Map<String,List<String>> getResponseHeader()
    {
        return ResponseHeader;
    }
}
