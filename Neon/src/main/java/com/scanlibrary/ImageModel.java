package com.scanlibrary;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lakshaygirdhar on 24/12/15.
 */
public class ImageModel implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("imageName")
    private String imageName;
    @SerializedName("tagName")
    private String tagName;
    @SerializedName("imagePath")
    private String imagePath;
    @SerializedName("tagsModel")
    private ImageTagsModel tagsModel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ImageModel() {
        imageName = "IMG";
    }

    public ImageTagsModel getTagsModel() {
        return tagsModel;
    }

    public void setTagsModel(ImageTagsModel tagsModel) {
        this.tagsModel = tagsModel;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }


}
