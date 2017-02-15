package com.pickle.pixel.dropboxtest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.dropbox.chooser.android.DbxChooser;

public class MainActivity extends AppCompatActivity {

    static final int DBX_CHOOSER_REQUEST = 0;  // You can change this if needed

    private static final String APP_KEY = "ziz3befif8nlh3p";

    private Button mChooserButton;
    private DbxChooser mChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChooser = new DbxChooser(APP_KEY);

        mChooserButton = (Button) findViewById(R.id.chooser_button);
        mChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChooser.forResultType(DbxChooser.ResultType.PREVIEW_LINK)
                        .launch(MainActivity.this, DBX_CHOOSER_REQUEST);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DBX_CHOOSER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                DbxChooser.Result result = new DbxChooser.Result(data);
                Log.d("main", "Link to selected file: " + result.getLink());

                // Handle the result
                Log.d("main", "Name of File: " + result.getName());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Cancelled by the user.
                Log.d("main", "Cancelled by User");
            } else {
                // Failed
                Log.d("main", "Failed for some reason");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
