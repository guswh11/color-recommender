package edu.skku.color_recommender;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ColorExtractor {

    private Context context;
    private ArrayList<ArrayList<Integer>> samples;
    private ArrayList<String> labels;

    public ColorExtractor(Context context) {
        this.context = context;
        loadColorMatrix();
    }

    public void loadColorMatrix() {

        samples = new ArrayList<>();
        labels = new ArrayList<>();

        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open("sample.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while(line != null) {
                ArrayList sample = new ArrayList();
                line = line.substring(1, line.length()-1);
                line = line.trim().replaceAll(" +", " ");
                sample.add(new Integer(line.split(" ")[0]));
                sample.add(new Integer(line.split(" ")[1]));
                sample.add(new Integer(line.split(" ")[2]));
                samples.add(sample);
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream is = am.open("label.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while(line != null) {
                labels.add(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Color> extractColor(Uri uri) {
        ArrayList<Color> colorCandidates = new ArrayList();
        Mat img = Imgcodecs.imread(uri.toString(), Imgcodecs.IMREAD_COLOR);
        img = resize(img);

        // Detect obstruction(background and skin) and remove it
        Mat bgMask = getBg(img);
        Mat bgSkin = getSkin(img);
        Mat mask = new Mat();
        Core.bitwise_or(bgMask, bgSkin, mask);
        img = extractObject(img);

        // Clustering image and get result
        ClusterResult best = getCluster(img);
        colorCandidates = getColorFromCluster(best);

        return colorCandidates;
    }

    private Mat resize(Mat img) {
        Mat resized = new Mat();
        return resized;
    }

    private Mat getBg(Mat img) {
        Mat bg = new Mat();
        return bg;
    }

    private Mat getSkin(Mat img) {
        Mat skin = new Mat();
        return skin;
    }

    private Mat extractObject(Mat img) {
        Mat objectMat = new Mat();
        return objectMat;
    }

    private ClusterResult getCluster(Mat img) {
        ClusterResult result = new ClusterResult();
        return result;
    }

    private ArrayList<Color> getColorFromCluster(ClusterResult result) {
        ArrayList colorCandidates = new ArrayList();
        return colorCandidates;
    }

    public ArrayList<ArrayList<Integer>> getSamples() {
        return samples;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }
}
