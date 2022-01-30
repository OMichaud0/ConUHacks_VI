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

import kaaes.spotify.webapi.android.models.PlaylistTrack;

public class SongAdapter2 extends ArrayAdapter<PlaylistTrack> {

    private Context mContext;
    private int mResource;

    public SongAdapter2(@NonNull Context context, int resource, @NonNull List<PlaylistTrack> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        PlaylistTrack track = getItem(position);

        convertView = layoutInflater.inflate(mResource, parent, false);

        ImageView song_cover = convertView.findViewById(R.id.song_cover);
        TextView song_title = convertView.findViewById(R.id.song_title);
        TextView song_artist = convertView.findViewById(R.id.song_artist);
        TextView song_duration = convertView.findViewById(R.id.song_duration);


        song_title.setText(getItem(position).track.name);
        song_artist.setText(getItem(position).track.artists.get(0).name);
        int ms = (int) getItem(position).track.duration_ms;
        int min = (ms/1000)/60;
        int s = (ms/1000)%60;

        if(s < 10)
            song_duration.setText(min + ":0" + s);
        else
            song_duration.setText(min + ":" + s);

        Drawable d = loadCoverFromWeb(getItem(position).track.album.images.get(0).url);
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
