package controller;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conuhacks_vi.R;

import model.APISpotify;
import model.PlaylistAdapter;

public class AllPlaylistController extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_playlist);

        ListView playlist_all = findViewById(R.id.playlist_list);
        PlaylistAdapter adapter = new PlaylistAdapter(this, R.layout.playlist_layout, APISpotify.getAllPlaylist());
        playlist_all.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }
}
