package com.ziad.gallery_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    FloatingActionButton button;
    private RecyclerView galleryRecyclerView;
    private GalleryAdapter galleryAdapter;
    private BroadcastReceiver photosChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            loadPhotos();
        }
    };

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        // Find the RecyclerView in the layout
        galleryRecyclerView = findViewById(R.id.galleryRecyclerView);

        // Set the layout manager for the RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this,4);
        galleryRecyclerView.setLayoutManager(layoutManager);

        // Request permission to write to external storage if it has not been granted yet
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
        } else {
            // Load the photos into the RecyclerView if permission has already been granted
            loadPhotos();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register the BroadcastReceiver to listen for changes to the photos
        IntentFilter intentFilter = new IntentFilter("com.example.gallery.PHOTOS_CHANGED");
        registerReceiver(photosChangedReceiver, intentFilter);

        // Reload the photos in case they have changed
        loadPhotos();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the BroadcastReceiver to avoid memory leaks
        unregisterReceiver(photosChangedReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load the photos into the RecyclerView
                loadPhotos();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied to write to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadPhotos() {
        // Set up the query parameters
        String[] projection = { MediaStore.Images.Media.DATA };
        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        // Query the MediaStore for all images, sorted by date taken
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);

        // Iterate over the cursor and add each image file to the list
        List<File> allPhotos = new ArrayList<File>();
        if (cursor != null) {
            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(dataIndex);
                allPhotos.add(new File(filePath));
            }
            cursor.close();
        }

        // Reverse the order of the list to show the last taken photo first
        Collections.reverse(allPhotos);

        // Create a new GalleryAdapter with the image files
        galleryAdapter = new GalleryAdapter(allPhotos.toArray(new File[allPhotos.size()]));
        galleryAdapter.notifyDataSetChanged();
        galleryRecyclerView.setAdapter(galleryAdapter);
    }
}