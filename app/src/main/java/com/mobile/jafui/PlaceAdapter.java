package com.mobile.jafui;

import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobile.jafui.activities.PlaceActivity;
import com.mobile.jafui.entidades.Place;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private List<Place> places;

    public void setPlaces(List<Place> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = places.get(position);
        holder.bind(place);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir a PlaceActivity com os dados do lugar selecionado
                Intent intent = new Intent(v.getContext(), PlaceActivity.class);
                intent.putExtra("place", place);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places != null ? places.size() : 0;
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewPlace;
        RatingBar ratingBarPlace;
        TextView textViewName;
        TextView textViewAddress;
        TextView textViewDescription;
        TextView textViewCreatedBy;
        TextView textViewRatingNumber;

        PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPlace = itemView.findViewById(R.id.imageView_place);
            ratingBarPlace = itemView.findViewById(R.id.ratingBar_place);
            textViewName = itemView.findViewById(R.id.textView_name_place);
            textViewAddress = itemView.findViewById(R.id.textView_address_place);
            textViewDescription = itemView.findViewById(R.id.textView_description_place);
            textViewCreatedBy = itemView.findViewById(R.id.textNameUser);
            textViewRatingNumber = itemView.findViewById(R.id.ratingNumber);
        }

        void bind(Place place) {
            byte[] decodedString = Base64.decode(place.getPhotos().get(0), Base64.DEFAULT);
            Glide.with(itemView.getContext())
                    .asBitmap()
                    .load(decodedString)
                    .into(imageViewPlace);

            ratingBarPlace.setRating(place.getRating());
            textViewName.setText(place.getName());
            textViewAddress.setText(place.getAddress());
            textViewDescription.setText(place.getDescription());
            textViewCreatedBy.setText(place.getCreatedBy());
            textViewRatingNumber.setText(String.valueOf(place.getRating()));
        }
    }
}

