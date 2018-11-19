package com.group10.backupbuddy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import rx.Producer;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {


    private Context mCtx;
    private List<Video> videoList;
    public View view;

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
        holder.textViewTitle.setText(video.getTitle());
        holder.test = video.getVideo();
//        holder.imageView.setImageURI(video.getImage());


        Glide.with(mCtx)
                .load(video.getImage())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textViewTitle;
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
        }
    }
}
