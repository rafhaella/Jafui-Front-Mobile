package com.mobile.jafui.api;

import com.mobile.jafui.entidades.Place;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PlaceService {

    @Headers({
            "Accept: application/json",
            "User-Agent: AppJaFui"
    })

    @GET("places")
    Call<List<Place>> getPlaces();
    @POST("places")
    Call<Place> createPlace(@Body Place place);

    @PUT("places/{id}")
    Call<Place> updatePlace(@Path("id") String placeId, @Body Place place);

    @DELETE("places/{id}")
    Call<Void> deletePlace(@Path("id") String placeId);
}
