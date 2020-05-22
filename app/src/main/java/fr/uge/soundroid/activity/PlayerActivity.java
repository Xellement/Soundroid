package fr.uge.soundroid.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import fr.uge.soundroid.R;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        String name = intent.getStringExtra("MusicName"), artist = intent.getStringExtra("MusicArtist");

        TextView tv = findViewById(R.id.musicName);
        tv.setText(name);

        tv = findViewById(R.id.musicArtist);
        tv.setText(artist);

    }
}
