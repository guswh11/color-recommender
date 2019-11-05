package edu.skku.color_recommender;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ColorExtractor ce;
    private ImageView iv;

    static {
        System.loadLibrary("opencv_java3");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.blue)).getBitmap();
                    ArrayList<Color> result = ce.extractColor(bitmap);
                    iv.setBackgroundColor(Color.rgb(result.get(0).red(), result.get(0).green(), result.get(0).blue()));
                    Log.d("COLOR_EXTRACT", "" + result.get(0));
                    Log.d("COLOR_EXTRACT", "" + result.get(1));
                    Log.d("COLOR_EXTRACT", "" + result.get(2));
                    Log.d("COLOR_EXTRACT", ce.getColorName(result.get(0)));
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
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.main_iv);
        ce = new ColorExtractor();
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
