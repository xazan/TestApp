package ua.zp.eldorado.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;


public class MainActivity extends FragmentActivity implements AdapterView.OnItemClickListener {

    public static final String LOG_TAG = "debugTest";
//    public static final String PATH_TO_IMAGES = "/testapp";
    public static final String PATH_TO_THUMBNAIL = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp/";
    public static final int THUMBNAIL_SIZE = 100;
    GridViewAdapter mGridViewAdapter;
    public static Vector<String> mFilePath = new Vector<>();
    public static Vector<String> mFileThumbPath = new Vector<>();


    @Override
    protected void onStart() {
        super.onStart();
        updateGridView();
    }

    private void updateGridView() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String pathToImages = preferences.getString(getString(R.string.prefs_image_dir_key), getString(R.string.prefs_image_dir_default));
        GetImagesFromExternalDataStorage getImagesFromExternalDataStorage = new GetImagesFromExternalDataStorage();
        getImagesFromExternalDataStorage.execute(pathToImages);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_layout);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        mGridViewAdapter = new GridViewAdapter(this);
        gridView.setAdapter(mGridViewAdapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearThumbsDirectory(PATH_TO_THUMBNAIL);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mGridViewAdapter = new GridViewAdapter(this);
    }

    private void clearThumbsDirectory(String pathToThumbnail) {
        File directory = new File(pathToThumbnail);
        if (directory.isDirectory()) {
            for (File thumbsFile : directory.listFiles()) {
                thumbsFile.delete();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("image_index", Integer.toString(position));
        mGridViewAdapter = new GridViewAdapter(this);
        startActivity(intent);
    }

    /**
     * Fetch images from external storage
     */
    public class GetImagesFromExternalDataStorage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            File workDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + params[0]);
            if (workDirectory.isDirectory()) {

                final File[] files = workDirectory.listFiles();
                int i = 0;

                for (final File file : files) {

                    String fileName = file.getName().toString();
                    String fileNameShort = fileName.substring(0, fileName.lastIndexOf("."));
                    String fileExt = fileName.substring(fileName.lastIndexOf("."));
                    String imageThumbFileName = PATH_TO_THUMBNAIL.concat(fileNameShort).concat("_tmb").concat(fileExt);
                    File imageThumbFile = new File(imageThumbFileName);
                    if (!imageThumbFile.exists()) {
                        try {
                            imageThumbFile.createNewFile();
                            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file.getAbsolutePath().toString()), THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] bitmapdata = baos.toByteArray();
                            FileOutputStream fos = new FileOutputStream(imageThumbFile);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mFilePath.add(file.getAbsolutePath().toString());
                    mFileThumbPath.add(imageThumbFile.getAbsolutePath().toString());
                    publishProgress();
                    i++;
                }
            } else {
                Log.d(LOG_TAG, "Wrong path to images: " + workDirectory.getAbsolutePath().toString());
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            mGridViewAdapter.notifyDataSetChanged();
        }


    }

    /**
     * GridView Adapter
     */
    public class GridViewAdapter extends BaseAdapter {
        private Context mContext;

        public GridViewAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mFileThumbPath.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView image;

            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_layout, parent, false);

            image = (ImageView) convertView.findViewById(R.id.image);
            image.setImageBitmap(BitmapFactory.decodeFile(mFileThumbPath.get(position)));
            return convertView;
        }




    }

}
