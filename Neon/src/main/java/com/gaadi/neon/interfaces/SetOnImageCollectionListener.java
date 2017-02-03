package com.gaadi.neon.interfaces;

import com.gaadi.neon.util.FileInfo;

import java.util.HashMap;
import java.util.List;

/**
 * @author princebatra
 * @version 1.0
 * @since 3/2/17
 */
public interface SetOnImageCollectionListener {

    void imageCollection(HashMap<String,List<FileInfo>> imageTagsCollection);
    void imageCollection(List<FileInfo> imageCollection);

}
