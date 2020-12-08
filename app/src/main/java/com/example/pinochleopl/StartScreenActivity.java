/*
 ************************************************************
 * Name: Srijan Prasad Joshi
 * Project: Pinochle Java/Android
 * Class: OPL Fall 20
 * Date: 12/8/2020
 ************************************************************
*/

package com.example.pinochleopl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

//        save_game();
//
//        for(String ele: fileList()){
//            deleteFile(ele);
//        }
    }

    public void listener_btn_load_game(View v) {
        String file_name = spinner_file_names.getSelectedItem().toString();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(INTENT_SHOULD_LOAD_FROM_FILE, true);
        intent.putExtra(INTENT_FILE_NAME, file_name);

        startActivity(intent);
    }

    public void listener_btn_start_new_game(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(INTENT_SHOULD_LOAD_FROM_FILE, false);

        startActivity(intent);
    }

    private void save_game() {
        String file_name = "case_3";
        String case_1 = "Round: 1\n\nComputer:\n   Score: 0 / 0\n   Hand: QH KH QC KD JH XH QS 9H XC JS QD KC\n   Capture Pile:\n   Melds:\n\nHuman:\n   Score: 0 / 22\n   Hand: AH AC AS AD XH AC JH QC KD 9S 9D\n   Capture Pile: XC JC\n   Melds: 9H \n\nTrump Card: AH\nStock: KS QH 9C 9S XD KC JD JS KS QD XS 9C JC KH AD 9D AS XS XD JD QS\n\nNext Player: Human";
        String case_2 = "Round: 1\n\nComputer:\n   Score: 0 / 0\n   Hand: QH KH QC QD KD JH XH 9H XC JS QD KS\n   Capture Pile:\n   Melds:\n\nHuman:\n   Score: 0 / 22\n   Hand: AH AC AS AD XH AC JH QC QS KD 9D\n   Capture Pile: XC JC\n   Melds: 9H \n\nTrump Card: KH\nStock: AH KC QH 9C 9S XD KC JD JS 9S KS XS 9C JC AD 9D AS XS XD JD QS\n\nNext Player: Human";
        String case_3 = "Round: 1\n\nComputer:\n   Score: 0 / 107\n   Hand: QD KS 9H \n   Capture Pile: 9C JC JH QH KD AD KC AC 9S XS XD JD 9C JC XC QC XC QC QD KD JS AS\n   Melds:\n\nHuman:\n   Score: 0 / 110\n   Hand: QS JD XH\n   Capture Pile: 9D XD KC AC XS AS 9H XH KH AH 9S QS 9D AD KH AH JS KS JH QH \n   Melds:\n\nTrump Card: C\nStock: \n\nNext Player: Human";

        FileOutputStream fos = null;
        try {
            fos = (openFileOutput(file_name, Context.MODE_PRIVATE));
            fos.write(case_3.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
