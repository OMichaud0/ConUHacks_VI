package controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conuhacks_vi.R;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import model.Song;
import model.SongAdapter;
import model.APISpotify;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivityController extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://localhost:8888/callback";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ArrayList<Song> viewHot = new ArrayList<Song>();
        ArrayList<Song> viewNew = new ArrayList<Song>();
        for(int i = 0; i < 8; i++)
        {
            viewHot.add(new Song("Artist " + i, "Title "+ i));
            viewNew.add(new Song("New Artist "+i, "New Song "+i));
        }

        RecyclerView rvHit = findViewById(R.id.rvHits);
        LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(MainActivityController.this, LinearLayoutManager.HORIZONTAL, false);
        rvHit.setLayoutManager(horizontalLayoutManager1);
        SongAdapter adapter = new SongAdapter(this, viewHot);
        rvHit.setAdapter(adapter);

        RecyclerView rvNew = findViewById(R.id.rvNew);
        LinearLayoutManager horizontalLayoutManager2 = new LinearLayoutManager(MainActivityController.this, LinearLayoutManager.HORIZONTAL, false);
        rvNew.setLayoutManager(horizontalLayoutManager2);
        adapter = new SongAdapter(this, viewNew);
        rvNew.setAdapter(adapter);
    }

    public void openUserMenu(View view){
        Log.d("tag", "menu openned");
        PopupMenu userMenu = new PopupMenu(this, view);
        userMenu.setOnMenuItemClickListener(this);
        MenuInflater inflater = userMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_user, userMenu.getMenu());
        userMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        boolean returnValue;
        Log.d("tag", String.valueOf(menuItem.getItemId()));
        switch (menuItem.getItemId())
        {
            case R.id.login:
                AuthorizationRequest.Builder builder =
                        new AuthorizationRequest.Builder("55cf1e6ed39d4fa1becb626ec9086ef1", AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

                builder.setScopes(new String[]{"streaming"});
                AuthorizationRequest request = builder.build();

                AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
                returnValue =  true;
                break;

            case R.id.logout:
                AuthorizationClient.clearCookies(this);
                returnValue = true;
                break;

            default:
                returnValue = false;
                break;

        }

        return returnValue;
    }


    public void generatePlaylist(View view){
        startActivity(new Intent(this, GeneratePlaylistController.class));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response

                    APISpotify.setToken(response.getAccessToken());
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }


    public void playlist(View view){
        Log.d("", "TESTESTESTESTESTEST");
        SpotifyService spotify = APISpotify.api.getService();

        spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>() {
            @Override
            public void success(Album album, Response response) {
                Log.d("Album success", album.name);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Album failure", error.toString());
            }
        });

        spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>() {
            @Override
            public void success(Album album, Response response) {
                Log.d("Album success", album.name);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Album failure", error.toString());
            }
        });

        List<PlaylistSimple> test = spotify.getMyPlaylists().items;
        if(test.isEmpty()) {
            Log.d("", "FAILURE");
        }else{
            Log.d("", "SUCCESS");
        }

    }

}
