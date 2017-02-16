package com.pickle.pixel.dropboxtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class ListFiles extends AppCompatActivity {

    File[] listFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

        String tilesetName = this.getIntent().getStringExtra("TILESETNAME");
        Toast.makeText(this,"Given Name: " + tilesetName, Toast.LENGTH_LONG).show();

        listFiles = this.getFilesDir().listFiles();

        ListView listView = (ListView) findViewById(R.id.listFiles);

        ArrayAdapter<File> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listFiles );

        listView.setAdapter(arrayAdapter);

    }
}
