package com.mobile.jafui.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;
import com.mobile.jafui.ImageUtils;
import com.mobile.jafui.PhotoAdapter;
import com.mobile.jafui.R;
import com.mobile.jafui.activities.ui.home.HomeFragment;
import com.mobile.jafui.api.PlaceService;
import com.mobile.jafui.api.RestServiceGenerator;
import com.mobile.jafui.entidades.Place;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePlaceActivity extends AppCompatActivity implements PhotoAdapter.OnImageDeleteListener {

    private ViewPager2 pageView;
    private PhotoAdapter photoAdapter;
    private List<String> imageList;
    private ActivityResultLauncher<String[]> selectImageLauncher;
    private PlaceService placeService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_place);

        String userEmail = getIntent().getStringExtra("userEmail");
        
        // Initialize the image list
        imageList = new ArrayList<>();


        pageView = findViewById(R.id.view_pager_photos);
        photoAdapter = new PhotoAdapter(imageList, this);
        pageView.setAdapter(photoAdapter);

        selectImageLauncher = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(),
                new ActivityResultCallback<List<Uri>>() {
                    @Override
                    public void onActivityResult(List<Uri> result) {
                        if (result != null && result.size() > 0) {
                            int initialSize = imageList.size(); // Store the initial size of the image list
                            List<String> compressedImagePaths = ImageUtils.compressImages(CreatePlaceActivity.this, result);
                            List<String> base64Images = ImageUtils.convertToBase64(compressedImagePaths);
                            imageList.addAll(base64Images);

                            int newItemsCount = imageList.size() - initialSize; // Calculate the number of new items added
                            photoAdapter.notifyItemRangeInserted(initialSize, newItemsCount); // Notify adapter of inserted items
                        }
                    }
                });

        Button selectImageButton = findViewById(R.id.button_add_photos);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] mimeTypes = {"image/*"};
                selectImageLauncher.launch(mimeTypes);
            }
        });

        ImageButton buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button createPlaceButton = findViewById(R.id.button_create_place);


        createPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText nameEditText = findViewById(R.id.edit_text_name);
                TextInputEditText addressEditText = findViewById(R.id.edit_text_address);
                TextInputEditText descriptionEditText = findViewById(R.id.edit_text_description);
                RatingBar ratingBar = findViewById(R.id.ratingBar);
                float rating = ratingBar.getRating();

                Place place = new Place();
                place.setName(nameEditText.getText().toString());
                place.setAddress(addressEditText.getText().toString());
                place.setDescription(descriptionEditText.getText().toString());
                place.setRating(rating);
                place.setCreatedBy(userEmail);

                List<String> base64Images = new ArrayList<>();
                for (String base64Image : imageList) {
                    base64Images.add(base64Image);
                    Log.d("CreatePlaceActivity", "Imagem base64: " + base64Image);
                }
                place.setPhotos(base64Images);

                placeService = RestServiceGenerator.createService(PlaceService.class);
                Call<Place> call = placeService.createPlace(place);
                call.enqueue(new Callback<Place>() {
                    @Override
                    public void onResponse(Call<Place> call, Response<Place> response) {
                        if (response.isSuccessful()) {
                            Place createdPlace = response.body();
                            Toast.makeText(getApplicationContext(), "Lugar criado com sucesso", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                            resultIntent.putExtra("email", userEmail);
                            setResult(RESULT_OK, resultIntent);
                            startActivity(resultIntent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Erro ao criar o lugar", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Place> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Erro na chamada à API", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    @Override
    public void onImageDelete(int position) {
        if (position >= 0 && position < imageList.size()) {
            imageList.remove(position);
            photoAdapter.notifyItemRemoved(position);
            Toast.makeText(this, "Imagem removida", Toast.LENGTH_SHORT).show();
        }
    }
}