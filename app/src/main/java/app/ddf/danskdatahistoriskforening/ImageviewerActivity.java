package app.ddf.danskdatahistoriskforening;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ImageviewerActivity extends AppCompatActivity implements View.OnClickListener {
    Button backButton;
    Intent result;
    ArrayList<Integer> removedImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageviewer);

        result = new Intent();
        removedImages = new ArrayList<>();

        backButton = (Button) findViewById(R.id.imageview_back_button);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == backButton){
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        result.putExtra("removedImages", removedImages);
        setResult(Activity.RESULT_OK, result);

        super.onBackPressed();
    }
}
