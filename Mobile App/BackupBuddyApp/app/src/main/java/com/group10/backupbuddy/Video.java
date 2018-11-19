package com.group10.backupbuddy;

import android.net.Uri;

import java.net.URI;

public class Video {
    private int id;
    private String title;;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }
    public String getVideo() {
        return video;
    }

    private String image;
    private String video;

    public Video(int id, String title, String image, String video) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.video = video;
    }
}
