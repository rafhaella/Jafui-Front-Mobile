package com.mobile.jafui.api;

import com.mobile.jafui.entidades.User;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @Headers({
            "Accept: application/json",
            "User-Agent: AppJaFui"
    })

    @GET("user/by_email/{email}")
    Call<User> getUserByEmail(@Path("email") String email);

    @GET("user")
    Call<List<User>> getUsers();

    @GET("user")
    Call<User> getUser(String userId);
    @POST("user/login")
    Call<User> userLogin(@Body RequestBody requestBodyLogin);
    @POST("user")
    Call<User> createUser(@Body RequestBody requestBody);

    @PUT("user/{id}")
    Call<User> updateUser(@Path("id") String userId, @Body User user);

    @DELETE("user/{id}")
    Call<Void> deleteUser(@Path("id") String userId);
}
