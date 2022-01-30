package model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.StrictMode;
import android.util.Log;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Followers;

public class APISpotify {

    public static SpotifyService spotify;
    public static SpotifyApi api = new SpotifyApi();

    public static void setToken(String token){
        api.setAccessToken(token);
        spotify = APISpotify.api.getService();
    }

    public static Followers[] getFollowers(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Followers test = spotify.getMe().followers;

        return test.;
    }


}
