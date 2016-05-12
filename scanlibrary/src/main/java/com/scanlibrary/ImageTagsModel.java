package com.scanlibrary;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lakshaygirdhar on 7/10/15.
 */
public class ImageTagsModel implements Serializable {

    @SerializedName("tagName")
    private String tag_name;

    @SerializedName("parent")
    private String parent_name;

    @SerializedName("tagType")
    private String image_type;

    @SerializedName("tagOrder")
    private String order;

    @SerializedName("tagID")
    private String tagID;

    @SerializedName("isRequired")
    private String mandatory;

    @SerializedName("updated_time")
    private String updated_time;

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public String getTagId() {
        return tagID;
    }

    public void setTagId(String tagId) {
        this.tagID = tagId;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }

    public String getTag_name() {
        return tag_name;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public void setTag_name(String tag_name) {

        this.tag_name = tag_name;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageTagsModel that = (ImageTagsModel) o;

        return tag_name.equals(that.tag_name);

    }

    @Override
    public int hashCode() {
        return tag_name.hashCode();
    }
}
