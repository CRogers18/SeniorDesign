package com.group10.backupbuddy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import rx.Producer;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {


    private Context mCtx;
    private List<Video> videoList;
    public View view;
    public int curVideo;
    private Context context;


    public VideoAdapter(Context mCtx, List<Video> videoList) {
        this.mCtx = mCtx;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout,null);
        VideoViewHolder holder = new VideoViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        curVideo = position;
        holder.textViewTitle.setText(video.getTitle());
        holder.test = video.getVideo();
//        holder.imageView.setImageURI(video.getImage());


        String test = video.getVideo().toString();
        String test2 = video.getImage().toString();
        String firstPart = test.substring(19, test.length());
        String SecondPart = test2.substring(19,test2.length());

        System.out.println("firstPart = :"+firstPart);
        System.out.println("SecondPart = :" +SecondPart);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("delete button: " + holder.test);
//                videoList.get(position);
                int newPosition = holder.getAdapterPosition();

                videoList.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition,videoList.size());

//                RequestQueue rq = Volley.newRequestQueue(context);
                RequestQueue queue = Volley.newRequestQueue(context);

                String serverUrl = "http://192.168.4.1/begin.php";



                StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("TAG", "response = "+ response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error = "+ error);
                    }
                })
                {


                    @Override
                    public Map<String, String> getHeaders()  {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Accept", "application/json");
                        headers.put("first_name", firstPart);
                        headers.put("last_name", SecondPart);


                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                    ////
                    @Override
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("first_name",video.getVideo().toString());
                        params.put("last_name",video.getImage().toString());
                        return params; //return the parameters
                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);



            }
        });

        Glide.with(mCtx)
                .load(video.getImage())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textViewTitle;
        Button button;
        String test;
        public VideoViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    System.out.println("this is a test: " + test);
                    Intent demo = new Intent(itemView.getContext(), VideoPlayerActivity.class);
                    demo.putExtra("VideoUrl",test);
                    itemView.getContext().startActivity(demo);

                }
            });


            imageView = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            button = itemView.findViewById(R.id.button2);

        }
    }
}
