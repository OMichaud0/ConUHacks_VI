package model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conuhacks_vi.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{
    private List<Song> mViewSongs;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public SongAdapter(Context context, List<Song> songs) {
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
        Song song = mViewSongs.get(position);
        holder.vCover.setImageResource(R.drawable.starboy_cover);
        holder.vArtist.setText(song.artist);
        holder.vTitle.setText(song.title);
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
    public Song getItem(int id) {
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

}
