package edu.skku.color_recommender;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ResultAdapter extends FragmentPagerAdapter {
    private ArrayList <ResultItemFragment> fragments = new ArrayList<>();

    public ResultAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public ResultAdapter(@NonNull FragmentManager fm, ArrayList<ResultItemFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
