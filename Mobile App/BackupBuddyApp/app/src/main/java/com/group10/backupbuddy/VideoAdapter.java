package com.group10.backupbuddy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
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

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("delete button: " + holder.test);
//                videoList.get(position);
                int newPosition = holder.getAdapterPosition();

                videoList.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition,videoList.size());

                String url = "http://10.0.2.2/delete.php";
                RequestQueue rq = Volley.newRequestQueue(context);
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("userId", video.getImage());
                parameters.put("id", video.getVideo());

                JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(parameters),

                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
//                                    VolleyLog.v("Response:%n %s", response.toString(4));
                                    System.out.println(response);
                                    if(Integer.parseInt(response.getString("id")) == 0) {
//                                        Toast.makeText(getApplicationContext(), "Incorrect Username or Password", Toast.LENGTH_SHORT).show();

                                    }else {
//                                        Toast.makeText(getApplicationContext(), "Successfully Logged in", Toast.LENGTH_SHORT).show();
//                                        Intent nextView = new Intent(LoginActivity.this, MainActivity.class);
//                                        nextView.putExtra("ID", response.getString("id"));
//                                        startActivity(nextView);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                rq.add(req);


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
