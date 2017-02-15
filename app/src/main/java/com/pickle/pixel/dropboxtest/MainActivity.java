package com.pickle.pixel.dropboxtest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dropbox.chooser.android.DbxChooser;

public class MainActivity extends AppCompatActivity {

    static final int CHOOSER_SPRITE_SHEET = 0;
    static final int CHOOSER_TILESET = 1;

    private static final String APP_KEY = "ziz3befif8nlh3p";

    private Button mChooserButton;
    private DbxChooser mChooser;

    private android.net.Uri containerTileset;
    private android.net.Uri containerSpriteSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChooser = new DbxChooser(APP_KEY);

        mChooserButton = (Button) findViewById(R.id.btnImportSprite);
        mChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChooser.forResultType(DbxChooser.ResultType.DIRECT_LINK)
                        .launch(MainActivity.this, CHOOSER_SPRITE_SHEET);
            }
        });

        mChooserButton = (Button) findViewById(R.id.btnImportTile);
        mChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChooser.forResultType(DbxChooser.ResultType.PREVIEW_LINK)
                        .launch(MainActivity.this, CHOOSER_TILESET);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSER_SPRITE_SHEET && resultCode == Activity.RESULT_OK) {
            DbxChooser.Result result = new DbxChooser.Result(data);
            Log.d("main", "Link to selected file: " + result.getLink());

            // Handle the result
            containerSpriteSheet = result.getLink();
            Log.d("main", "Name of File: " + result.getName());
            EditText text = (EditText) findViewById(R.id.editSpriteSheet);
            text.setText(result.getName());
        } else if (requestCode == CHOOSER_TILESET && resultCode == Activity.RESULT_OK) {
            DbxChooser.Result result = new DbxChooser.Result(data);
            Log.d("main", "Link to selected file: " + result.getLink());

            // Handle the result
            containerTileset = result.getLink();
            Log.d("main", "Name of File: " + result.getName());
            EditText text = (EditText) findViewById(R.id.editTileSheet);
            text.setText(result.getName());
        } else if ( (requestCode == CHOOSER_TILESET || requestCode == CHOOSER_SPRITE_SHEET ) &&
                resultCode == Activity.RESULT_CANCELED) {
                // Cancelled by the user.
                Log.d("main", "Cancelled by User");
        } else if (requestCode == CHOOSER_TILESET || requestCode == CHOOSER_SPRITE_SHEET ){
                // Failed
                Log.d("main", "Failed for some reason");
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
