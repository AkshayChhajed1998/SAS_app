package com.example.workstation.rfid_sas;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class analysis extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String domain_name;
    AsyncRequest P ;
    private String mParam1;
    private String mParam2;

    public analysis() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        this.domain_name =this.getResources().getString(R.string.domain_name);
        P = new AsyncRequest("GET",getContext());
        Bundle B=getArguments();
        JSONArray j=new JSONArray();
        try {
            j = new JSONArray(B.getString("displayData"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        boolean is_student = false,is_teacher=false;
        try {
             is_student=(boolean)((JSONObject)((JSONObject)j.get(0)).get("fields")).get("is_student");
             is_teacher=(boolean)((JSONObject)((JSONObject)j.get(0)).get("fields")).get("is_teacher");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(is_student)
        P.setUrl(domain_name+this.getResources().getString(R.string.student_analysis));
        if(is_teacher)
        P.setUrl(domain_name+this.getResources().getString(R.string.teacher_analysis));
        P.start();
        try {
            P.join();
            Log.i("DATA:----", "onCreate: "+P.getResponseBody());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle B=getArguments();
        JSONArray j=new JSONArray();
        try {
            j = new JSONArray(B.getString("displayData"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if((boolean)((JSONObject)((JSONObject)j.get(0)).get("fields")).get("is_student"))
            {
                View v= inflater.inflate(R.layout.fragment_analysis_student, container, false);
                String gs = P.getResponseBody();
                JSONObject J=new JSONObject(gs);
                Log.i("length", "onCreateView: "+J.getString("graph"));
                //String g1 = gs.substring(0,gs.indexOf("$$$"));
                //String g2 = gs.substring(gs.indexOf("$$$")+3,gs.length());
                Log.i("graph 1", "onCreateView: ");
                Log.i("graph 2", "onCreateView: ");
                WebView WV = (WebView) v.findViewById(R.id.GV);
                WebView WV1 = (WebView) v.findViewById(R.id.GV1);
                WV.getSettings().setJavaScriptEnabled(true);
                WV1.getSettings().setJavaScriptEnabled(true);
                WV.loadData(this.getResources().getString(R.string.start_html)+J.getString("graph")+this.getResources().getString(R.string.end_html),"text/html",null);
                WV1.loadData(this.getResources().getString(R.string.start_html)+J.getString("graph1")+this.getResources().getString(R.string.end_html),"text/html",null);
                return v;
            }

            else
            {
                return inflater.inflate(R.layout.fragment_analysis_teacher, container, false);
            }

        } catch (JSONException e) {
            return null;
        }

    }


}
