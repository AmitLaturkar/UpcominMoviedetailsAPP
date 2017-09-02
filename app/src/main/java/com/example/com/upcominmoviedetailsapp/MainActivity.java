package com.example.com.upcominmoviedetailsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    AdapterBoxoffice adapterBoxoffice;
    RecyclerView movielistview;
    ArrayList<Movielistbean> arrayList = new ArrayList<>();
    String url="https://api.themoviedb.org/3/movie/upcoming?api_key=b7cd3340a794e5a2f35e3abb820b497f";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendrequest();

    }

 public void sendrequest(){
     final ProgressDialog pDialog = new ProgressDialog(this);
     pDialog.setMessage("Loading...");
     pDialog.show();

     JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
             url, null,
             new Response.Listener<JSONObject>() {

                 @Override
                 public void onResponse(JSONObject response) {
                     try {
                         JSONArray jsonArray = response.getJSONArray("results");
                         for(int i=0;i<jsonArray.length();i++)
                         {
                             JSONObject jsonObject = jsonArray.getJSONObject(i);
                             String title = jsonObject.getString("title");
                             String poster_path = jsonObject.getString("poster_path");
                             String release_date = jsonObject.getString("release_date");
                             String adult = jsonObject.getString("adult");
                             String id = jsonObject.getString("id");

                             Movielistbean movielistbean = new Movielistbean();
                             movielistbean.setTitle(title);
                             movielistbean.setPoster_path(poster_path);
                             movielistbean.setRelease_date(release_date);
                             movielistbean.setAdult(adult);
                             movielistbean.setId(id);

                             arrayList.add(movielistbean);

                         }
                         Log.d("Response", response.toString());
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                     pDialog.hide();
                     movielistview = (RecyclerView)findViewById(R.id.movielistview);
                     adapterBoxoffice = new AdapterBoxoffice(getApplicationContext(),arrayList);
                     movielistview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                     movielistview.setAdapter(adapterBoxoffice);

                     adapterBoxoffice.setOnItemClickListener(new AdapterBoxoffice.MyClickListener() {
                         @Override
                         public void onItemClick(int position, View v) {
                             //arrayList.get(position).getTitle();
                             //Toast.makeText(getApplicationContext(),arrayList.get(position).getTitle(),Toast.LENGTH_LONG).show();
                             startActivity(new Intent(getApplicationContext(),MovieDetailsActivity.class).putExtra("MovieID",arrayList.get(position).getId()));
                         }
                     });

                 }
             }, new Response.ErrorListener() {

         @Override
         public void onErrorResponse(VolleyError error) {
             VolleyLog.d("Response", "Error: " + error.getMessage());
             // hide the progress dialog
             pDialog.hide();
         }
     });

// Adding request to request queue
     AppController.getInstance().addToRequestQueue(jsonObjReq);

 }


}
