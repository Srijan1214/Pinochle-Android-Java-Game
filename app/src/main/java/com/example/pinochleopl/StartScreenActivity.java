package com.example.pinochleopl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

public class StartScreenActivity extends AppCompatActivity {

    Spinner spinner_file_names;
    public final static String INTENT_SHOULD_LOAD_FROM_FILE = "com.example.pinochleopl.LOADING_FROM_FILE";
    public final static String INTENT_FILE_NAME = "com.example.pinochleopl.FILE_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        List<String> spinnerArray = Arrays.asList(fileList());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        spinner_file_names = (Spinner) findViewById(R.id.spinner_files_available);
        spinner_file_names.setAdapter(adapter);
//
//        for(String ele: fileList()){
//            deleteFile(ele);
//        }
    }

    public void listener_btn_load_game(View v) {
        String file_name = spinner_file_names.getSelectedItem().toString();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(INTENT_SHOULD_LOAD_FROM_FILE, true);
        intent.putExtra(INTENT_FILE_NAME, file_name);

        startActivity(intent);
    }

    public void listener_btn_start_new_game(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(INTENT_SHOULD_LOAD_FROM_FILE, false);

        startActivity(intent);
    }
}