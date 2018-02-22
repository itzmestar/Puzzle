package com.xenum.puzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import java.util.Random;
//import android.util.Log;
//import android.view.Gravity;

public class Game extends Activity {

	//private static final String TAG = "Puzzle" ;
	public static final String KEY_DIFFICULTY ="com.xenum.Puzzle.difficulty" ;
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;
	protected int puzzle[] = new int[3 * 3];
	protected int moves=0;//no. of moves
	protected int posx, posy;//Blank Position
	private PuzzleView puzzleView;
	
	//for continue option
	private static final String PREF_PUZZLE = "puzzle" ;
	private static final String PREF_MOVES = "moves" ;
	protected static final int DIFFICULTY_CONTINUE = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//Log.d(TAG, "onCreate" );
		int diff = getIntent().getIntExtra(KEY_DIFFICULTY,
				DIFFICULTY_EASY);
		//puzzle = getPuzzle(diff);
		puzzle = createPuzzle(getPuzzle(diff),diff);
		puzzleView = new PuzzleView(this);
		setContentView(puzzleView);
		puzzleView.requestFocus();
		// If the activity is restarted, do a continue next time
		getIntent().putExtra(KEY_DIFFICULTY, DIFFICULTY_CONTINUE);
	}

	/** Change the tile only if it's a valid move */
	protected void setTileIfValid(int i, int j) 
	{
		setTile(posx, posy, puzzle[j*3+i]);
		setTile(i,j,0);
		posx=i;posy=j;
	}
	protected void publishResultDialog()
	{
		new AlertDialog.Builder(this)
    	.setTitle(R.string.win_game_title)
    	.setMessage("You Won.")
    	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
                Game.this.finish();
           }
       })

    	.show();
	}
	
	/** Cache of used tiles */
	private final int used[][][] = new int[3][3][];
	/** Return cached used tiles visible from the given coords */
	protected int[] getUsedTiles(int x, int y) 
	{
		return used[x][y];
	}
	/** Compute the two dimensional array of used tiles */
	protected boolean checkResult()
	{
		if(puzzle[8]==0)
		{
			for(int i=0;i<8;i++)
				if(puzzle[i]!= i+1)
					return false;
			return true;
		}
		else 
			return false;
	}

/*	private final int PUZZLE[]={
			1,2,3,
			4,5,6,
			7,8,0
	};*/
	private final String easyPuzzle =
	/*	"821" +
		"364" +
		"570" ;*/
			"123"+
		    "485"+
			"760";
	private final String mediumPuzzle =
			"362" +
			"841" +
			"570" ;
	private final String hardPuzzle =
			"276" +
			"438" +
			"150" ;
		/** Given a difficulty level, come up with a new puzzle */
	private int[] getPuzzle(int diff) 
	{
		String puz;
		switch (diff) 
		{
			case DIFFICULTY_CONTINUE:
				puz = getPreferences(MODE_PRIVATE).getString(PREF_PUZZLE,
						easyPuzzle);
				moves = (int) getPreferences(MODE_PRIVATE).getLong(PREF_MOVES,
						moves);
				break;
			case DIFFICULTY_HARD:
				puz = hardPuzzle;
				moves=0;
				break;
			case DIFFICULTY_MEDIUM:
				puz = mediumPuzzle;
				moves=0;
				break;
			case DIFFICULTY_EASY:
			default:
				puz = easyPuzzle;
				moves=0;
				break;
		}
		return fromPuzzleString(puz);
	}
	/* Returns the Shuffled Array according to Difficulty level */
	private int[] createPuzzle(int temppuz[], int diff)
	{
		Random r=new Random();
    	//n=r.nextInt(3);
		int x,y;
		for(int k=-1; k<diff; k++)
		{
			do{
				x=r.nextInt(3);
				y=r.nextInt(3);
			}while(x==y);
			
			for(int i=0,temp;i<3;i++)
			{	
				//row swapping
				temp=temppuz[3*x+i];
				temppuz[3*x+i]=temppuz[3*y+i];
				temppuz[3*y+i]=temp;
				//col swapping
				temp=temppuz[i*3+x];
				temppuz[i*3+x]=temppuz[i*3+y];
				temppuz[i*3+y]=temp;
			}
		}
		if(diff==2)
		{
			x=r.nextInt(3);
			y=r.nextInt(3);
			for(int i=0,temp;i<3;i++)
			{
				temp=temppuz[3*x+i];
				temppuz[3*x+i]=temppuz[i*3+y];
				temppuz[i*3+y]=temp;
			}
		}
		if(diff!=-1)
		{
			for(int i=0;i<9;i++)
			{
				if(temppuz[i]==0)
				{
					temppuz[i]=temppuz[8];
					temppuz[8]=0;
					break;
				}
			}
		}
		return temppuz;
	}
		/** Convert an array into a puzzle string */
	static private String toPuzzleString(int[] puz) 
	{
		StringBuilder buf = new StringBuilder();
		for (int element : puz) 
		{
			buf.append(element);
		}
		return buf.toString();
	}
		/** Convert a puzzle string into an array */
	static protected int[] fromPuzzleString(String string) 
	{
		int[] puz = new int[string.length()];
		for (int i = 0; i < puz.length; i++) 
		{
			puz[i] = string.charAt(i) - '0' ;
		}
		return puz;
	}
		/** Return the tile at the given coordinates */
	private int getTile(int x, int y) 
	{
		return puzzle[y * 3 + x];
	}
		/** Change the tile at the given coordinates */
	private void setTile(int x, int y, int value) 
	{
		puzzle[y * 3 + x] = value;
	}
		/** Return a string for the tile at the given coordinates */
	protected String getTileString(int x, int y) 
	{
		int v = getTile(x, y);
		if (v == 0)
			return "" ;
		else
			return String.valueOf(v);
	}
		/**Check if there are any valid moves */
	protected boolean checkIfValidMove(int i, int j)//howKeypadOrError(int i, int j) 
	{
		if(j>-1&&j<3&&i>-1&&i<3)
		{
			if(posx==i)
			{
				if(posy==j+1||posy==j-1)
				{
					return true;
				}
			}
			else if(posy==j)
			{
				if(posx==i+1||posx==i-1)
				{
					return true;
				}
			}
		}
			return false;
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		//Log.d(TAG, "onPause" );
		//Music.stop(this);
	// Save the current puzzle
		getPreferences(MODE_PRIVATE).edit().putString(PREF_PUZZLE,
				toPuzzleString(puzzle)).commit();
		getPreferences(MODE_PRIVATE).edit().putLong(PREF_MOVES,
				moves).commit();
	}
}