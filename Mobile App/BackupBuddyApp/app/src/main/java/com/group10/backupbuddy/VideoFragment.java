package com.group10.backupbuddy;


import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 *
 * in this activity
 * Intent intent = new Intent(getBaseContext(), SignoutActivity.class);
 intent.putExtra("EXTRA_SESSION_ID", sessionId);
 startActivity(intent);

 from the new activity
 String sessionId= getIntent().getStringExtra("EXTRA_SESSION_ID");

 */
public class VideoFragment extends Fragment {


    RecyclerView recyclerView;
    VideoAdapter adapter;
    List<Video> videoList;


    //will be based on click to change to which url is used
    String videoUrl = "https://test.b-cdn.net/bunny_720p.m4v";


    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        videoList = new ArrayList<>();
        recyclerView = (RecyclerView)getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new VideoAdapter(getContext(), videoList);
        recyclerView.setAdapter(adapter);
        System.out.println("mewhat");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Login();
            }
        }, 500);
    }


    public void Login(){

//        String url = "http://10.0.2.2/testing.php";
        // This bottom url is for pi
        String url = "http://192.168.4.1/testing.php";
//        url = "https://api.myjson.com/bins/kp9wz";
        RequestQueue rq = Volley.newRequestQueue(getContext());
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
                        // this url is for pi
                        String base = "http://192.168.4.1/";
//                        String base = "http://10.0.2.2/";
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
                    adapter = new VideoAdapter(getContext(), videoList);
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    private void GetVideos(){
//        String url = "http://passwords.aisoftworks.net/fetchAll.php";
//        RequestQueue rq = Volley.newRequestQueue(this);
//        HashMap<String, String> parameters = new HashMap<String, String>();
//        Intent temp = getIntent(); // gets the previously created intent
//        String idString = temp.getStringExtra("ID");//retrieves ID passed from login activity
//        parameters.put("id", idString);
    }

}
