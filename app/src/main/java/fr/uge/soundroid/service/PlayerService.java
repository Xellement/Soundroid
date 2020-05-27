package fr.uge.soundroid.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.uge.soundroid.R;
import fr.uge.soundroid.activity.PlayerActivity;
import fr.uge.soundroid.database.entity.Song;

public class PlayerService extends Service implements Serializable, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        AudioManager.OnAudioFocusChangeListener {

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    public static final String ACTION_PLAY = "fr.uge.soundroid.service.ACTION_PLAY";
    public static final String ACTION_PAUSE = "fr.uge.soundroid.service.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "fr.uge.soundroid.service.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "fr.uge.soundroid.service.ACTION_NEXT";
    public static final String ACTION_STOP = "fr.uge.soundroid.service.ACTION_STOP";

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;

    private final IBinder iBinder = new LocalBinder();
    private MediaPlayer player;
    private List<Song> songs;
    private Song currentSong;
    private int currentSongIndex = -1;
    private int resumePosition;
    private AudioManager audioManager;

    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    private void initMediaPlayer() {
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
        player.setOnSeekCompleteListener(this);
        player.reset();
        try {
            player.setDataSource(currentSong.getSongPath());
        } catch (IOException e) {
            Log.e("player.setDatasource", "Error while setting datasource :" + e.toString());
            stopSelf();
        }
        player.prepareAsync();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        //Invoked when the audio focus of the system is updated.
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (player == null) initMediaPlayer();
                else if (!player.isPlaying()) player.start();
                player.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (player.isPlaying()) player.stop();
                player.release();
                player = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (player.isPlaying()) player.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (player.isPlaying()) player.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        skipToNext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Perform one-time setup procedures

        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
        callStateListener();
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
        //Listen for new Audio to play -- BroadcastReceiver
        register_playNewAudio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            stopSong();
            player.release();
        }
        removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        removeNotification();
        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback.
        playSong();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() == null) {
            try {
                //An audio file is passed to the service through putExtra();
                currentSongIndex = intent.getIntExtra("SongIndex", 0);
                List<Song> playlist = (ArrayList<Song>) intent.getSerializableExtra("Playlist");
                if (currentSongIndex != -1 && currentSongIndex < playlist.size()) {
                    currentSong = playlist.get(currentSongIndex);
                    songs = playlist;
                } else {
                    stopSelf();
                }
            } catch (NullPointerException e) {
                Log.d("Null", "Received null playlist");
            }
        }
        //Request audio focus
        if (! requestAudioFocus()) {
            //Could not gain focus
            stopSelf();
        }
        if (mediaSessionManager == null) {
            initMediaSession();
            initMediaPlayer();
            buildNotification(PlaybackStatus.PLAYING);
        }
        if (intent.getAction() != null) {
            //Handle Intent action from MediaSession.TransportControls
            handleIncomingActions(intent);
        }
        else {
            changeSong();
        }
        return START_STICKY;
    }

    public void playSong() {
        if (! player.isPlaying()) {
            player.start();
        }
    }

    public void stopSong() {
        if (player == null) return;
        if (player.isPlaying()) {
            player.stop();
        }
    }

    public void pauseSong() {
        if (player.isPlaying()) {
            player.pause();
            resumePosition = player.getCurrentPosition();
        }
    }

    public void resumeSong() {
        if (! player.isPlaying()) {
            player.seekTo(resumePosition);
            player.start();
        }
    }

    public void changeSong() {
        if (player == null) return;
        if (player.isPlaying()) {
            player.stop();
         }
        player.reset();
        try {
            player.setDataSource(currentSong.getSongPath());
        } catch (IOException e) {
            Log.e("setDatasource", "Error while setting player datasource for song " + currentSong.getMusicName());
            stopSelf();
        }
        player.prepareAsync();
    }

    public void skipToNext() {

        if (currentSongIndex == songs.size() - 1) {
            //if last in playlist
            currentSongIndex = 0;
            currentSong = songs.get(currentSongIndex);
        } else {
            //get next in playlist
            currentSong = songs.get(++currentSongIndex);
        }
        stopSong();
        //reset mediaPlayer
        player.reset();
        initMediaPlayer();
    }

    public void skipToPrevious() {

        if (currentSongIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            currentSongIndex = songs.size() - 1;
            currentSong = songs.get(currentSongIndex);
        } else {
            //get previous in playlist
            currentSong = songs.get(--currentSongIndex);
        }
        stopSong();
        //reset mediaPlayer
        player.reset();
        initMediaPlayer();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void seekTo(int ms) {
        player.seekTo(ms);
    }

    //Handle incoming phone calls
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (player != null) {
                            pauseSong();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (player != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeSong();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    //Becoming noisy
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseSong();
            buildNotification(PlaybackStatus.PAUSED);
        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                //Get the new media index form SharedPreferences
                currentSongIndex = intent.getIntExtra("MusicIndex", -1);
                songs = (ArrayList<Song>) intent.getSerializableExtra("Playlist");
                if (currentSongIndex != -1 && currentSongIndex < songs.size()) {
                    //index is in a valid range
                    currentSong = songs.get(currentSongIndex);
                } else {
                    stopSelf();
                }
            } catch (NullPointerException e) {
                Log.d("NPE", e.toString());
                stopSelf();
            }
            //A PLAY_NEW_AUDIO action received
            //reset mediaPlayer to play the new Audio
            stopSong();
            player.reset();
            initMediaPlayer();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
        }
    };

    private void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(PlayerActivity.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    @SuppressLint("NewApi")
    private void initMediaSession() {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeSong();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseSong();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                Log.d("STOP", "SHOULD STOP");
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void updateMetaData() {
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, currentSong.getBitmapIcon(getApplicationContext()))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSong.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentSong.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSong.getMusicName())
                .build());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }


    private void buildNotification(PlaybackStatus playbackStatus) {
        /*
         * Notification actions -> playbackAction()
         *  0 -> Play
         *  1 -> Pause
         *  2 -> Next track
         *  3 -> Previous track
         *  4 -> Stop
         */
        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = currentSong.getBitmapIcon(getApplicationContext());
        MediaControllerCompat controller = mediaSession.getController();

        String channelId;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("playerservice", "Player Service");
        }
        else {
            channelId = "";
        }

        Intent activityIntent = new Intent(this, PlayerActivity.class);
        activityIntent.putExtra("MusicIndex", currentSongIndex);
        activityIntent.putExtra("MusicsList", (ArrayList<Song>) songs);
        activityIntent.putExtra("AlreadyPlaying", true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(activityIntent);
        PendingIntent activityPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(getResources().getColor(R.color.colorPrimary))
                // Set the large and small icons
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText(currentSong.getArtist())
                .setContentTitle(currentSong.getMusicName())
                .setContentInfo(currentSong.getAlbum())

                .setContentIntent(activityPendingIntent/*controller.getSessionActivity()*/)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // Add playback actions
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2))
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "stop", playbackAction(4));

//        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
        startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, PlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 4:
                // Stop track
                playbackAction.setAction(ACTION_STOP);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }
}