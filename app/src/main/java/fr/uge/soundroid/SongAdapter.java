package fr.uge.soundroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.uge.soundroid.database.entity.Song;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    private List<Song> musics;

    // Define listener member variable
    private SongAdapter.OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(SongAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView musicName, musicArtist;
        public ImageView iconMusic, iconLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            musicName = itemView.findViewById(R.id.MusicName);
            musicArtist = itemView.findViewById(R.id.MusicArtist);
            iconMusic = itemView.findViewById(R.id.iconMusic);
            iconLike = itemView.findViewById(R.id.iconLike);

            System.out.println("TAILLE : " + musicName.getTextSize());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onItemClick(v, position);
                    }
                }
            });
        }

        private void update(Song song) {
            musicName.setText(song.getMusicName());
            musicArtist.setText(song.getArtist());
            iconMusic.setImageBitmap(song.getBitmapIcon(iconMusic.getContext()));
            iconLike.setImageBitmap(song.getBitmapLike(iconLike.getContext()));
        }

        @Override
        public void onClick(View v) {}
    }

    public SongAdapter(List<Song> l){
        super();
        musics = l;
    }

    public void setMusicList(List<Song> l) {
        musics = l;
        notifyDataSetChanged();
    }

    public List<Song> getMusics() {
        return musics;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        holder.update(musics.get(pos));
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

}
