package com.gaadi.neon.interfaces;

import com.gaadi.neon.Enumerations.ResponseCode;
import com.gaadi.neon.util.FileInfo;

import java.util.HashMap;
import java.util.List;

/**
 * @author princebatra
 * @version 1.0
 * @since 3/2/17
 */
public interface OnImageCollectionListener {

    void imageCollection(HashMap<String,List<FileInfo>> imageTagsCollection, ResponseCode responseCode);
    void imageCollection(List<FileInfo> imageCollection,ResponseCode responseCode);

}
