package com.mobile.jafui.entidades;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Place implements Serializable {
    private String id;
    private String name;
    private String address;
    private String description;
    private float rating;
    private List<String> photos;
    private List<String> reviews;

    private String createdBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public Place(){

    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", rating=" + rating +
                ", photos=" + photos +
                ", reviews=" + reviews +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (Float.compare(place.rating, rating) != 0) return false;
        if (!Objects.equals(id, place.id)) return false;
        if (!Objects.equals(name, place.name)) return false;
        if (!Objects.equals(address, place.address)) return false;
        if (!Objects.equals(description, place.description)) return false;
        if (!Objects.equals(createdBy, place.createdBy)) return false;
        if (!Objects.equals(photos, place.photos)) return false;
        return Objects.equals(reviews, place.reviews);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (rating != +0.0f ? Float.floatToIntBits(rating) : 0);
        result = 31 * result + (photos != null ? photos.hashCode() : 0);
        result = 31 * result + (reviews != null ? reviews.hashCode() : 0);
        return result;
    }
}
