package com.mobile.jafui.activities.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.mobile.jafui.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.jafui.PlaceAdapter;
import com.mobile.jafui.api.PlaceService;
import com.mobile.jafui.api.RestServiceGenerator;
//import com.mobile.jafui.databinding.FragmentHomeBinding;
import com.mobile.jafui.entidades.Place;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class HomeFragment extends Fragment {

    private PlaceAdapter placeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        placeAdapter = new PlaceAdapter();
        recyclerView.setAdapter(placeAdapter);

        // Faz a chamada à API para buscar os lugares
        PlaceService placeService = RestServiceGenerator.createService(PlaceService.class);
        Call<List<Place>> call = placeService.getPlaces();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                if (response.isSuccessful()) {
                    List<Place> places = response.body();
                    if (places != null) {
                        placeAdapter.setPlaces(places);
                    }
                } else {
                    showSnackbar(getString(R.string.error_message));
                }
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                showSnackbar(getString(R.string.error_message));
            }
        });

        return root;
    }
    private void showSnackbar(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
    }
}