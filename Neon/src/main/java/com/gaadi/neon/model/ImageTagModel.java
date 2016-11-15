package com.gaadi.neon.model;

/**
 * @author lakshaygirdhar
 * @version 1.0
 * @since 15/11/16
 */

public class ImageTagModel
{
    private String tagName;
    private boolean mandatory;
    private String tagId;

    public String getTagName()
    {
        return tagName;
    }

    public void setTagName(String tagName)
    {
        this.tagName = tagName;
    }

    public boolean isMandatory()
    {
        return mandatory;
    }

    public void setMandatory(boolean mandatory)
    {
        this.mandatory = mandatory;
    }

    public String getTagId()
    {
        return tagId;
    }

    public void setTagId(String tagId)
    {
        this.tagId = tagId;
    }
}
