package com.gaadi.neon.util;

import android.os.Handler;

import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.interfaces.IGalleryParam;
import com.gaadi.neon.interfaces.INeutralParam;
import com.gaadi.neon.interfaces.IParam;
import com.gaadi.neon.interfaces.SetOnImageCollectionListener;
import com.gaadi.neon.model.ImageTagModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author princebatra
 * @version 1.0
 * @since 1/2/17
 */
public class SingletonClass {

    private static SingletonClass singleonInstance;
    private ArrayList<FileInfo> imagesCollection;
    private ICameraParam cameraParam;
    private IGalleryParam galleryParam;
    private boolean neutralEnabled;
    private INeutralParam neutralParam;
    private IParam genericParam;
    private static boolean clearInstance;

    public void scheduleSinletonClearance(){
        clearInstance = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSingleonInstance();
            }
        }, 10000);
    }


    public SetOnImageCollectionListener getImageResultListener() {
        return imageResultListener;
    }

    public void setImageResultListener(SetOnImageCollectionListener imageResultListener) {
        this.imageResultListener = imageResultListener;
    }

    private SetOnImageCollectionListener imageResultListener;

    public IParam getGenericParam() {
        if (galleryParam != null)
            return galleryParam;
        else if (cameraParam != null)
            return cameraParam;
        else
            return neutralParam;
    }

    public boolean isNeutralEnabled() {
        return neutralEnabled;
    }

    public void setNeutralEnabled(boolean neutralEnabled) {
        this.neutralEnabled = neutralEnabled;
    }


    public INeutralParam getNeutralParam() {
        return neutralParam;
    }

    public void setNeutralParam(INeutralParam neutralParam) {
        this.neutralParam = neutralParam;
    }

    public ArrayList<FileInfo> getImagesCollection() {
        return imagesCollection;
    }

    private SingletonClass() {
    }

    public synchronized static SingletonClass getSingleonInstance() {
        if (singleonInstance == null || clearInstance) {
            singleonInstance = new SingletonClass();
            clearInstance = false;
        }
        return singleonInstance;
    }

    public boolean checkImagesAvailableForTag(ImageTagModel tagModel) {
        if (imagesCollection == null || imagesCollection.size() <= 0) {
            return false;
        }
        for (int i = 0; i < imagesCollection.size(); i++) {
            if (imagesCollection.get(i).getFileTag() != null && imagesCollection.get(i).getFileTag().getTagId().equals(tagModel.getTagId()) &&
                    imagesCollection.get(i).getFileTag().getTagName().equals(tagModel.getTagName())) {
                return true;
            }
        }
        return false;
    }

    public boolean checkImageAvailableForPath(FileInfo fileInfo) {
        if (imagesCollection == null || imagesCollection.size() <= 0) {
            return false;
        }
        for (int i = 0; i < imagesCollection.size(); i++) {
            if (imagesCollection.get(i).getFilePath().equalsIgnoreCase(fileInfo.getFilePath())) {
                return true;
            }
        }
        return false;
    }

    public boolean removeFromCollection(FileInfo fileInfo) {
        if (imagesCollection == null || imagesCollection.size() <= 0) {
            return true;
        }
        for (int i = 0; i < imagesCollection.size(); i++) {
            if (imagesCollection.get(i).getFilePath().equals(fileInfo.getFilePath())) {
                return imagesCollection.remove(i) != null;
            }
        }
        return true;
    }

    public boolean putInImageCollection(FileInfo fileInfo) {
        if (imagesCollection == null) {
            imagesCollection = new ArrayList<>();
        }
        return imagesCollection.add(fileInfo);
    }

    public boolean removeFromCollection(int position) {
        if (imagesCollection == null || imagesCollection.size() <= 0) {
            return true;
        }
        return imagesCollection.remove(position) != null;
    }

    public HashMap<String, List<FileInfo>> getFileHashMap() {
        if (imagesCollection == null || imagesCollection.size() <= 0) {
            return null;
        }
        HashMap<String, List<FileInfo>> hashMap = new HashMap<>();

        for (int i = 0; i < imagesCollection.size(); i++) {
            FileInfo singleFile = imagesCollection.get(i);
            if(hashMap.containsKey(singleFile.getFileTag().getTagId())){
                hashMap.get(singleFile.getFileTag().getTagId()).add(singleFile);
            }else{
                List<FileInfo>singleTagFiles = new ArrayList<>();
                singleTagFiles.add(singleFile);
                hashMap.put(singleFile.getFileTag().getTagId(),singleTagFiles);
            }
        }
        return hashMap;
    }

    public void setGalleryParam(IGalleryParam params) {
        this.galleryParam = params;
    }

    public IGalleryParam getGalleryParam() {
        return galleryParam;
    }

    public ICameraParam getCameraParam() {
        return cameraParam;
    }

    public void setCameraParam(ICameraParam cameraParam) {
        this.cameraParam = cameraParam;
    }
}
