package edu.skku.color_recommender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    ViewPager viewPager;
    TabLayout tabLayout;
    ResultAdapter resultAdapter;

    ArrayList<ResultItemFragment> resultFragment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

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

        for(int i=0;i<4;i++){
            ResultItemFragment item = new ResultItemFragment();
            resultFragment.add(item);
        }

        resultAdapter = new ResultAdapter(getSupportFragmentManager(), resultFragment);
        viewPager.setAdapter(resultAdapter);

        tabLayout.setupWithViewPager(viewPager, true);
    }
}
