package fr.uge.soundroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private List<Playlist> playlists;

    public PlaylistAdapter(List<Playlist> l){
        playlists = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.playlist_list_icon, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Playlist p = playlists.get(position);

        holder.playlistName.setText(p.getName());
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView playlistName;
        public View iconView;

        public ViewHolder(View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlistNameIcon);
            iconView = itemView.findViewById(R.id.playlistIcon);
        }
    }

}
