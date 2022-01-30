package model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.conuhacks_vi.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import controller.FavoriteController;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class SongAdapter3 extends ArrayAdapter<Track> {

    private Context mContext;
    private int mResource;

    public SongAdapter3(@NonNull Context context, int resource, @NonNull List<Track> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        Track track = getItem(position);

        convertView = layoutInflater.inflate(mResource, parent, false);

        ImageView song_cover = convertView.findViewById(R.id.song_cover);
        TextView song_title = convertView.findViewById(R.id.song_title);
        TextView song_artist = convertView.findViewById(R.id.song_artist);
        TextView song_duration = convertView.findViewById(R.id.song_duration);


        song_title.setText(getItem(position).name);
        song_artist.setText(getItem(position).artists.get(0).name);
        int ms = (int) getItem(position).duration_ms;
        int min = (ms/1000)/60;
        int s = (ms/1000)%60;

        if(s < 10)
            song_duration.setText(min + ":0" + s);
        else
            song_duration.setText(min + ":" + s);

        Drawable d = loadCoverFromWeb(getItem(position).album.images.get(0).url);
        if(d != null)
            song_cover.setImageDrawable(d);
        else
            song_cover.setImageResource(R.drawable.no_image);


        return convertView;
    }

    private Drawable loadCoverFromWeb(String url) {
        try{
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "");
            return d;
        } catch (Exception e){
            return null;
        }
    }
}
