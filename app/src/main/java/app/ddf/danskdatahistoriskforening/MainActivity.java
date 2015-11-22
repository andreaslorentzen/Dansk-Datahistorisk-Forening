package app.ddf.danskdatahistoriskforening;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton addActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        addActivityButton = (FloatingActionButton) findViewById(R.id.fab);
        addActivityButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == addActivityButton){
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }
    }
}
