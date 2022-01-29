package controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conuhacks_vi.R;
import com.spotify.sdk.android.auth.AuthorizationClient;

public class GeneratePlaylistController extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.generate_playlist);
}


    public void logout(View view){
        AuthorizationClient.clearCookies(this);
        startActivity(new Intent(this, MainActivityController.class));
    }
}
