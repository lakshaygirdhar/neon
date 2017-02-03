package com.gaadi.neon.interfaces;

import android.os.Parcelable;

import com.gaadi.neon.Enumerations.CameraFacing;
import com.gaadi.neon.Enumerations.CameraOrientation;
import com.gaadi.neon.Enumerations.CameraType;

import java.io.Serializable;

/**
 * @author princebatra
 * @version 1.0
 * @since 25/1/17
 */
public interface ICameraParam extends IParam{

    CameraFacing getCameraFacing();

    CameraOrientation getCameraOrientation();

    boolean getFlashEnabled();

    boolean getCameraSwitchingEnabled();

    boolean getVideoCaptureEnabled();

    CameraType getCameraViewType();

    boolean cameraToGallerySwitchEnabled();

}
