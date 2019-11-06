package edu.skku.color_recommender;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private ColorExtractor ce;

    ViewPager viewPager;
    TabLayout tabLayout;
    ResultAdapter resultAdapter;

    ArrayList<ResultItemFragment> resultFragment = new ArrayList<>();

    static {
        System.loadLibrary("opencv_java3");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    // ce.extractColor(bitmap);
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        ce = new ColorExtractor();

        //set margin
        viewPager.setClipToPadding(false);
            /*
            int dp_val = 10;
            float d = getResources().getDisplayMetrics().density;
            int margin = (int) (dp_val*d);
            */
        int margin = 30;
        viewPager.setPadding(margin, 0, margin, 0);
        viewPager.setPageMargin(margin/2);

        for(int i=0;i<1;i++){
            ResultItemFragment item = new ResultItemFragment();
            resultFragment.add(item);
        }

        resultAdapter = new ResultAdapter(getSupportFragmentManager(), resultFragment);
        viewPager.setAdapter(resultAdapter);

        tabLayout.setupWithViewPager(viewPager, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
