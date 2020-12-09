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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

public class StartScreenActivity extends AppCompatActivity {

	// The reference to the spinner view object
    Spinner spinner_file_names;

	// The static string that the GameActivity uses to get the boolean value of weather to load from file or not.
    public final static String INTENT_SHOULD_LOAD_FROM_FILE = "com.example.pinochleopl.LOADING_FROM_FILE";
	
	// The static string that the GameActivity uses to get file name from the loaded intent
    public final static String INTENT_FILE_NAME = "com.example.pinochleopl.FILE_NAME";

    /* *********************************************************************
    Function Name: onCreate 
    Purpose: The life-cycle method called when the activity is finished initializing.
				It finds all the file that have already been saved and tells adds them to the spinner.
    Parameters:
				Bundle savedInstanceState, a Bundle object, used by the  classes higher up in the hierarchy.
    Return Value: None
    Local Variables:
    			spinnerArray, a list of strings, containing the names of the files the app as access to.
				adapter, an ArrayAdapter, that adds the names of files to the spinner.
    Assistance Received: none
    ********************************************************************* */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        List<String> spinnerArray = Arrays.asList(fileList());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        spinner_file_names = (Spinner) findViewById(R.id.spinner_files_available);
        spinner_file_names.setAdapter(adapter);

    }

    /* *********************************************************************
    Function Name: listener_btn_load_game 
    Purpose: To listen to the load game button, and make the GameActivity load the game from the file and start
				the game
    Parameters:
				View v, the view object of the button that is clicked
    Return Value: None
    Local Variables:
    			file_name, the name of the file gotten from the spinner in string format
    			intent, an Intent object, needed to start the new activity
    Assistance Received: none
    ********************************************************************* */
    public void listener_btn_load_game(View v) {
        String file_name = spinner_file_names.getSelectedItem().toString();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(INTENT_SHOULD_LOAD_FROM_FILE, true);
        intent.putExtra(INTENT_FILE_NAME, file_name);

        startActivity(intent);
    }

    /* *********************************************************************
    Function Name: listener_btn_start_new_game 
    Purpose: To listen to the start new game button and make the GameActivity start a new game
    Parameters:
				View v, the view object of the button that is clicked
    Return Value: None
    Local Variables:
    			intent, an Intent object, needed to start the new activity
    Assistance Received: none
    ********************************************************************* */
    public void listener_btn_start_new_game(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(INTENT_SHOULD_LOAD_FROM_FILE, false);

        startActivity(intent);
    }
}
