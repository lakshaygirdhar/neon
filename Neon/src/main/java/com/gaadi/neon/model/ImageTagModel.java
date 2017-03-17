package com.gaadi.neon.model;

import java.io.Serializable;

/**
 * @author lakshaygirdhar
 * @version 1.0
 * @since 15/11/16
 */

public class ImageTagModel implements Serializable {
    private String tagName;
    private boolean mandatory;
    private String tagId;
    private int numberOfPhotos;

    public ImageTagModel() {
    }

    public ImageTagModel(String _tagName, String _tagId, boolean _mandatory) {
        tagName = _tagName;
        tagId = _tagId;
        mandatory = _mandatory;
    }

    public ImageTagModel(String _tagName, String _tagId, boolean _mandatory,int _numberOfPhotos) {
        tagName = _tagName;
        tagId = _tagId;
        mandatory = _mandatory;
        numberOfPhotos = _numberOfPhotos;
    }


    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public int getNumberOfPhotos() {
        return numberOfPhotos;
    }

    public void setNumberOfPhotos(int numberOfPhotos) {
        this.numberOfPhotos = numberOfPhotos;
    }
}
