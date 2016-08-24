package com.scanlibrary;

import android.net.Uri;

/**
 * @author lakshaygirdhar
 * @since 13-08-2016
 *
 */
public interface IScanner {

    void onBitmapSelect(Uri uri);

    void onScanFinish(Uri uri);
}
