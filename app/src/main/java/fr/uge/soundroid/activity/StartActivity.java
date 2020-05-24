package fr.uge.soundroid.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import fr.uge.soundroid.R;
import fr.uge.soundroid.service.IndexService;

public class StartActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE
    };

    private static final int REQUEST_PERMISSIONS = 1;

    private static final int PERMISSIONS_COUNT = 1;

    @SuppressLint("NewApi")
    private boolean arePermissionsDenied() {
        for (int i = 0; i < PERMISSIONS_COUNT; ++i) {
            if (checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (arePermissionsDenied()) {
            ((ActivityManager) (this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
            recreate();
        } else {
            onResume();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }
        IndexTask index = new IndexTask(this);
        index.execute();
    }

    private static class IndexTask extends AsyncTask<Void, Void, Void> {
        Activity activity;
        private IndexService indexService;

        IndexTask(Activity act) {
            activity = act;
            indexService = new IndexService(activity.getApplication());
        }
        @Override
        protected Void doInBackground(Void... voids) {
            indexService.addMusicFilesFromRoot();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(activity.getApplicationContext(), HomeActivity.class);
            Log.d("index", "indexing finished");
            activity.startActivity(intent);
            activity.finish();
        }
    }
}
