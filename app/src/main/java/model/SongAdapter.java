package model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conuhacks_vi.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistTrack;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{
    private List<PlaylistTrack> mViewSongs;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public SongAdapter(Context context, List<PlaylistTrack> songs) {
        this.mInflater = LayoutInflater.from(context);
        this.mViewSongs = songs;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.song_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistTrack song = mViewSongs.get(position);

        Drawable d = loadCoverFromWeb(song.track.album.images.get(0).url);
        if(d != null)
            holder.vCover.setImageDrawable(d);
        else
            holder.vCover.setImageResource(R.drawable.no_image);

        holder.vArtist.setText(song.track.artists.get(0).name);
        holder.vTitle.setText(song.track.name);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mViewSongs.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView vCover;
        TextView vArtist;
        TextView vTitle;

        ViewHolder(View itemView) {
            super(itemView);
            vCover = itemView.findViewById(R.id.image_cover);
            vArtist = itemView.findViewById(R.id.text_artist);
            vTitle = itemView.findViewById(R.id.text_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public PlaylistTrack getItem(int id) {
        return mViewSongs.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
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
