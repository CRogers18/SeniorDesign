package com.group10.backupbuddy;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class internet extends AppCompatActivity {


    RecyclerView recyclerView;
    VideoAdapter adapter;
    List<Video> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet);
//        final TextView mTextView = (TextView) findViewById(R.id.textView);

        videoList = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        Uri uri = Uri.parse("https://i.imgur.com/Imu2Qp0.jpg");
        videoList.add(
                new Video(
                        1,
                        "help", "https://i.imgur.com/Imu2Qp0.jpg","https://test.b-cdn.net/bunny_720p.m4v"

                )
        );
        videoList.add(
                new Video(
                        2,
                        "help", "","4"

                )
        );
        videoList.add(
                new Video(
                        2,
                        "help", "","3"

                )
        );
        videoList.add(
                new Video(
                        2,
                        "help", "","2"

                )
        );
        videoList.add(
                new Video(
                        2,
                        "help", "","1"

                )
        );

        adapter = new VideoAdapter(this, videoList);
        recyclerView.setAdapter(adapter);
        System.out.println("mewhat");

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://localhost/testing.php";

//// Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        mTextView.setText("Response is: "+ response.substring(0,500));
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                mTextView.setText("That didn't work!");
//            }
//        });

//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        mTextView.setText("Response: " + response.toString());
//                        System.out.println("SJJSJSJSJJ"+response.toString());
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO: Handle error
//
//                    }
//                });
//        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);


// Add the request to the RequestQueue.
//        queue.add(jsonObjectRequest);
        Login();
    }


    public void Login(){

        String url = "http://127.0.0.1/testing.php";
        url = "https://api.myjson.com/bins/kp9wz";
        RequestQueue rq = Volley.newRequestQueue(this);
        HashMap<String, String> parameters = new HashMap<String, String>();
      //  parameters.put("login", username.getText().toString().trim());
       // parameters.put("password", pass.getText().toString().trim());

//        System.out.println("who");
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("employees");
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject employee = jsonArray.getJSONObject(i);
                        String test = employee.getString("mail");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
//        System.out.print("who");

//                response -> {
//                    try {
//                        System.out.println("what: "+response);
//                        //VolleyLog.v("Response:%n %s", response.toString(4));
//                        if(Integer.parseInt(response.getString("id")) == 0) {
//                            Toast.makeText(getApplicationContext(), "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
//
//                        }else {
//                            Toast.makeText(getApplicationContext(), "Successfully Logged in", Toast.LENGTH_SHORT).show();
////                                Intent nextView = new Intent(LoginActivity.this, MainActivity.class);
////                                nextView.putExtra("ID", response.getString("id"));
////                                startActivity(nextView);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });

        rq.add(req);


    }
}
