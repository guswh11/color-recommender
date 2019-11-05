package edu.skku.color_recommender;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2Lab;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2RGB;

public class ColorExtractor {

    private final static float CROP_PORTION = 0.9f;     // Portion of remaining image after crop
    private final static float RESIZE_PORTION = 0.05f;  // Portion of remaining image after resize
    private final static int CANNY_KERNEL_SIZE = 1;     // Size of kernel to use canny edge detection
    private final static int CANNY_THRESHOLD = 15;      // Threshold for canny edge detection
    private final static int CANNY_RATIO = 3;           // Ratio to define upper threshold for canny edge detection
    private final static int COLOR_MAX_DISTANCE = 15;   // Maximum distance of colors be considered same
    private final static int CLUSTER_K = 3;             // Number of clusters which represent candidate colors
    private final static int TRUE = 255;                // Match true value to White(0xFFFFFF)
    private final static int FALSE = 0;                 // Match false value to Black(0x000000)

    public enum ReferenceColor {

        WHITE("white", Color.valueOf(Color.rgb(255, 255, 255))),
        BEIGE("beige", Color.valueOf(Color.rgb(231, 218, 205))),
        GREY("grey", Color.valueOf(Color.rgb(166, 179, 189))),
        SKY_BLUE("sky blue", Color.valueOf(Color.rgb(192, 219, 215))),
        PINK("pink", Color.valueOf(Color.rgb(246, 190, 201))),
        YELLOW("yellow", Color.valueOf(Color.rgb(228, 213, 112))),
        ORANGE("orange", Color.valueOf(Color.rgb(252, 119, 84))),
        BLACK("black", Color.valueOf(Color.rgb(0, 0, 0))),
        BROWN("brown", Color.valueOf(Color.rgb(82, 35, 24))),
        NAVY("navy", Color.valueOf(Color.rgb(35, 21, 77))),
        GREEN("green", Color.valueOf(Color.rgb(41, 64, 44))),
        RED("red", Color.valueOf(Color.rgb(160, 39, 39))),
        PURPLE("purple", Color.valueOf(Color.rgb(66, 36, 87))),;

        private String name;
        private Color color;

        ReferenceColor(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public Color getColor() {
            return color;
        }
    }

    public ColorExtractor() {
    }

    public String getColorName(Color color) {
        String name = new String();
        double minDistance = 1234567890.0;

        for (ReferenceColor refColor: ReferenceColor.values()) {
            double distance = Math.pow(refColor.getColor().red() - color.red(), 2)
                    + Math.pow(refColor.getColor().green() - color.green(), 2)
                    + Math.pow(refColor.getColor().blue() - color.blue(), 2);
            if (distance < minDistance) {
                minDistance = distance;
                name = refColor.getName();
            }
        }

        return name;
    }

    public ArrayList<Color> extractColor(Bitmap bitmap) {
        Log.d("COLOR_EXTRACT", "START");
        Mat img = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);

        Utils.bitmapToMat(bitmap, img);
        img = resize(img);

        // Detect obstruction(background) and remove it
        Mat bgMask = getBg(img);
        Core.bitwise_not(bgMask, bgMask);
        img = extractObject(img, bgMask);

        // Clustering image and get result
        ArrayList<Color> colors = getCluster(img);
        Log.d("COLOR_EXTRACT", "END");
        return colors;
    }

    private Mat resize(Mat img) {
        Mat resized;
        int cropRows, cropCols;
        int rowStart, rowEnd, colStart, colEnd;

        cropRows = (int) (img.rows() * CROP_PORTION);
        cropCols = (int) (img.cols() * CROP_PORTION);

        rowStart = (img.rows() - cropRows) / 2;
        rowEnd = rowStart + cropRows;
        colStart = (img.cols() - cropCols) / 2;
        colEnd = colStart + cropCols;

        resized = img.submat(rowStart, rowEnd, colStart, colEnd);
        Imgproc.resize(resized, resized, new Size(), RESIZE_PORTION, RESIZE_PORTION, Imgproc.INTER_LINEAR);

        return resized;
    }

    private Mat getBg(Mat img) {
        Mat bg = new Mat();
        Core.bitwise_or(getFloodFill(img), getGlobal(img), bg);
        return bg;
    }

