package com.mobile.jafui;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<String> imageList;
    private final OnImageDeleteListener onImageDeleteListener;

    public PhotoAdapter(List<String> imageList, OnImageDeleteListener onImageDeleteListener) {
        this.imageList = imageList;
        this.onImageDeleteListener = onImageDeleteListener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String base64Image = imageList.get(position);
        Bitmap bitmap = decodeBase64Image(base64Image);
        holder.imageView.setImageBitmap(bitmap);

        holder.deleteButton.setOnClickListener(v -> {
            if (onImageDeleteListener != null) {
                onImageDeleteListener.onImageDelete(holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    private Bitmap decodeBase64Image(String base64Image) {
        if (base64Image != null) {
            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } else {
            return null;
        }
    }


    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton deleteButton;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_place);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    public interface OnImageDeleteListener {
        void onImageDelete(int position);
    }
}
