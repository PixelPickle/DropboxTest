package com.pickle.pixel.dropboxtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dropbox.chooser.android.DbxChooser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int CHOOSER_SPRITE_SHEET = 0;
    static final int CHOOSER_TILESET = 1;

    private static final String APP_KEY = "ziz3befif8nlh3p";

    private static List<String> validSpriteTypes = new ArrayList<String>() {{ add("bmp");}};
    private static List<String> validTilesetTypes = new ArrayList<String>() {{ add("xml");}};


    private DbxChooser mChooser;

    private String tilesetName;
    private String tilesetPath;
    private android.net.Uri containerTileset;
    private android.net.Uri containerSpriteSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChooser = new DbxChooser(APP_KEY);

        Button mChooserButton;
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

        mChooserButton = (Button) findViewById(R.id.btnCreateTileset);
        mChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTileset();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSER_SPRITE_SHEET && resultCode == Activity.RESULT_OK) {
            // Get the result
            DbxChooser.Result result = new DbxChooser.Result(data);

            // Log the file's information
            Log.d("main", "Name of File: " + result.getName());
            Log.d("main", "Link to selected file: " + result.getLink());

            // Validate the file extension
            if( validateSpriteURI(result.getName()) ) {
                containerSpriteSheet = result.getLink();
                EditText text = (EditText) findViewById(R.id.editSpriteSheet);
                text.setText(result.getName());
            }else{
                Toast.makeText(this,"Invalid File Type", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CHOOSER_TILESET && resultCode == Activity.RESULT_OK) {
            // Get the result
            DbxChooser.Result result = new DbxChooser.Result(data);

            // Log the file's information
            Log.d("main", "Name of File: " + result.getName());
            Log.d("main", "Link to selected file: " + result.getLink());

            // Validate the file extension
            if( validateTilesetURI(result.getName())) {
                containerTileset = result.getLink();
                EditText text = (EditText) findViewById(R.id.editTileSheet);
                text.setText(result.getName());
            }else{
                Toast.makeText(this,"Invalid File Type", Toast.LENGTH_LONG).show();
            }
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

    private void createTileset(){

        EditText editText = (EditText) findViewById(R.id.editTilesetName);

        if( editText.getText().length() == 0 ){
            Toast.makeText(this,"Name Required", Toast.LENGTH_LONG).show();
            return;
        }

        if( containerSpriteSheet == null ){
            Toast.makeText(this,"Sprite Sheet Not Selected", Toast.LENGTH_LONG).show();
            return;
        }

        if( containerTileset == null ){
            Toast.makeText(this,"Tileset File Not Selected", Toast.LENGTH_LONG).show();
            return;
        }

        tilesetName = editText.getText().toString();
        downloadFiles();
    }

    private void downloadFiles() {

        tilesetPath = tilesetName.replace(" ", "");
        tilesetPath = this.getFilesDir() + "/" + tilesetPath;
        Log.d("main", "File Path: " + tilesetPath);

        downloadSpriteSheet();
    }

    private void finishedDownloads(){
        Intent intent = new Intent(this, ListFiles.class);
        intent.putExtra("TILESETNAME", tilesetName);
        startActivity(intent);
    }

    private void downloadSuccessful(String filename){
        if( filename.equals(tilesetPath + ".bmp")){
            downloadTileset();
        }else if ( filename.equals(tilesetPath + ".xml")){
            finishedDownloads();
        }else{
            Log.e("main", "Unknown File Downloaded: " + filename );
        }
    }

    private void downloadSpriteSheet(){

        final DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.execute(containerSpriteSheet.toString(), tilesetPath + ".bmp");
    }

    private void downloadTileset(){

        final DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.execute(containerTileset.toString(), tilesetPath + ".xml");
    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private String filename;

        private DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            filename = sUrl[1];
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(filename);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null){
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(context, "Downloaded: " + filename.substring(filename.lastIndexOf('/') + 1),
                        Toast.LENGTH_SHORT).show();
                downloadSuccessful(filename);
            }
        }
    }

    private boolean validateSpriteURI( String fileName ){
        return validateFileType( fileName, validSpriteTypes );
    }

    private boolean validateTilesetURI( String fileName ){
        return validateFileType( fileName, validTilesetTypes );
    }

    private boolean validateFileType (String fileName, List<String> validTypes ){
        String fileExtension =  fileName.substring(fileName.lastIndexOf('.') + 1);
        Log.d("main", "Chosen file's extension: " + fileExtension);
        return validTypes.contains(fileExtension);
    }
}
