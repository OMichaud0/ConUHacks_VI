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

public class APISpotify {

    /*
    Any http call made through the SpotifyService interface that requires a map uses the following
    logic. The input string of the map needs to be one of the fields of the query described for the
    desired action here: https://developer.spotify.com/documentation/web-api/reference/#/ . The
    object that the String needs to map to is should be of the type also described in the query
    section of the link provided above. Optional query fields can be omitted without error, but some
    fields may be mandatory.
     */
    public static SpotifyService spotify;
    public static SpotifyApi api = new SpotifyApi();
    public static String mtoken;

    /*
    Upon receiving the authentification token from Spotify after login, this will store it into the
    SpotifyService from the wrapper, allowing it to do http calls requiring a token without sending
    the token everytime.
    */
    public static void setAccess(String token){
        /*
        Enabling this Strictmode allows to do http calls through the main thread. However this was
        only a temporary fix for the competition. Further development would require to develop
        multithreading as http calls greatly slow down the main thread causing frame losses.
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mtoken = token;
        api.setAccessToken(token);
        spotify = APISpotify.api.getService();
        Log.d("", peepo.testUser(spotify.getMe().id));
    }

    /*
    This method fetches playlists linked to the String that was written in the search bar. Note that
    this search engine is not as performant as the one in Spotify's app. Hence, more precise
    research maybe be required to obtain the desired search results.
     */
    public static List<PlaylistSimple> searchPlaylist(String name){
        return spotify.searchPlaylists(name).playlists.items;
    }

    /*
    This method will fetch a user's playlist through the wrapper and convert the returned pager into
     a list of the tracks for easier manipulation.
     */
    public static List<PlaylistTrack> getTracks(String userId, String playlistId){
        return spotify.getPlaylistTracks(userId, playlistId).items;
    }

    /*
    topPopularity needs a list of playlist tracks (which implements Track from the wrapper). It will
     mergesort the list based on their popularity rating (higher = better) and will return the five
     most popular tracks.
     */
    public static List<String> topPopularity(List<PlaylistTrack> playlist){
        List<String> top = new ArrayList<>();
        playlist = mergeSort(playlist);
        for(int i = playlist.size() - 1; i > playlist.size() - 6 && i >= 0; i--){
            top.add(playlist.get(i).track.id);
        }
        return top;
    }

    /*
    Using the topPopularity method, it uses the five most popular songs returned to fetch what
    Spotify would recommend based on those 5 songs (the API call cannot accept more than five
    different songs as an input for recommendations). The getRecommendations from the wrapper
    returns a Recommendations object from which we get a list of 20 tracks (the number is set in
    the http call).
     */
    public static List<Track> getRecom(List<PlaylistTrack> playlist){
        List<String> top = topPopularity(playlist);
        Map<String, Object> mapping = new HashMap<>();
        if(playlist.size() >= 3){
            mapping.put("seed_tracks", top.get(0) + "," + top.get(1) + "," + top.get(2));
        }else if(playlist.size() >= 2){
            mapping.put("seed_tracks", top.get(0) + "," + top.get(1));
        }else{
            mapping.put("seed_tracks", top.get(0));
        }

        mapping.put("limit", 20);
        mapping.put("market", "CA");
        return spotify.getRecommendations(mapping).tracks;
    }

    /*
    Creates a playlist on the user's account with the provided name. The API does not return any
    error if the name already exists, there will simply be 2 playlists with the same name. The query
    fields "public", "collaborative" and "description" are currently set to the default settings,
    but could easily be implemented. This return the Playlist object that was just created if
    further manipulation is required.
     */
    public static Playlist createPlaylist(String userId, String name){
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("name", name);
        mapping.put("public", "true");
        mapping.put("collaborative", "false");
        mapping.put("description", "");
        return spotify.createPlaylist(userId, mapping);
    }

    /*
    Using the 3 playlists selected by the user, recommendations from all three playlists using
    getRecom() and adds the 20 recommendations from each playlists to the generated playlist by
    alternating between the three  for a total of 60 recommended songs. It returns the updated
    Playlist object in case further manipulation is needed.
     */
    public static Playlist generatePlaylist(List<PlaylistSimple> listOfPlaylists){
        Playlist list = createPlaylist(spotify.getMe().id, "Beeeeesss In The Breeeeze");

        List<Track> recom1 = getRecom(getTracks(listOfPlaylists.get(0).owner.id, listOfPlaylists.get(0).id));
        List<Track> recom2 = getRecom(getTracks(listOfPlaylists.get(1).owner.id, listOfPlaylists.get(1).id));
        List<Track> recom3 = getRecom(getTracks(listOfPlaylists.get(2).owner.id, listOfPlaylists.get(2).id));

        Map<String, Object> query = new HashMap<>();
        Map<String, Object> body = new HashMap<>();


        for(int i = 0; i < recom1.size() && i < 20; i++){
            query.put("uris", recom1.get(i).uri + "," + recom2.get(i).uri + "," + recom3.get(i).uri);;
            spotify.addTracksToPlaylist(spotify.getMe().id, list.id, query, body);
        }

        list = spotify.getPlaylist(list.owner.id, list.id);

        return list;
    }

    // Returns the name of the current user.
    public static String getName(){
        return spotify.getMe().display_name;
    }

    // Returns the profile picture of the current user if it exists, other return a generic image.
    public static String getProfilePicture(){
        if(spotify.getMe().images.size() > 0)
            return spotify.getMe().images.get(0).url;
        else
            return "https://us.123rf.com/450wm/urfandadashov/urfandadashov1805/urfandadashov180500070/100957966-photo-not-available-icon-isolated-on-white-background-vector-illustration.jpg?ver=6";
    }

    // Returns the "Top 50 - Global" playlist from Spotify. It has a static user and id.
    public static List<PlaylistTrack> getTop50(){
        return spotify.getPlaylistTracks("spotify", "37i9dQZEVXbMDoHDwVN2tF").items.subList(0,15);
    }

    /* Returns the "New Music Friday Canada" playlist which contains the most recent releases in
    Canada. It has a static user and id.
     */
    public static List<PlaylistTrack> getNewRelease(){
        return spotify.getPlaylistTracks("spotify", "37i9dQZF1DX5DfG8gQdC3F").items.subList(0,15);
    }

    // Returns the list of the user's favorite songs obtained from the pager returned by the wrapper.
    public static List<Track> getUserFavorites(){
        return spotify.getTopTracks().items;
    }

    // Returns a list of all the user's playlists.
    public static List<PlaylistSimple> getAllPlaylist(){
        return spotify.getPlaylists(spotify.getMe().id).items;
    }

    /* Creates a new playlist to which are added all the songs from the first playlist and from the
    second playlist.
     */
    public static Playlist concatenate(List<PlaylistSimple> listOfPlaylists){
        Playlist list = createPlaylist(spotify.getMe().id, "Concatenated");
        List<PlaylistTrack> list1 = getTracks(listOfPlaylists.get(0).owner.id, listOfPlaylists.get(0).id);
        List<PlaylistTrack> list2 = getTracks(listOfPlaylists.get(1).owner.id, listOfPlaylists.get(1).id);

        Map<String, Object> query = new HashMap<>();
        Map<String, Object> body = new HashMap<>();


        for(PlaylistTrack t : list1){
            query.put("uris", t.track.uri);
            spotify.addTracksToPlaylist(spotify.getMe().id, list.id, query, body);
        }
        for(PlaylistTrack t : list2){
            query.put("uris", t.track.uri);
            spotify.addTracksToPlaylist(spotify.getMe().id, list.id, query, body);
        }

        list = spotify.getPlaylist(list.owner.id, list.id);

        return list;
    }

    // The popularity based mergesort used by topPopularity()
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
