package com.gaadi.neon.util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author lakshaygirdhar
 * @since 13-08-2016
 *
 */
public class PhotoParams implements Serializable {


    public static String DEFAULT_API = "";
    private String imageName;
    private CameraOrientation orientation;
    private int noOfPhotos, requestCode = 11;
    private FolderOptions folderOptions;
    private boolean tagEnabled;
    private boolean metaEnabled;
    private String uploadApi;
    private MODE mode = MODE.NEUTRAL;
    private ArrayList<?> imagePathList;
    private boolean enableCapturedReview;
    private boolean enableExtraBrightness;
    private boolean restrictedExtensionEnabled;
    private CameraFacing cameraFace;
    private boolean galleryFromCameraEnabled;

    public enum FolderOptions {
        PUBLIC_DIR, PUBLIC_DIR_DCIM, PUBLIC_DIR_SOCIAL, PUBLIC_DIR_ALL;
    }


    public enum MODE {CAMERA_PRIORITY, NEUTRAL, GALLERY_PRIORITY;}

    public enum CameraOrientation {
        LANDSCAPE, PORTRAIT, BOTH;
    }

    public enum CameraFacing {
        FRONT, BACK
    }

    public boolean isGalleryFromCameraEnabled() {
        return galleryFromCameraEnabled;
    }

    public void setGalleryFromCameraEnabled(boolean galleryFromCameraEnabled) {
        this.galleryFromCameraEnabled = galleryFromCameraEnabled;
    }

    public CameraFacing getCameraFace() {
        return cameraFace;
    }

    public void setCameraFace(CameraFacing cameraFace) {
        this.cameraFace = cameraFace;
    }

    public PhotoParams() {

    }


    public void setImageName(String imageName) {
        this.imageName = imageName;
    }


    public boolean getEnableCapturedReview() {
        return enableCapturedReview;
    }

    public void setEnableCapturedReview(Boolean enableCapturedReview) {
        this.enableCapturedReview = enableCapturedReview;
    }

    public String getImageName() {
        return imageName;
    }

    public ArrayList<?> getImagePathList() {
        return imagePathList;
    }

    public void setImagePathList(ArrayList<?> imagePathList) {
        this.imagePathList = imagePathList;
    }

    public MODE getMode() {
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    public static String getDefaultApi() {
        return DEFAULT_API;
    }

    public static void setDefaultApi(String defaultApi) {
        DEFAULT_API = defaultApi;
    }

    public int getNoOfPhotos() {
        return noOfPhotos;
    }

    public void setNoOfPhotos(int noOfPhotos) {
        this.noOfPhotos = noOfPhotos;
    }

    public CameraOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(CameraOrientation orientation) {
        this.orientation = orientation;
    }

    public FolderOptions getFolderOptions() {
        return folderOptions;
    }

    public void setFolderOptions(FolderOptions folderOptions) {
        this.folderOptions = folderOptions;
    }

    public boolean getTagEnabled() {
        return tagEnabled;
    }

    public void setTagEnabled(Boolean tagEnabled) {
        this.tagEnabled = tagEnabled;
    }

    public boolean getMetaEnabled() {
        return metaEnabled;
    }

    public void setMetaEnabled(Boolean metaEnabled) {
        this.metaEnabled = metaEnabled;
    }

    public String getUploadApi() {
        return uploadApi;
    }

    public void setUploadApi(String uploadApi) {
        this.uploadApi = uploadApi;
    }

//    public ArrayList<String> getImageList() {
//        return imageList;
//    }

//    public void setImageList (ArrayList<String> imageList) {
//        this.imageList = imageList;
//    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public boolean getEnableExtraBrightness() {
        return enableExtraBrightness;
    }

    public void setEnableExtraBrightness(Boolean enableExtraBrightness) {
        this.enableExtraBrightness = enableExtraBrightness;
    }

    public boolean isRestrictedExtensionEnabled() {
        return restrictedExtensionEnabled;
    }

    public void setRestrictedExtensionEnabled(boolean restrictedExtensionEnabled) {
        this.restrictedExtensionEnabled = restrictedExtensionEnabled;
    }
}
