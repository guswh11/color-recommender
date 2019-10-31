package edu.skku.color_recommender;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class DetectActivity extends AppCompatActivity {
    ImageView pictureView;

    Uri fileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);

        fileUri = getIntent().getParcelableExtra("imageUri");
        pictureView = findViewById(R.id.pictureView);
        pictureView.setImageURI(fileUri);
    }
}
