package com.mobile.jafui;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mobile.jafui.activities.PlaceActivity;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Long> commentIds;
    private List<String> commentTexts;
    private List<String> userNames;
    private CommentDeleteListener deleteListener;
    private CommentEditListener editListener;

    public CommentAdapter(List<Long> commentIds, List<String> commentTexts, List<String> userNames) {
        this.commentIds = commentIds;
        this.commentTexts = commentTexts;
        this.userNames = userNames;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Long id = commentIds.get(position);
        String commentText = commentTexts.get(position);
        String userName = userNames.get(position);
        holder.bind(id, commentText, userName);
    }

    @Override
    public int getItemCount() {
        return commentTexts.size();
    }

    public void setComments(List<Long> commentIds, List<String> commentTexts, List<String> userNames) {
        this.commentIds = commentIds;
        this.commentTexts = commentTexts;
        this.userNames = userNames;
        notifyDataSetChanged();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView userNameText, commentId;

        private EditText commentText;
        private Button editButton, deleteButton, updateButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentId = itemView.findViewById(R.id.commentId);
            commentText = itemView.findViewById(R.id.comment_text);
            userNameText = itemView.findViewById(R.id.user_name_text);
            editButton = itemView.findViewById(R.id.edit_comment);
            deleteButton = itemView.findViewById(R.id.delete_comment);
            updateButton = itemView.findViewById(R.id.update_comment);
        }

        public void bind(Long id, String comment, String userName) {
            commentId.setText(String.valueOf(id));
            commentText.setText(comment);
            userNameText.setText(userName);

            editButton.setOnClickListener(v -> {
                updateButton.setVisibility(View.VISIBLE);
                updateButton.setEnabled(true);
                editButton.setVisibility(View.INVISIBLE);
                commentText.requestFocus();
                commentText.setCursorVisible(true);
                commentText.setFocusableInTouchMode(true);
                commentText.setSelection(commentText.getText().length());
                InputMethodManager imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(commentText, InputMethodManager.SHOW_IMPLICIT);
            });

            updateButton.setOnClickListener(v -> {
                // Chamar o método de atualização do comentário
                if (editListener != null) {
                    String updatedCommentText = commentText.getText().toString();
                    Log.d("CommentAdapter", "Comentário: " + updatedCommentText);
                    editListener.onCommentEdit(String.valueOf(id), updatedCommentText);
                    updateButton.setVisibility(View.INVISIBLE);
                    updateButton.setEnabled(false);
                    editButton.setVisibility(View.VISIBLE);
                    commentText.setFocusableInTouchMode(false);
                    commentText.setCursorVisible(false);
                    InputMethodManager imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
                }
            });

            deleteButton.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(itemView.getContext())
                        .setTitle("Excluir Comentário")
                        .setMessage("Tem certeza de que deseja excluir este comentário?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (deleteListener != null) {
                                    int position = getAdapterPosition();
                                    if (position != RecyclerView.NO_POSITION) {
                                        String commentId = String.valueOf(commentIds.get(position));
                                        deleteListener.onCommentDelete(commentId);
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
            });
        }
    }

    public interface CommentDeleteListener {
        void onCommentDelete(String commentId);
    }
    public interface CommentEditListener {
        void onCommentEdit(String commentId, String updatedComment);
    }

    public void setCommentDeleteListener(CommentDeleteListener listener) {
        deleteListener = listener;
    }

    public void setCommentEditListener(CommentEditListener listener) {
        editListener = listener;
    }
}
