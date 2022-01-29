package controller;

import static com.spotify.sdk.android.auth.AccountsQueryParameters.CLIENT_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conuhacks_vi.R;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.ArrayList;

import model.Song;
import model.SongAdapter;

public class MainActivityController extends AppCompatActivity {
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private String token = "";

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

    public void generatePlaylist(View view){
        startActivity(new Intent(this, GeneratePlaylistController.class));


        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder("55cf1e6ed39d4fa1becb626ec9086ef1", AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
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

                    token = response.getAccessToken();
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
