package com.mobile.jafui.api;

import com.mobile.jafui.entidades.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommentService {

    @Headers({
            "Accept: application/json",
            "User-Agent: AppJaFui"
    })

    @GET("place_comment/{id}")
    Call<Comment> getCommentById();

    @GET("place_comment/by_place/{idPlace}")
    Call<List<Comment>> getCommentByPlaceId(@Path("idPlace") String idPlace);

    @POST("place_comment")
    Call<Comment> addComment(@Body Comment  commentRequest);

    @PUT("place_comment/{id}")
    Call<Comment> updateComment(@Path("id") Long commentId, @Body Comment comment);

    @DELETE("place_comment/{id}")
    Call<Void> deleteComment(@Path("id") Long commentId);
}
