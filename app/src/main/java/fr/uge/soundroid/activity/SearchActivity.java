package fr.uge.soundroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import fr.uge.soundroid.R;
import fr.uge.soundroid.SongAdapter;
import fr.uge.soundroid.database.entity.Song;
import fr.uge.soundroid.database.viewmodel.SongViewModel;

public class SearchActivity extends AppCompatActivity {
    private final static int SEARCH_SONG = 0;
    private final static int SEARCH_ARTIST = 1;
    private final static int SEARCH_ALBUM = 2;

    private SongAdapter searchResultAdapter;
    private RecyclerView searchResultRV;
    private SongViewModel songViewModel;
    private List<Song> resultByArtist;
    private List<Song> resultByAlbum;
    private List<Song> resultByKeyWord;
    private List<Song> searchResult;
    private EditText searchEdit;
    private Button validate;
    private String search = "";
    private int searchType = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        songViewModel = ViewModelProviders.of(this).get(SongViewModel.class);
        searchEdit = findViewById(R.id.searchEdit);
        searchResultRV = findViewById(R.id.searchRecycler);
        validate = findViewById(R.id.searchValider);

        searchResultAdapter = new SongAdapter(new ArrayList<Song>());
        setMusicListenerClick(searchResultAdapter);
        searchResultRV.setAdapter(searchResultAdapter);
        updateLayoutManager(searchResultRV, 1);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                search = searchEdit.getText().toString();
                if (search.equals("")) {
                    return;
                }
                Log.d("edits", search);

                switch (searchType) {
                    case SEARCH_SONG:
                        searchByKeyWords(search);
                        break;
                    case SEARCH_ALBUM:
                        searchByAlbum(search);
                        break;
                    case SEARCH_ARTIST:
                        searchByArtist(search);
                        break;
                    default:
                        return;
                }
                searchType = -1;
                updateSearchResult();
            }
        });
    }

    private void updateSearchResult() {
        List<Song> tmp = new ArrayList<>();
        if (resultByArtist != null && resultByArtist.size() > 0) {
            tmp.addAll(resultByArtist);
        }
        if (resultByAlbum != null && resultByAlbum.size() > 0) {
            tmp.addAll(resultByAlbum);
        }
        if (resultByKeyWord != null && resultByKeyWord.size() > 0) {
            tmp.addAll(resultByKeyWord);
        }
        if (tmp.size() > 0) {
            searchResult = new ArrayList<>(new HashSet<>(tmp));
            Log.d("result", searchResult.toString());
            searchResultAdapter.setMusicList(searchResult);
        }
    }

    private void searchByArtist(String artist) {
        if (artist.equals("")) return;
        songViewModel.findByArtist(artist).observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                resultByArtist = songs;
                updateSearchResult();
            }
        });
    }

    private void searchByAlbum(String album) {
        if (album.equals("")) return;
        songViewModel.findByAlbum(album).observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                resultByAlbum = songs;
                updateSearchResult();
            }
        });
    }

    private void searchByKeyWords(String keyWords) {
        if (keyWords.equals("")) return;
        songViewModel.findLikeName(keyWords).observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                resultByKeyWord = songs;
                updateSearchResult();
            }
        });
    }

    public void setMusicListenerClick(final SongAdapter adap){
        // TODO : maybe find a way to refactor this because its the same code as in PlaylistActivity.setMusicClickListener()
        adap.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext() , PlayerActivity.class);
                intent.putExtra("MusicIndex", position);
                intent.putExtra("MusicsList", (ArrayList<Song>) adap.getMusics());
                intent.putExtra("PlaylistName", "Musics"); // TODO : deal with "Récents" and "Historique" -> they are currently the same
                intent.putExtra("BackActivity", "HOME");
                intent.putExtra("HomeStatus","Recents");
                intent.putExtra("AlreadyPlaying", false);
                startActivity(intent);
            }
        });
    }

    private void updateLayoutManager(RecyclerView rv, int span) {
        rv.setLayoutManager(new GridLayoutManager(this, span, GridLayoutManager.VERTICAL, false));
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonSong:
                if (checked) {
                    searchType = SEARCH_SONG;
                }
                break;
            case R.id.radioButtonAlbum:
                if (checked) {
                    searchType = SEARCH_ALBUM;
                }
                break;
            case R.id.radioButtonArtist:
                if (checked) {
                    searchType = SEARCH_ARTIST;
                }
                break;
        }
    }

}
