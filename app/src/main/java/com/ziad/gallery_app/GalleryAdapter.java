package com.ziad.gallery_app;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private File[] photos;

    public GalleryAdapter(File[] photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File photo = photos[position];
        holder.bind(photo);
    }

    @Override
    public int getItemCount() {
        return photos.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView photoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            itemView.setOnClickListener(this);
        }

        public void bind(File photo) {
            Glide.with(photoImageView.getContext())
                    .load(photo)
                    .centerCrop()
                    .into(photoImageView);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                File photo = photos[position];
                showPhotoDialog(photo);
            }
        }

        private void showPhotoDialog(File photo) {
            // Create a new Dialog object
            Dialog photoDialog = new Dialog(photoImageView.getContext());
            photoDialog.setContentView(R.layout.photo_dialog);

            // Find the ImageView in the dialog layout and load the selected photo into it
            ImageView selectedPhotoImageView = photoDialog.findViewById(R.id.selectedPhotoImageView);
            Glide.with(selectedPhotoImageView.getContext())
                    .load(photo)
                    .fitCenter()
                    .into(selectedPhotoImageView);

            // Show the dialog
            photoDialog.show();
        }
    }
}