package com.gaadi.neon.util;

import java.io.Serializable;

/**
 * Created by Lakshay
 * @since 27-02-2015.
 *
 */
public class FileInfo implements Serializable {

    private String filePath;
    private FILE_TYPE type;
    private String fileName;
    private String displayName;
    private boolean selected;
    private SOURCE source;
    private int fileCount;

    public FileInfo() {
        this.selected = false;
    }

    public SOURCE getSource() {
        return source;
    }

    public void setSource(SOURCE source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = super.equals(o);
        if (equals) {
            return true;
        }
        if (o instanceof FileInfo) {
            if (this.filePath.equals(((FileInfo) o).getFilePath())) {
                return true;
            }
        }
        return false;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FILE_TYPE getType() {
        return type;
    }

    public void setType(FILE_TYPE type) {
        this.type = type;
    }

    public enum FILE_TYPE {IMAGE, FOLDER}

    public enum SOURCE {PHONE_CAMERA, PHONE_GALLERY, SERVER}
}
