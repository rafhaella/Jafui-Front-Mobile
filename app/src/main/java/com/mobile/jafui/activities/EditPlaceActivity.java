package com.mobile.jafui.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mobile.jafui.ImageUtils;
import com.mobile.jafui.PhotoAdapter;
import com.mobile.jafui.R;
import com.mobile.jafui.api.PlaceService;
import com.mobile.jafui.api.RestServiceGenerator;
import com.mobile.jafui.entidades.Place;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPlaceActivity extends AppCompatActivity implements PhotoAdapter.OnImageDeleteListener {

    private TextView textViewPlaceName;
    private TextView textViewPlaceAddress;
    private TextView textViewPlaceDescription;
    private RatingBar ratingBarPlace;
    private List<String> imageList;
    private PhotoAdapter photoAdapter;
    private ActivityResultLauncher<String[]> selectImageLauncher;
    private ViewPager2 pageView;
    private PlaceService placeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);

        textViewPlaceName = findViewById(R.id.edit_text_name);
        textViewPlaceAddress = findViewById(R.id.edit_text_address);
        textViewPlaceDescription = findViewById(R.id.edit_text_description);
        ratingBarPlace = findViewById(R.id.ratingBar);
        pageView = findViewById(R.id.view_pager_photos);
        imageList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(imageList, this);
        pageView.setAdapter(photoAdapter);

        ImageButton buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> onBackPressed());

        Place place = (Place) getIntent().getSerializableExtra("place");
        String userEmail = getIntent().getStringExtra("userEmail");
        String idPlace = place.getId();

        textViewPlaceName.setText(place.getName());
        textViewPlaceAddress.setText(place.getAddress());
        textViewPlaceDescription.setText(place.getDescription());
        ratingBarPlace.setRating(place.getRating());

        // Configurar o ViewPager2 com as imagens do lugar
        photoAdapter.setImageList(place.getPhotos());
        photoAdapter.notifyDataSetChanged();
        pageView.setCurrentItem(0, false);


        selectImageLauncher = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(),
                new ActivityResultCallback<List<Uri>>() {
                    @Override
                    public void onActivityResult(List<Uri> result) {
                        if (result != null && result.size() > 0) {
                            int initialSize = imageList.size(); // Store the initial size of the image list
                            List<String> compressedImagePaths = ImageUtils.compressImages(EditPlaceActivity.this, result);
                            List<String> base64Images = ImageUtils.convertToBase64(compressedImagePaths);
                            imageList.addAll(initialSize, base64Images);
                            photoAdapter.notifyDataSetChanged();

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

        Button updatePlaceButton = findViewById(R.id.button_update_place);
        updatePlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recupere os valores inseridos nos campos de texto
                TextInputEditText nameEditText = findViewById(R.id.edit_text_name);
                TextInputEditText addressEditText = findViewById(R.id.edit_text_address);
                TextInputEditText descriptionEditText = findViewById(R.id.edit_text_description);
                RatingBar ratingBar = findViewById(R.id.ratingBar);
                float rating = ratingBar.getRating();

                // Crie um objeto Place com os valores inseridos
                Place place = new Place();
                place.setName(nameEditText.getText().toString());
                place.setAddress(addressEditText.getText().toString());
                place.setDescription(descriptionEditText.getText().toString());
                place.setRating(rating);
                place.setCreatedBy(userEmail);

                // Obtenha a lista de imagens em base64 da lista imageList
                List<String> base64Images = new ArrayList<>();
                for (String base64Image : imageList) {
                    base64Images.add(base64Image);
                    Log.d("CreatePlaceActivity", "Imagem base64: " + base64Image);
                }
                // Adicione a lista de imagens em base64 ao objeto Place
                place.setPhotos(base64Images);

                // Faça a chamada à API para salvar o lugar
                placeService = RestServiceGenerator.createService(PlaceService.class);
                Call<Place> call = placeService.updatePlace(idPlace, place);
                call.enqueue(new Callback<Place>() {
                    @Override
                    public void onResponse(Call<Place> call, Response<Place> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Lugar atualizado com sucesso", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                            resultIntent.putExtra("email", userEmail);
                            setResult(RESULT_OK, resultIntent);
                            startActivity(resultIntent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Erro ao atualizar o lugar", Toast.LENGTH_SHORT).show();
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
                photoAdapter.setImageList(imageList); // Atualiza a lista de imagens no adaptador
                photoAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Imagem removida", Toast.LENGTH_SHORT).show();
            }
        }

}
