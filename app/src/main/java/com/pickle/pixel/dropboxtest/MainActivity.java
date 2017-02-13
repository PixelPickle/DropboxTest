package com.pickle.pixel.dropboxtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String ACCESS_TOKEN = "Z4zFAHp5MtAAAAAAAAAAC143TZkWjqDNC3_Ib6zLMdk87WPjlVctA3y8b4AqTZuB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
            DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

            // Get current account info
            FullAccount account = client.users().getCurrentAccount();
            System.out.println(account.getName().getDisplayName());

            ListFolderResult result = client.files().listFolder("");
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    System.out.println(metadata.getPathLower());
                }

                if (!result.getHasMore()) {
                    break;
                }

                result = client.files().listFolderContinue(result.getCursor());

                // Upload "test.txt" to Dropbox
                try (InputStream in = new FileInputStream("test.txt")) {
                    FileMetadata metadata = client.files().uploadBuilder("/test.txt")
                            .uploadAndFinish(in);
                }catch(IOException e){

                }
            }
        }catch(DbxException e){

        }
    }

}
