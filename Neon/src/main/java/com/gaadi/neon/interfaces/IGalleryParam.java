package com.gaadi.neon.interfaces;

import com.gaadi.neon.Enumerations.GalleryType;

/**
 * @author princebatra
 * @version 1.0
 * @since 25/1/17
 */
public interface IGalleryParam extends IParam{

    boolean selectVideos();

    GalleryType getGalleryViewType();

    boolean enableFolderStructure();

    boolean galleryToCameraSwitchEnabled();

    boolean isRestrictedExtensionJpgPngEnabled();

}
