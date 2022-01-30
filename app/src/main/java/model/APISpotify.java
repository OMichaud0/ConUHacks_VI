package model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.StrictMode;
import android.util.Log;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

public class APISpotify {

    public static SpotifyService spotify;
    public static SpotifyApi api = new SpotifyApi();

    public static void setAccess(String token){
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
        List<String> top = null;
        playlist = mergeSort(playlist);
        for(int i = playlist.size() - 1; i > playlist.size() - 6; i--){
            top.add(playlist.get(i).track.id);
        }
        return top;
    }

    public static List<Track> getRecom(List<PlaylistTrack> playlist){
        List<String> top = topPopularity(playlist);
        Map<String, Object> mapping = new HashMap<>();
        for(String s : top){
            mapping.put("seed_tracks", s);
        }
        return spotify.getRecommendations(mapping).tracks;
    }

    public static String createPlaylist(String userId, String name){
        Boolean isPublic = new Boolean(true);
        Map<String, Object> mapping = new HashMap<>();
        mapping.put(name, isPublic);
        return spotify.createPlaylist(userId, mapping).id;
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

    public static void generatePlaylist(List<PlaylistSimple> listOfPlaylists){
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
        List<PlaylistTrack> mergedList = null;
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
