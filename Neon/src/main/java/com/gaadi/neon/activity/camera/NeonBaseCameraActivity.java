package com.gaadi.neon.activity.camera;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.gaadi.neon.Enumerations.CameraFacing;
import com.gaadi.neon.Enumerations.CameraOrientation;
import com.gaadi.neon.Enumerations.CameraType;
import com.gaadi.neon.activity.BaseActivity;
import com.gaadi.neon.activity.NeonBaseActivity;
import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.util.Constants;

/**
 * @author princebatra
 * @version 1.0
 * @since 25/1/17
 */
public abstract class NeonBaseCameraActivity extends NeonBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
