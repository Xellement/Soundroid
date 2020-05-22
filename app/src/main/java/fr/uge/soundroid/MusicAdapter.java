package fr.uge.soundroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder>{

    private List<Music> musics;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView musicName, musicArtist;
        public ImageView iconMusic, iconLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            musicName = itemView.findViewById(R.id.MusicName);
            musicArtist = itemView.findViewById(R.id.MusicArtist);
            iconMusic = itemView.findViewById(R.id.iconMusic);
            iconLike = itemView.findViewById(R.id.iconLike);
        }

        private void update(Music m) {
            musicName.setText(m.getMusicName());
            musicArtist.setText(m.getArtist());
            iconMusic.setImageBitmap(m.getBitmapIcon(iconMusic.getContext()));
            iconLike.setImageBitmap(m.getBitmapLike(iconLike.getContext()));
        }

        @Override
        public void onClick(View v) {

        }
    }

    public MusicAdapter(List<Music> l){
        super();
        musics = l;
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
