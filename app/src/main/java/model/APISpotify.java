package model;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.StrictMode;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.PlaylistsPager;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.client.Response;

public class APISpotify {

    public static SpotifyService spotify;
    public static SpotifyApi api = new SpotifyApi();
    public static String mtoken;

    public static void setAccess(String token){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mtoken = token;
        api.setAccessToken(token);
        spotify = APISpotify.api.getService();
    }

    public static List<PlaylistSimple> searchPlaylist(String name){
        return spotify.searchPlaylists(name).playlists.items;
    }

    public static List<PlaylistTrack> getTracks(String userId, String playlistId){
        return spotify.getPlaylistTracks(userId, playlistId).items;
    }

    public static List<String> topPopularity(List<PlaylistTrack> playlist){
        List<String> top = new ArrayList<>();
        playlist = mergeSort(playlist);
        for(int i = playlist.size() - 1; i > playlist.size() - 6; i--){
            top.add(playlist.get(i).track.id);
        }
        return top;
    }

    public static List<Track> getRecom(List<PlaylistTrack> playlist){
        List<String> top = topPopularity(playlist);
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("seed_tracks", top.get(0) + "," + top.get(1) + "," + top.get(2));
        mapping.put("limit", 20);
        mapping.put("market", "CA");
        return spotify.getRecommendations(mapping).tracks;
    }

    public static Playlist createPlaylist(String userId, String name){
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("name", name);
        mapping.put("public", "true");
        mapping.put("collaborative", "false");
        mapping.put("description", "");
        return spotify.createPlaylist(userId, mapping);
    }

    public static void fillPlaylist(String userId, String listId, List<Track> playlist){
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> body = new HashMap<>();
        for(Track t : playlist){
            query.put(t.uri, null);
            body.put(t.uri, null);
        }

        spotify.addTracksToPlaylist(userId, listId, query, body);
    }

    public static Playlist generatePlaylist(List<PlaylistSimple> listOfPlaylists){
        Playlist list = createPlaylist(spotify.getMe().id, "Beeeeesss In The Breeeeze");

        List<Track> recom1 = getRecom(getTracks(listOfPlaylists.get(0).owner.id, listOfPlaylists.get(0).id));
        List<Track> recom2 = getRecom(getTracks(listOfPlaylists.get(0).owner.id, listOfPlaylists.get(0).id));
        List<Track> recom3 = getRecom(getTracks(listOfPlaylists.get(0).owner.id, listOfPlaylists.get(0).id));

        Map<String, Object> query = new HashMap<>();
        Map<String, Object> body = new HashMap<>();


        for(int i = 0; i < recom1.size() && i < 20; i++){
            query.put("uris", recom1.get(i).uri + "," + recom2.get(i).uri + "," + recom3.get(i).uri);;
            spotify.addTracksToPlaylist(spotify.getMe().id, list.id, query, body);
        }

        return list;
    }

    public static String getName(){
        return spotify.getMe().display_name;
    }

    public static String getProfilePicture(){
        if(spotify.getMe().images.size() > 0)
            return spotify.getMe().images.get(0).url;
        else
            return "https://us.123rf.com/450wm/urfandadashov/urfandadashov1805/urfandadashov180500070/100957966-photo-not-available-icon-isolated-on-white-background-vector-illustration.jpg?ver=6";
    }

    public static List<PlaylistTrack> getTop50(){
        return spotify.getPlaylistTracks("spotify", "37i9dQZEVXbMDoHDwVN2tF").items.subList(0,15);
    }

    public static List<PlaylistTrack> getNewRelease(){
        return spotify.getPlaylistTracks("spotify", "37i9dQZF1DX5DfG8gQdC3F").items.subList(0,15);
    }

    public static List<Track> getUserFavorites(){
        return spotify.getTopTracks().items;
    }

    public static List<PlaylistSimple> getAllPlaylist(){
        return spotify.getPlaylists(spotify.getMe().id).items;
    }


    public static List<PlaylistTrack> mergeSort(List<PlaylistTrack> listE){
        if(listE.size() == 1){
            return listE;
        }else{
            int mid = (listE.size()) / 2;
            List<PlaylistTrack> list1 = listE.subList(0, mid);
            List<PlaylistTrack> list2 = listE.subList(mid, listE.size());
            list1 = mergeSort(list1);
            list2 = mergeSort(list2);
            return merge(list1, list2);
        }
    }

    public static List<PlaylistTrack> merge(List<PlaylistTrack> list1, List<PlaylistTrack> list2){
        int c1 = 0;
        int c2 = 0;
        List<PlaylistTrack> mergedList = new ArrayList<>();
        while(c1 < list1.size() || c2 < list2.size()){
            if(c1 == list1.size()){
                mergedList.add(list2.get(c2));
                c2++;
            }else if(c2 == list2.size()){
                mergedList.add(list1.get(c1));
                c1++;
            }else if(list1.get(c1).track.popularity.compareTo(list2.get(c2).track.popularity) >= 0){
                mergedList.add(list2.get(c2));
                c2++;
            }else if(list1.get(c1).track.popularity.compareTo(list2.get(c2).track.popularity) < 0){
                mergedList.add(list1.get(c1));
                c1++;
            }else{
                mergedList.add(list1.get(c1));
                c1++;
            }
        }
        return mergedList;
    }


}
