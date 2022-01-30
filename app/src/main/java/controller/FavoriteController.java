package controller;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conuhacks_vi.R;

import kaaes.spotify.webapi.android.models.PlaylistTrack;
import model.APISpotify;

import model.SongAdapter3;

public class FavoriteController extends AppCompatActivity {

    ListView listView;
    SongAdapter3 adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite);


        listView = findViewById(R.id.list_favorite);
        adapter = new SongAdapter3(this, R.layout.song_layout2, APISpotify.getUserFavorites());
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }
}
