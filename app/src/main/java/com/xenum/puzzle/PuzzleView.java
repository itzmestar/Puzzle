package com.xenum.puzzle;
import android.os.Bundle;
import android.os.Parcelable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
//import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class PuzzleView extends View 
{
	//private static final String TAG = "Puzzle" ;
	private final Game game;
	private static final String SELX = "selX" ;
	private static final String MOVES = "moves" ;
	private static final String SELY = "selY" ;
	private static final String VIEW_STATE = "viewState" ;
	private static final int ID = 42;
	
	public PuzzleView(Context context) 
	{
		super(context);
		this.game = (Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		setId(ID);
	}
	@Override
	protected Parcelable onSaveInstanceState() 
	{
		Parcelable p = super.onSaveInstanceState();
		//Log.d(TAG, "onSaveInstanceState" );
		Bundle bundle = new Bundle();
		
		bundle.putInt(SELX, selX);
		bundle.putInt(SELY, selY);
		//Log.d(TAG, "onSaveInstanceState: X="+selX+"Y="+selY );
		bundle.putInt(MOVES, game.moves);
		//Log.d(TAG, "onSaveInstanceState: Moves="+game.moves );
		bundle.putParcelable(VIEW_STATE, p);
		return bundle;
	}
	@Override
	protected void onRestoreInstanceState(Parcelable state) 
	{
		//Log.d(TAG, "onRestoreInstanceState" );
		Bundle bundle = (Bundle) state;
		select(bundle.getInt(SELX), bundle.getInt(SELY));
		game.moves=bundle.getInt(MOVES);
		//Log.d(TAG, "onRestoreInstanceState"+game.moves );
		super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));
		return;
	}	

	private float width; // width of one tile
	private float height; // height of one tile
	private int selX; // X index of selection
	private int selY; // Y index of selection
	private int flag=0; // 
	private final Rect selRect = new Rect();
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
		width = w / 3f;
		height = h / 4f;
		getRect(selX, selY, selRect);
		//Log.d(TAG, "onSizeChanged: width " + width + ", height "
		//+ height);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		// Draw the background...
		Paint background = new Paint();
		background.setColor(getResources().getColor(
				R.color.puzzle_background));
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		// Draw the board...
		// Define colors for the grid lines
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));

		// Draw the major grid lines
		for (int i = 0; i < 4; i++) 
		{
			canvas.drawLine(0, i * height, getWidth(), i * height,
					dark);
			canvas.drawLine(0, i * height + 1, getWidth(), i * height
					+ 1, hilite);
			canvas.drawLine(i * width, 0, i * width, getHeight()-height, dark);
			canvas.drawLine(i * width + 1, 0, i * width + 1,
					getHeight()-height, hilite);
		}
		// Draw the numbers...
		// Define color and style for numbers
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(
				R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height);
		foreground.setTextAlign(Paint.Align.CENTER);
		// Draw the number in the center of the tile
		FontMetrics fm = foreground.getFontMetrics();
		// Centering in X: use alignment (and X at midpoint)
		float x = width / 2;
		// Centering in Y: measure ascent/descent first
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		for (int i = 0; i < 3; i++) 
		{
			for (int j = 0; j < 3; j++) 
			{
				canvas.drawText(this.game.getTileString(i, j), i
						* width + x, j * height + y, foreground);
				//canvas.drawText(String.valueOf(k), i
					//	* width + x, j * height + y, foreground);
			}
		}
		//foreground.setTextAlign(Paint.Align.LEFT);
		foreground.setTextSize(height * 0.50f);
		canvas.drawText("MOVES",
				 width , 3 * height + y, foreground);
		canvas.drawText(String.valueOf(game.moves), 2
				* width + x, 3 * height + y, foreground);
		// Draw the selection...
		//Log.d(TAG, "selRect=" + selRect);
		Paint selected = new Paint();
		selected.setColor(getResources().getColor(
				R.color.puzzle_selected));
		if(flag==0)
		{	
			for(int i=0;i<3;i++)
				for(int j=0;j<3;j++)
					if(game.puzzle[j*3+i]==0)
					{
						select(i,j);
						game.posx=i;
						game.posy=j;
					
					}
			flag++;
		}
		canvas.drawRect(selRect, selected);
	/*
		// Draw the hints...
		// Pick a hint color based on #moves left
		Paint hint = new Paint();
		int c[] = { getResources().getColor(R.color.puzzle_hint_0),
				getResources().getColor(R.color.puzzle_hint_1),
				getResources().getColor(R.color.puzzle_hint_2), };
		Rect r = new Rect();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				int movesleft = 9 - game.getUsedTiles(i, j).length;
				if (movesleft < c.length) {
					getRect(i, j, r);
					hint.setColor(c[movesleft]);
					canvas.drawRect(r, hint);
				}
			}
		}
	// Number is not valid for this tile
	//Log.d(TAG, "setSelectedTile: invalid: " + tile);
	//startAnimation(AnimationUtils.loadAnimation(game,
	//R.anim.shake));
	// Draw the selection...*/
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		//Log.d(TAG, "onKeyDown: keycode =" + keyCode + ", event="
			//	+ event);
		switch (keyCode) 
		{
			case KeyEvent.KEYCODE_DPAD_UP:
				callFun(selX, selY + 1);
				//setSelectedTile(selX, selY + 1);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				callFun(selX, selY - 1);
				//setSelectedTile(selX, selY - 1);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				callFun(selX + 1, selY);
				//setSelectedTile(selX + 1, selY);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				callFun(selX - 1, selY);
				//setSelectedTile(selX - 1, selY);
				break;
			default:
				return super.onKeyDown(keyCode, event);
		}
		return true;
	}
	
	private void select(int x, int y) 
	{
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 2);
		selY = Math.min(Math.max(y, 0), 2);
		getRect(selX, selY, selRect);
		invalidate(selRect);
	}
	
	private void getRect(int x, int y, Rect rect) 
	{
		rect.set((int) (x * width), (int) (y * height), (int) (x
		* width + width), (int) (y * height + height));
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		callFun((int) (event.getX() / width),
				(int) (event.getY() / height));
			//select((int) (event.getX() / width),
				//	(int) (event.getY() / height));
		
		//game.showKeypadOrError(selX, selY);
		//Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
		return true;
	}
	
	public void callFun(int i,int j)
	{
		if (game.checkIfValidMove(i,j))
		{
			select (i,j);
			setSelectedTile(i,j);
		}
	}
	//public void setSelectedTile(int tile) {
	public void setSelectedTile(int i,int j) 
	{
		game.setTileIfValid(i, j);
		invalidate();
		game.moves++;
		if(game.checkResult())
		{
			game.publishResultDialog();
		}
		// Number is not valid for this tile
		//Log.d(TAG, "setSelectedTile: invalid: " + tile);
	}		
}