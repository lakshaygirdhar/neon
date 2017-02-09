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

//// TODO Review Lakshay : should be renamed to OnImageCollectionListener
public interface SetOnImageCollectionListener {

    void imageCollection(HashMap<String,List<FileInfo>> imageTagsCollection, ResponseCode responseCode);
    void imageCollection(List<FileInfo> imageCollection,ResponseCode responseCode);

}
