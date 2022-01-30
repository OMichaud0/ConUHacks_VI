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

import kaaes.spotify.webapi.android.models.PlaylistSimple;

public class PlaylistAdapter extends ArrayAdapter<PlaylistSimple> {

    private Context mContext;
    private int mResource;

    public PlaylistAdapter(@NonNull Context context, int resource, @NonNull List<PlaylistSimple> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        PlaylistSimple playlist = getItem(position);

        convertView = layoutInflater.inflate(mResource, parent, false);

        ImageView playlist_cover = convertView.findViewById(R.id.playlist_cover);
        TextView playlist_name = convertView.findViewById(R.id.playlist_name);
        TextView playlist_owner = convertView.findViewById(R.id.playlist_owner);


        playlist_name.setText(getItem(position).name);
        playlist_owner.setText(getItem(position).owner.display_name);

        Drawable d = loadCoverFromWeb(getItem(position).images.get(0).url);
        if(d != null)
            playlist_cover.setImageDrawable(d);
        else
            playlist_cover.setImageResource(R.drawable.no_image);


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
