package fr.uge.soundroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private List<Playlist> playlists;

    // Define listener member variable
    private OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView playlistName;
        public ImageView iconView;

        public ViewHolder(final View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlistNameIcon);
            iconView = itemView.findViewById(R.id.iconePlaylist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }

        private void update(Playlist p) {
            iconView.setImageBitmap(p.getBitmap(iconView.getContext()));
            playlistName.setText(p.getName());
        }

        @Override
        public void onClick(View v) {

        }
    }

    public PlaylistAdapter(List<Playlist> l){
        super();
        playlists = l;
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_list_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        holder.update(playlists.get(pos));
    }

}
