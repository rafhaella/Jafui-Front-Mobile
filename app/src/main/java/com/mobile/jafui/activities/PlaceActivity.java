package com.mobile.jafui.activities;

import static com.mobile.jafui.R.*;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mobile.jafui.CommentAdapter;
import com.mobile.jafui.PhotosPagerAdapter;
import com.mobile.jafui.R;
import com.mobile.jafui.api.PlaceService;
import com.mobile.jafui.api.RestServiceGenerator;
import com.mobile.jafui.entidades.Comment;
import com.mobile.jafui.api.CommentService;
import com.mobile.jafui.entidades.Place;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaceActivity extends AppCompatActivity implements CommentAdapter.CommentDeleteListener, CommentAdapter.CommentEditListener {

    private EditText editComment;
    private Long selectedCommentId = null;
    private String idPlace, userEmail, userName;
    private Long id;
    private LocalDate createdAt;
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<String> commentList, commentListUser;
    private List<Long> commentListIds;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_place);

        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());
        Place place = (Place) intent.get().getSerializableExtra("place");

        LinearLayout placeBar = findViewById(R.id.placeBar);
        Button menuButton = placeBar.findViewById(R.id.menu_place);
        ImageButton buttonBack = findViewById(R.id.button_back);
        Button buttonSubmitComment = findViewById(R.id.button_submit_comment);
        TextView textViewPlaceName = findViewById(R.id.textNamePlace);
        TextView textViewPlaceAddress = findViewById(R.id.textAddressPlace);
        TextView textViewPlaceDescription = findViewById(R.id.textDescriptionPlace);
        TextView textViewNameUser = findViewById(R.id.text_NameUser);
        TextView textViewRatingNumber = findViewById(R.id.ratingNumber);
        RatingBar ratingBarPlace = findViewById(R.id.rating_place);
        ViewPager2 viewPager = findViewById(R.id.view_pager_photos);
        TabLayout tabLayout = findViewById(R.id.into_tab_layout);


        editComment = findViewById(R.id.edit_comment);
        commentRecyclerView = findViewById(R.id.comment_item);
        commentList = new ArrayList<>();
        commentListUser = new ArrayList<>();
        commentListIds = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentListIds, commentList, commentListUser);
        commentAdapter.setCommentDeleteListener(this);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerView.setAdapter(commentAdapter);
        CommentAdapter.CommentEditListener editListener = (commentId, commentText) -> {
            String newCommentText = editComment.getText().toString().trim();
            Long commentIdLong = Long.parseLong(commentId);
            updateComment(commentIdLong, newCommentText);
        };
        // Configure o CommentAdapter com o CommentEditListener
        commentAdapter = new CommentAdapter(commentListIds, commentList, commentListUser);
        commentAdapter.setCommentDeleteListener(this);
        commentAdapter.setCommentEditListener(editListener);
        commentRecyclerView.setAdapter(commentAdapter);


        menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(PlaceActivity.this, v);
            popupMenu.getMenuInflater().inflate(menu.place, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.editar_place) {
                    Intent editIntent = new Intent(PlaceActivity.this, EditPlaceActivity.class);
                    editIntent.putExtra("place", place);
                    editIntent.putExtra("userEmail", userEmail);
                    startActivity(editIntent);
                    return true;
                }
                if (itemId == R.id.excluir_place) {
                    new MaterialAlertDialogBuilder(PlaceActivity.this)
                            .setTitle("Excluir lugar")
                            .setMessage("Tem certeza de que deseja excluir este lugar?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Chamar o método para deletar o lugar
                                    deletePlace(place.getId());
                                }
                            })
                            .setNegativeButton("Não", null)
                            .show();
                    return true;
                    }
                return false;
            });
            popupMenu.show();
        });

        buttonBack.setOnClickListener(v -> onBackPressed());

        Log.d("PlaceActivity", "Conectou com a API");
        id = 0L;
        idPlace = place.getId();
        userEmail = place.getCreatedBy();
        userName = place.getCreatedBy();
        createdAt = LocalDate.now();

        buttonSubmitComment.setOnClickListener(v -> {
            String commentText = editComment.getText().toString().trim();
            if (TextUtils.isEmpty(commentText)) {
                Toast.makeText(PlaceActivity.this, "Por favor, insira um comentário", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedCommentId == null) {
                addComment(commentText);
            } else {
                updateComment(selectedCommentId, commentText);
            }
        });

        textViewPlaceName.setText(place.getName());
        textViewPlaceAddress.setText(place.getAddress());
        textViewPlaceDescription.setText(place.getDescription());
        textViewNameUser.setText(place.getCreatedBy());
        textViewRatingNumber.setText(String.valueOf(place.getRating()));
        ratingBarPlace.setRating(place.getRating());

        List<String> photos = place.getPhotos();

        PhotosPagerAdapter photosPagerAdapter = new PhotosPagerAdapter(photos);
        viewPager.setAdapter(photosPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            Drawable icon = ContextCompat.getDrawable(getApplicationContext(), drawable.selected_pager_dot);
            tab.setIcon(icon);
        }).attach();

        loadComments();
    }

    @Override
    public void onCommentEdit(String commentId, String commentText) {
        // Preencha o EditText com o texto do comentário
        editComment.setText(commentText);
        try {
            Long commentIdLong = Long.parseLong(commentId);
            updateComment(commentIdLong, commentText);
        } catch (NumberFormatException e) {
            Log.e("PlaceActivity", "Invalid comment ID: " + commentId, e);
            Toast.makeText(this, "Invalid comment ID", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onCommentDelete(String commentId) {
        try {
            Long commentIdLong = Long.parseLong(commentId);
            deleteComment(commentIdLong);
        } catch (NumberFormatException e) {
            Log.e("PlaceActivity", "Invalid comment ID: " + commentId, e);
            Toast.makeText(this, "Invalid comment ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadComments() {
        CommentService commentService = createCommentService();
        commentService.getCommentByPlaceId(idPlace).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful()) {
                    List<Comment> comments = response.body();
                    List<String> commentTexts = new ArrayList<>();
                    List<String> userNames = new ArrayList<>();
                    List<Long> commentIds = new ArrayList<>();

                    for (Comment comment : comments) {
                        commentTexts.add(comment.getComment());
                        userNames.add(comment.getUserName());
                        commentIds.add(comment.getId());
                    }
                    commentAdapter.setComments(commentIds, commentTexts, userNames);

                    Log.d("PlaceActivity", "Comentários carregados: " + commentIds + " " + commentTexts + " " + userNames);
                } else {
                    String errorMessage = "Erro ao obter comentários";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                            Log.d("PlaceActivity", "Erro: " + errorMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(PlaceActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(PlaceActivity.this, "Falha na chamada: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("PlaceActivity", "Erro: " + t.getMessage());
            }
        });
    }

    private void deleteComment(Long commentId) {
        CommentService commentService = createCommentService();
        Call<Void> call = commentService.deleteComment(commentId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PlaceActivity.this, "Comentário excluído com sucesso", Toast.LENGTH_SHORT).show();
                    loadComments(); // Recarrega os comentários após a exclusão
                } else {
                    String errorMessage = "Erro ao excluir comentário";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                            Log.d("PlaceActivity", "Erro: " + errorMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(PlaceActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PlaceActivity.this, "Erro na chamada com a API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addComment(String commentText) {
        commentText = editComment.getText().toString().trim();
        Comment request = new Comment(id, commentText, userEmail, userName, createdAt.toString(), idPlace);

        CommentService commentService = createCommentService();
        Call<Comment> call = commentService.addComment(request);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    editComment.setText("");
                    Log.d("PlaceActivity", "Comentário adicionado: " + response.body());
                    loadComments();
                } else {
                    String errorMessage = "Erro ao adicionar comentário";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                            Log.d("PlaceActivity", "Erro: " + errorMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(PlaceActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(PlaceActivity.this, "Erro na chamada com a API", Toast.LENGTH_SHORT).show();
                Log.e("PlaceActivity", "Erro na chamada com a API", t);
            }
        });
    }

    private void updateComment(Long id, String updatedCommentText) {
        Comment updatedComment = new Comment(id, updatedCommentText, userEmail, userName, createdAt.toString(), idPlace);
        Log.d("PlaceActivity", "Comentário a ser atualizado: " + id + " " + updatedComment);
        CommentService commentService = createCommentService();
        Call<Comment> call = commentService.updateComment(id, updatedComment);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    Comment updatedComment = response.body();
                    Log.d("PlaceActivity", "Comentário atualizado: " + id + " " + updatedComment);
                    Toast.makeText(PlaceActivity.this, "Comentário atualizado com sucesso", Toast.LENGTH_SHORT).show();
                    loadComments(); // Recarrega os comentários após a atualização
                } else {
                    String errorMessage = "Erro ao atualizar comentário";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                            Log.d("PlaceActivity", "Erro: " + errorMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(PlaceActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(PlaceActivity.this, "Erro na chamada com a API", Toast.LENGTH_SHORT).show();
                Log.e("PlaceActivity", "Erro na chamada com a API", t);
            }
        });
    }

    private void deletePlace(String placeId) {
        // Crie uma instância do serviço PlaceService
        PlaceService placeService = RestServiceGenerator.createService(PlaceService.class);

        // Faça a chamada à API para deletar o lugar
        Call<Void> call = placeService.deletePlace(placeId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Lugar deletado com sucesso
                    Toast.makeText(getApplicationContext(), "Lugar deletado com sucesso", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    resultIntent.putExtra("email", userEmail);
                    setResult(RESULT_OK, resultIntent);
                    startActivity(resultIntent);
                    finish();
                } else {
                    // Erro ao deletar o lugar
                    Toast.makeText(getApplicationContext(), "Erro ao deletar o lugar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Erro na chamada à API
                Toast.makeText(getApplicationContext(), "Erro na chamada à API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CommentService createCommentService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://lb-jafui-117865972.us-east-1.elb.amazonaws.com/jafui-comment-api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(CommentService.class);
    }
}