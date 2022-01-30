package controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conuhacks_vi.R;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;
import model.APISpotify;
import model.PlaylistAdapter;

public class GeneratePlaylistController extends AppCompatActivity implements View.OnKeyListener {

    private static final int NUMBER_OF_PLAYLIST = 3;

    EditText searchbar;
    List<PlaylistSimple> list;
    PlaylistAdapter adapter;
    ListView playlist_list;
    List<PlaylistSimple> selectedPlaylist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_playlist);

        selectedPlaylist = new ArrayList<PlaylistSimple>();

        searchbar = findViewById(R.id.searchbar);
        searchbar.setOnKeyListener(this);

        list = new ArrayList<PlaylistSimple>();

        list = APISpotify.searchPlaylist("music");
        playlist_list = findViewById(R.id.playlist_list);
        adapter = new PlaylistAdapter(this, R.layout.playlist_layout, list);
        playlist_list.setAdapter(adapter);


        playlist_list.setOnItemClickListener((parent, view, position, id)->
        {
            if(selectedPlaylist.size() < NUMBER_OF_PLAYLIST)
            {
                selectedPlaylist.add((PlaylistSimple) playlist_list.getItemAtPosition(position));
            }
            else {
                APISpotify.generatePlaylist(selectedPlaylist);
                selectedPlaylist.clear();
            }
        });


    adapter.notifyDataSetChanged();
}

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

            if (!event.isShiftPressed()) {
                Log.v("AndroidEnterKeyActivity", "Enter Key Pressed!");
                list = APISpotify.searchPlaylist(searchbar.getText().toString().trim());
                adapter.notifyDataSetChanged();
            }
        }
        return false;
    }
}
