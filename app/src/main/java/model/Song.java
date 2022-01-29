package model;

import android.media.Image;

import com.example.conuhacks_vi.R;

public class Song {

    public String title;
    public String artist;


    public Song(String artist, String title)
    {
        this.title = title;
        this.artist = artist;
    }

    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }

}
