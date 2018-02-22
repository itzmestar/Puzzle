package com.xenum.puzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import android.content.DialogInterface;
import android.content.Intent;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class Puzzle extends Activity implements OnClickListener 
{
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    // Set up click listeners for all the buttons
    	View continueButton = this.findViewById(R.id.continue_button);
    	continueButton.setOnClickListener(this);
    	View newButton = this.findViewById(R.id.new_button);
    	newButton.setOnClickListener(this);
    	View aboutButton = this.findViewById(R.id.about_button);
    	aboutButton.setOnClickListener(this);
    	View exitButton = this.findViewById(R.id.exit_button);
    	exitButton.setOnClickListener(this);
    }
    	
    
    public void onClick(View v) 
    {
    	switch (v.getId()) 
    	{
    		case R.id.continue_button:
    			startGame(Game.DIFFICULTY_CONTINUE);
    			break;
    		case R.id.about_button:
    			Intent i = new Intent(this, About.class);
    			startActivity(i);
    			break;
        	case R.id.exit_button:
        		finish();
        		break;
        	case R.id.new_button:
				openNewGameDialog();
				break;
    	}
    }
    	// private static final String TAG = "Puzzle" ;
    	  //  /** difficulty level dialog */
    private void openNewGameDialog() 
    {
       	new AlertDialog.Builder(this)
        	.setTitle(R.string.new_game_title)
        	.setItems(R.array.difficulty,
        			new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialoginterface,
        					int i) {
    	    				startGame(i);
    	    			}
    	    	})
        .show();
    }
    private void startGame(int i) 
    {
        // Start game here...
       	Intent intent = new Intent(Puzzle.this, Game.class);
       	intent.putExtra(Game.KEY_DIFFICULTY, i);
       	startActivity(intent);
    }
}
