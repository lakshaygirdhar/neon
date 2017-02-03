package com.gaadi.neon.activity.gallery;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.gaadi.neon.activity.ImageShow;
import com.gaadi.neon.adapter.GridFilesAdapter;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.SingletonClass;
import com.scanlibrary.R;
import com.scanlibrary.databinding.ActivityGridFilesBinding;

public class GridFilesActivity extends NeonBaseGalleryActivity {

    MenuItem textViewDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bindXml();
        String title = getIntent().getStringExtra(Constants.BucketName);
        if (title == null || title.length() <= 0) {
            title = "Files";
        }
        setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done_file, menu);
        textViewDone = menu.findItem(R.id.menu_next);
        textViewDone.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.menu_next) {
            if (SingletonClass.getSingleonInstance().getImagesCollection() == null ||
                    SingletonClass.getSingleonInstance().getImagesCollection().size() <= 0) {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }else {
                if(SingletonClass.getSingleonInstance().getImagesCollection() == null ||
                        SingletonClass.getSingleonInstance().getImagesCollection().size()<=0){
                    Toast.makeText(this,"No image selected",Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }
                if (!SingletonClass.getSingleonInstance().isNeutralEnabled()) {
                    Intent intent = new Intent(this, ImageShow.class);
                    startActivity(intent);
                    finish();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void bindXml() {
        ActivityGridFilesBinding binder = DataBindingUtil.setContentView(this, R.layout.activity_grid_files);
        GridFilesAdapter adapter = new GridFilesAdapter(this, getFileFromBucketId(getIntent().getStringExtra(Constants.BucketId)));
        binder.gvFolderPhotos.setAdapter(adapter);
    }


}
