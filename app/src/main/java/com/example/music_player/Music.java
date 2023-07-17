package com.example.music_player;


import android.net.Uri;

import java.io.File;

public class Music extends File {
    private final Uri uri;
    private final String title;
    private int resourceId;

    Music(Uri uri, String title, int resourceId) {
        super(uri.toString());
        this.uri = uri;
        this.title = title;
        this.resourceId = resourceId;
    }

    public Uri getUri() {
        return uri;
    }
    public String getTitle() {
        return title;
    }
    public int getResourceId() { return resourceId; }
}
