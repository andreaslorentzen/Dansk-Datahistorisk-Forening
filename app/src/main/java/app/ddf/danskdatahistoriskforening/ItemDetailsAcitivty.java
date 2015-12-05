package app.ddf.danskdatahistoriskforening;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by mathias on 05/12/15.
 */
public class ItemDetailsAcitivty extends AppCompatActivity implements View.OnClickListener {

    EditText titleView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Intent i = getIntent();
        Item chosenItem = i.getParcelableExtra("item");

        titleView = (EditText) findViewById(R.id.titleView);
        titleView.setText(chosenItem.getItemHeadline());


    }

    @Override
    public void onClick(View v){

    }
}
