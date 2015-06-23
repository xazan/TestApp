package ua.zp.eldorado.testapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
        mViewPager.setOffscreenPageLimit(1);
    }


    public static class CustomPagerAdapter extends FragmentStatePagerAdapter {

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
            ((ImageView) imageView).setImageBitmap(BitmapFactory.decodeFile(MainActivity.mFilePath.elementAt(fragVal)));

            return layoutView;
        }
    }

}
