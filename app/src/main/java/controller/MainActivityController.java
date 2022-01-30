package controller;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conuhacks_vi.R;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.ArrayList;

import model.APISpotify;
import model.Song;
import model.SongAdapter;

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
    SharedPreferences sharedPreferences;
    boolean isConnected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        sharedPreferences = getSharedPreferences("ConuHacks", MODE_PRIVATE);
        if(!sharedPreferences.getString("token", "").isEmpty()) {
            APISpotify.setAccess(sharedPreferences.getString("token", ""));
            isConnected = true;
        }else
            isConnected = false;


        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if(s.equals("token"))
                {
                    if(!sharedPreferences.getString("token", "").isEmpty())
                        APISpotify.setAccess(sharedPreferences.getString("token", ""));
                }
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);



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
                login();
                returnValue =  true;
                break;

            case R.id.logout:
                AuthorizationClient.clearCookies(this);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", "");
                editor.apply();
                isConnected = false;
                returnValue = true;
                break;

            default:
                returnValue = false;
                break;

        }

        return returnValue;
    }

    private void login(){
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
    }


    public void generatePlaylist(View view){
        if(!isConnected)
        {
            new AlertDialog.Builder(this).setTitle("Spotify account not connected").setMessage("Your Spotify account needs to be connected to get access to this feature")
                    .setPositiveButton("Login", (dialog, which) -> login())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        }
        else
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
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", response.getAccessToken());
                    editor.apply();
                    isConnected = true;
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
