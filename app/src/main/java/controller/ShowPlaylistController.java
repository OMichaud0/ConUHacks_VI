package controller;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conuhacks_vi.R;

import java.io.InputStream;
import java.net.URL;

import kaaes.spotify.webapi.android.models.Playlist;
import model.SongAdapter2;

public class ShowPlaylistController extends AppCompatActivity {

    Playlist playlist;
    ListView listView;
    SongAdapter2 adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_playlist);

        ImageView cover_playlist = findViewById(R.id.cover_playlist);
        TextView title_playlist = findViewById(R.id.title_playlist);

        if((int) getIntent().getExtras().get("Controller") == 1)
            playlist = AddPlaylistController.playlist;
        else
            playlist = GeneratePlaylistController.playlist;



        title_playlist.setText(playlist.name);
        Drawable d = loadCoverFromWeb(playlist.images.get(0).url);
        if(d != null)
            cover_playlist.setImageDrawable(d);
        else
            cover_playlist.setImageResource(R.drawable.no_image);


        listView = findViewById(R.id.playlist);
        adapter = new SongAdapter2(this, R.layout.song_layout2, playlist.tracks.items);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    public void share(View view){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");

        String name = playlist.name;
        String url = playlist.external_urls.get("spotify");

        share.putExtra(Intent.EXTRA_SUBJECT, name);
        share.putExtra(Intent.EXTRA_TEXT, url);

        startActivity(Intent.createChooser(share, "Share your playlist !"));
    }

    private Drawable loadCoverFromWeb(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}
