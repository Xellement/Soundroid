package fr.uge.soundroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import fr.uge.soundroid.R;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // TODO : use PlayerService to play the musics
        Intent intent = getIntent();
        String name = intent.getStringExtra("MusicName");
        String artist = intent.getStringExtra("MusicArtist");
        boolean isLiked = intent.getBooleanExtra("isLiked", false);
        TextView tv = findViewById(R.id.musicName);
        tv.setText(name);

        tv = findViewById(R.id.musicArtist);
        tv.setText(artist);

        ImageView imageView = findViewById(R.id.likeButton);
        if (isLiked) {
            imageView.setImageResource(R.drawable.like);
        }
        else {
            imageView.setImageResource(R.drawable.nolike);
        }
    }
}
