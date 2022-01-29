package model;

import android.util.Log;

import kaaes.spotify.webapi.android.SpotifyApi;

public class APISpotify {
    public static SpotifyApi api = new SpotifyApi();

    public static void setToken(String token){
        api.setAccessToken(token);
    }


}
