package com.group10.backupbuddy;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Handler;
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
import java.util.Timer;
import java.util.TimerTask;

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

        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP2,150);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
            }
        }, 450);



//        Uri uri = Uri.parse("https://i.imgur.com/Imu2Qp0.jpg");
        videoList.add(
                new Video(
                        1,
                        "help", "https://i.imgur.com/Imu2Qp0.jpg","https://test.b-cdn.net/bunny_720p.m4v"

                )
        );


        adapter = new VideoAdapter(this, videoList);
        recyclerView.setAdapter(adapter);
        System.out.println("mewhat");

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2/testing.php";

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
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Login();
            }
        }, 2000);
    }


    public void Login(){

        String url = "http://10.0.2.2/testing.php";
//        url = "https://api.myjson.com/bins/kp9wz";
        RequestQueue rq = Volley.newRequestQueue(this);
        HashMap<String, String> parameters = new HashMap<String, String>();
      //  parameters.put("login", username.getText().toString().trim());
       // parameters.put("password", pass.getText().toString().trim());

        System.out.println("who");
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    System.out.println(jsonArray);
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        String base = "http://10.0.2.2/";
//                        JSONObject employee = jsonArray.getJSONObject(i);
//                        String test = employee.getString("mail");
                        System.out.println("values are here "+jsonArray.get(i));
                        // id title image video
                        String title = jsonArray.getString(i);
                        title =  title.substring(0,title.length()-4);
                        System.out.println("title: "+title);
                        String image = title + ".jpg";
                        image = base + image;
                        System.out.println("image: "+image);
                        String video2 = jsonArray.getString(i);
                        video2 = base+video2;
                        Video video = new Video(i,title,image,video2);
                        videoList.add(video);


                    }
                    adapter = new VideoAdapter(internet.this, videoList);
                    recyclerView.setAdapter(adapter);

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
