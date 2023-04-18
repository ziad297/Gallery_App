package com.ziad.gallery_app;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

public class PhotoService extends Service {

    private ContentObserver contentObserver;

    @Override
    public void onCreate() {
        super.onCreate();

        // Register a content observer to listen for changes in the MediaStore.Images.Media.EXTERNAL_CONTENT_URI URI
        contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                // Send a broadcast to notify that the photos have changed
                Intent intent = new Intent("com.example.gallery.PHOTOS_CHANGED");
                sendBroadcast(intent);
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the content observer when the service is destroyed
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
