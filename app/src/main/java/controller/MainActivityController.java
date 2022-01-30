package controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
import kaaes.spotify.webapi.android.models.Followers;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import model.Song;
import model.SongAdapter;
import model.APISpotify;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/*
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    SpotifyService spotify = APISpotify.api.getService();

    Strictmode allows us to bypass the threading paradigm of the app that would cause it ot crash
    when performing http calls to through the api. This is only a temporary fix, for a long term
    project this would be implemented using multithreading to maintain performance and prevent
    the app from crashing.
 */

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
        Log.d("tag", "menu opened");
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
                //The clientId needs to be fetch from the apps project on the Spotify developper tool
                AuthorizationRequest.Builder builder =
                        new AuthorizationRequest.Builder("55cf1e6ed39d4fa1becb626ec9086ef1", AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

                //All the scopes were added to make sure that all the desired actions can be performed
                builder.setScopes(new String[]{"streaming", "user-read-private", "user-follow-modify",
                        "user-follow-read", "user-library-modify", "user-library-read", "playlist-modify-private",
                        "playlist-read-collaborative", "app-remote-control", "user-read-email",
                        "playlist-read-private", "user-top-read", "playlist-modify-public",
                        "user-read-currently-playing", "user-read-recently-played", "ugc-image-upload",
                        "user-read-playback-state", "user-modify-playback-state", "user-read-playback-position"
                });
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

                    //Sets the token into the wrapper that will make the http calls
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

}
