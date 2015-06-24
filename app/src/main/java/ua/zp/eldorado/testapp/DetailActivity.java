package ua.zp.eldorado.testapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class DetailActivity extends FragmentActivity {

    static final int ITEMS = MainActivity.mFilePath.size();
    CustomPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager);

        Intent intent = getIntent();
        index = Integer.parseInt(intent.getStringExtra("image_index"));

        mCustomPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.myviewpager);
        mViewPager.setAdapter(mCustomPagerAdapter);
        mViewPager.setCurrentItem(index);
        mViewPager.setOffscreenPageLimit(0);
    }


    public static class CustomPagerAdapter extends FragmentPagerAdapter {

        private final LruCache<Integer, Fragment> mCache;

        public CustomPagerAdapter(FragmentManager fm) {
            super(fm);
            mCache = new LruCache<Integer, Fragment>(10);
        }

        @Override
        public Fragment getItem(int position) {
//            return ArrayListFragment.newInstance(position);
            return ImageFragment.init(position);
//            return mCache.get(position);
        }

        @Override
        public int getCount() {
            return ITEMS;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    public static class ImageFragment extends Fragment {
        int fragVal;

        public static ImageFragment init(int val) {
            ImageFragment imageFragment = new ImageFragment();
            Bundle args = new Bundle();
            args.putInt("val", val);
            imageFragment.setArguments(args);
            return imageFragment;
        }

        private static int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        private static Bitmap decodeSampledBitmapFromResource(String pathToFile,
                                                             int reqWidth, int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathToFile, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(pathToFile, options);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle args = getArguments();
            fragVal = args != null ? args.getInt("val") : 1;
            Log.d("debugTest", Integer.toBinaryString(fragVal));
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View layoutView = inflater.inflate(R.layout.detail_item_layout, container, false);
            View imageView = layoutView.findViewById(R.id.image_full);
//            ((ImageView) imageView).setImageBitmap(BitmapFactory.decodeFile(MainActivity.mFilePath.elementAt(fragVal)));
            ((ImageView) imageView).setImageBitmap(decodeSampledBitmapFromResource(MainActivity.mFilePath.elementAt(fragVal), 500, 500));

            return layoutView;
        }
    }

}
