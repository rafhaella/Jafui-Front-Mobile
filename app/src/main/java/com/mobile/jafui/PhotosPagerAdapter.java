package com.mobile.jafui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotosPagerAdapter extends RecyclerView.Adapter<PhotosPagerAdapter.PhotoViewHolder> {

    private List<String> photos;

    public PhotosPagerAdapter(List<String> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_place, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String photoBase64 = photos.get(position);
        // Decodifique a foto em Base64 para um formato adequado (por exemplo, Bitmap)
        Bitmap photoBitmap = decodeBase64(photoBase64);
        // Defina a foto no ImageView
        holder.imageViewPhoto.setImageBitmap(photoBitmap);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    private Bitmap decodeBase64(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewPhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPhoto = itemView.findViewById(R.id.imageView_photo);
        }
    }

}
