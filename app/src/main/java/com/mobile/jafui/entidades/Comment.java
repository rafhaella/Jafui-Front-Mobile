package com.mobile.jafui.entidades;

import com.google.gson.annotations.SerializedName;
public class Comment {
    @SerializedName("id")
    private Long id;

    @SerializedName("comment")
    private String comment;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userName")
    private String userName;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("idPlace")
    private String idPlace;

    public Comment(Long id, String updatedCommentText) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(String idPlace) {
        this.idPlace = idPlace;
    }
    public Comment(Long id, String comment, String userEmail, String userName, String createdAt, String idPlace) {
        this.id = id;
        this.comment = comment;
        this.userEmail = userEmail;
        this.userName = userName;
        this.createdAt = createdAt;
        this.idPlace = idPlace;
    }
}