    private Mat getFloodFill(Mat img) {
        Mat ff = new Mat(img.rows(), img.cols(), CvType.CV_8UC1, Scalar.all(0));
        Mat edge = new Mat();
        Mat imgCopy = new Mat();

        img.copyTo(imgCopy);
        Imgproc.cvtColor(imgCopy, imgCopy, COLOR_RGBA2RGB);
        Imgproc.cvtColor(imgCopy, imgCopy, COLOR_RGB2GRAY);
        Imgproc.blur(imgCopy, edge, new Size(CANNY_KERNEL_SIZE,CANNY_KERNEL_SIZE));
        Imgproc.Canny(edge, edge, CANNY_THRESHOLD, CANNY_THRESHOLD * CANNY_RATIO);

        // Conservative edge processing
        Mat tmp = new Mat();
        edge.copyTo(tmp);
        for (int i = 1; i < tmp.rows()-1; i++) {
            for (int j = 1; j < tmp.cols()-1; j++) {
                if (tmp.get(i, j)[0] > FALSE) {
                    edge.put(i-1, j-1, TRUE);
                    edge.put(i-1, j, TRUE);
                    edge.put(i-1, j+1, TRUE);
                    edge.put(i, j-1, TRUE);
                    edge.put(i, j+1, TRUE);
                    edge.put(i+1, j-1, TRUE);
                    edge.put(i+1, j, TRUE);
                    edge.put(i+1, j+1, TRUE);
                }
            }
        }
        for (int i = 0; i < edge.rows(); i++) {
            edge.put(i, 0, TRUE);
            edge.put(i, edge.cols()-1, TRUE);
        }
        for (int i = 0; i < edge.cols(); i++) {
            edge.put(0, i, TRUE);
            edge.put(edge.rows()-1, i, TRUE);
        }

        ArrayList<Pair> corners = new ArrayList<>();
        corners.add(new Pair(1, 1));
        corners.add(new Pair(1, imgCopy.cols()-2));
        corners.add(new Pair(imgCopy.rows()-2, 1));
        corners.add(new Pair(imgCopy.rows()-2, imgCopy.cols()-2));

        Stack<Pair> adjacents = new Stack<>();
        ArrayList<Pair> evaluated = new ArrayList<>();
        for (Pair corner: corners) {
            int cornerRow = (Integer) corner.first;
            int cornerCol = (Integer) corner.second;

            if (edge.get(cornerRow, cornerCol)[0] > FALSE) {
                continue;
            } else if (evaluated.contains(corner)) {
                continue;
            }
            evaluated.add(corner);
            ff.put(cornerRow, cornerCol, TRUE);
            pushAdjacents(getAdjacents(edge, cornerRow, cornerCol), adjacents, evaluated);
            while (!adjacents.isEmpty()) {
                Pair adjacent = adjacents.pop();
                int adjRow = (Integer) adjacent.first;
                int adjCol = (Integer) adjacent.second;
                if (Double.compare(edge.get(adjRow, adjCol)[0], FALSE) == 0) {
                    ff.put(adjRow, adjCol, TRUE);
                    pushAdjacents(getAdjacents(edge, adjRow, adjCol), adjacents, evaluated);
                }
            }
        }

        return ff;
    }

    private ArrayList<Pair> getAdjacents(Mat mat, int row, int col) {
        ArrayList<Pair> adjacents = new ArrayList<>();
        int rowMax = mat.rows()-1;
        int colMax = mat.cols()-1;

        if (row > 1) {
            adjacents.add(new Pair(row-1, col));
        }
        if (row < rowMax) {
            adjacents.add(new Pair(row+1, col));
        }
        if (col > 1) {
            adjacents.add(new Pair(row, col-1));
        }
        if (col < colMax) {
            adjacents.add(new Pair(row, col+1));
        }

        return adjacents;
    }

    private void pushAdjacents(ArrayList<Pair> adjacents, Stack stack, ArrayList<Pair> evaluated) {
        for (Pair adjacent: adjacents) {
            if (!evaluated.contains(adjacent)) {
                evaluated.add(adjacent);
                stack.push(adjacent);
            }
        }
    }

    private Mat getGlobal(Mat img) {
        Mat global = new Mat(img.rows(), img.cols(), CvType.CV_8UC1);
        Mat imgCopy = new Mat();

        img.copyTo(imgCopy);
        Imgproc.cvtColor(imgCopy, imgCopy, COLOR_RGBA2RGB);
        Imgproc.cvtColor(imgCopy, imgCopy, COLOR_RGB2Lab);

        ArrayList<Pair> corners = new ArrayList<>();
        corners.add(new Pair(0, 0));
        corners.add(new Pair(0, imgCopy.cols()-1));
        corners.add(new Pair(imgCopy.rows()-1, 0));
        corners.add(new Pair(imgCopy.rows()-1, imgCopy.cols()-1));

        for (int i = 0; i < imgCopy.rows(); i++) {
            for (int j = 0; j < imgCopy.cols(); j++) {
                for (Pair corner: corners) {
                    double norm = Math.sqrt(
                            Math.pow(Math.abs(
                                    imgCopy.get(i, j)[0] - imgCopy.get((Integer) corner.first, (Integer) corner.second)[0]), 2)
                            + Math.pow(Math.abs(
                                    imgCopy.get(i, j)[1] - imgCopy.get((Integer) corner.first, (Integer) corner.second)[1]), 2)
                            + Math.pow(Math.abs(
                                    imgCopy.get(i, j)[2] - imgCopy.get((Integer) corner.first, (Integer) corner.second)[2]), 2)
                    );

                    if (norm < COLOR_MAX_DISTANCE) {
                        global.put(i, j, TRUE);
                        break;
                    } else {
                        global.put(i, j, FALSE);
                    }
                }
            }
        }

        return global;
    }

    private Mat extractObject(Mat img, Mat bg) {
        Mat objectMat = new Mat(img.rows(), img.cols(), CvType.CV_8UC4, Scalar.all(0));

        for (int i = 0; i < img.rows(); i++) {
            for (int j = 0; j < img.cols(); j++) {
                if (bg.get(i, j)[0] > FALSE) {
                    objectMat.put(i, j, img.get(i, j));
                }
            }
        }

        return objectMat;
    }

    private ArrayList<Color> getCluster(Mat img) {
        ArrayList<Color> colors = new ArrayList<>();
        Mat labels = new Mat();
        Mat centers = new Mat();
        Mat img1D32F = new Mat();

        Mat img1D = img.reshape(1, img.cols()*img.rows());
        img1D.convertTo(img1D32F, CvType.CV_32F, 1.0/255.0);

        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Core.kmeans(img1D32F, CLUSTER_K, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);

        centers.convertTo(centers, CvType.CV_8U, 255.0);
        for (int i = 0; i < centers.rows(); i++) {
            int r = (int) centers.get(i, 0)[0];
            int g = (int) centers.get(i, 1)[0];
            int b = (int) centers.get(i, 2)[0];
            colors.add(Color.valueOf(Color.rgb(r, g, b)));
        }
        Collections.reverse(colors);

        return colors;
    }
}
