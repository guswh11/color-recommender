package edu.skku.color_recommender;

import android.util.Pair;

import java.util.ArrayList;

public class ClusterResult {

    private int k;
    private ArrayList<Integer> labels;
    private ArrayList<Pair> centers;

    public ClusterResult() {
    }

    public ClusterResult(int k, ArrayList<Integer> labels, ArrayList<Pair> centers) {
        this.k = k;
        this.labels = labels;
        this.centers = centers;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public ArrayList<Integer> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<Integer> labels) {
        this.labels = labels;
    }

    public ArrayList<Pair> getCenters() {
        return centers;
    }

    public void setCenters(ArrayList<Pair> centers) {
        this.centers = centers;
    }
}
