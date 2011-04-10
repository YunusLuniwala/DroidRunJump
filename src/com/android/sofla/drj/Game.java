package com.android.sofla.drj;

import java.util.Random;

import com.android.sofla.drj.Droid;
import com.android.sofla.drj.Pothole;


import android.graphics.Canvas;
import android.graphics.Paint;


public class Game {

	//
	// pothole resources
	//
	final int MAX_potholes = 10;
	float MIN_POTHOLE_WIDTH = 100.0f;
	float MAX_POTHOLE_WIDTH = 210.0f;
	Pothole [] potholes;
	
	// keep track of last spawned pothole
	Pothole lastPothole;
	
	long spawnPotholeTicks;
	final long SPAWN_POTHOLE_TIME = 750;
	
	//
	// Droid/Player resources
	//
	Droid droid = new Droid(this);
	final float groundY = 400;
	final float groundHeight = 20;

	//
	// player input flag
	//
	boolean playerTap;

	//
	// possible game states
	//
	final int GAME_MENU = 0;
	final int GAME_READY = 1;
	final int GAME_PLAY = 2;
	final int GAME_OVER = 3;

	int gameState;

	//
	// game menu message
	//
	long tapToStartTime;
	boolean showTapToStart;
	
	//
	// get ready message
	//
	final int SHOW_GET_READY = 0;
	final int SHOW_GO = 1;
	
	long getReadyGoTime;
	int getReadyGoState;
	
	//
	// game over message
	//
	long gameOverTime;

	//
	// shared paint objects for drawing
	//
	Paint textPaint;
	Paint clearPaint;
	Paint greenPaint;
	
	//
	// random number generator
	//
	Random rng;
	
	//
	// display dimensions
	//
	int width;
	int height;
	
	public Game() {
		
		//
		// allocate resources needed by game
		//
		greenPaint = new Paint();
		greenPaint.setAntiAlias(true);
		greenPaint.setARGB(255, 0, 255, 0);
		greenPaint.setFakeBoldText(true);		
		greenPaint.setTextSize(42.0f);

		clearPaint = new Paint();
		clearPaint.setARGB(255, 0, 0, 0);
		clearPaint.setAntiAlias(true);
		
		rng = new Random();		
		
		droid = new Droid(this);
		
		potholes = new Pothole[MAX_potholes];
		for (int i=0; i<MAX_potholes; i++) {
			potholes[i] = new Pothole(this);
		}
		
		//
		// initialize the game
		//
		resetGame();
	}

	public void setScreenSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void run(Canvas canvas) {
		switch (gameState) {
		case GAME_MENU:
			gameMenu(canvas);
			break;
		case GAME_READY:
			gameReady(canvas);
			break;
		case GAME_PLAY:
			gamePlay(canvas);
			break;
		case GAME_OVER:
			gameOver(canvas);
			break;
		}
	}
	
	public void doTouch() {
		playerTap = true;
	}


	private void resetGame() {
		tapToStartTime = System.currentTimeMillis();
		showTapToStart = true;
		
		playerTap = false;
		
		spawnPotholeTicks = System.currentTimeMillis();
		
		droid.reset();
		
		for (Pothole p : potholes) {
			p.reset();
		}
		
		lastPothole = null;
		
		gameState = GAME_MENU;
		
		getReadyGoState = SHOW_GET_READY;
		getReadyGoTime = 0;		
	}
	
	public void initGameOver() {
		gameState = GAME_OVER;
		gameOverTime = System.currentTimeMillis();		
	}

	private void gameOver(Canvas canvas) {

		// clear screen
		canvas.drawRect(0, 0, width, height, clearPaint);
		
		canvas.drawText("GAME OVER", width/3, height/2, greenPaint);

		long now = System.currentTimeMillis() - gameOverTime;
		if (now > 2000) {
			resetGame();
		}
	}

	private void gamePlay(Canvas canvas) {
		// clear screen
		canvas.drawRect(0, 0, width, height, clearPaint);

		// draw ground
		canvas.drawRect(0, groundY, width, groundY+groundHeight, greenPaint);

		droid.update();
		droid.draw(canvas);

		for (Pothole p : potholes) {
			if (p.alive) {
				p.update();
				p.draw(canvas);
			}
		}

		spawnPothole();
	}

	private void gameReady(Canvas canvas) {
		
		long now;
		
		// clear screen
		canvas.drawRect(0, 0, width, height, clearPaint);
		
		switch (getReadyGoState) {
		case SHOW_GET_READY:
			canvas.drawText("GET READY", (width/2)-100.0f, height/2, greenPaint);
			now = System.currentTimeMillis() - getReadyGoTime;
			if (now > 1000) {
				getReadyGoTime = System.currentTimeMillis();
				getReadyGoState = SHOW_GO;
			}
			break;
		case SHOW_GO:
			canvas.drawText("GO!", (width/2)-40.0f, height/2, greenPaint);
			now = System.currentTimeMillis() - getReadyGoTime;
			if (now > 500) {				
				gameState = GAME_PLAY;
			}
			break;
		}
		
		// draw ground
		canvas.drawRect(0, groundY, width, groundY+groundHeight, greenPaint);
		
		droid.draw(canvas);					
	}

	private void gameMenu(Canvas canvas) {

		canvas.drawRect(0, 0, width, height, clearPaint);

		canvas.drawText("DROID-RUN-JUMP", (width/3)-40.0f, 100.0f, greenPaint);

		if (playerTap) {
			gameState = GAME_READY;
			playerTap = false;
			getReadyGoState = SHOW_GET_READY;
			getReadyGoTime = System.currentTimeMillis();

			// spawn 1st chasm so player sees something at start of game
			potholes[0].spawn(0);
			lastPothole = potholes[0];
		}

		long now = System.currentTimeMillis() - tapToStartTime;
		if (now > 550) {
			tapToStartTime = System.currentTimeMillis();
			showTapToStart = !showTapToStart;
		}

		if (showTapToStart) {
			canvas.drawText("TAP TO START", width/3, height-100.0f, greenPaint);
		}			
	}

	public float random(float a) {
		return rng.nextFloat() * a;
	}

	public float random(float a, float b) {		
		return Math.round(a + (rng.nextFloat() * (b - a)));
	}

	void spawnPothole() {
		long now = System.currentTimeMillis() - spawnPotholeTicks;

		if (now > SPAWN_POTHOLE_TIME) {

			// randomly determine whether or not to spawn a new pothole
			if ((int)random(10) > 2) {
				
				//
				// find an available pothole to use
				//
				
				for (Pothole p : potholes) {
					
					if (p.alive) {
						continue;
					}
					
					//
					// by default all new potholes start just beyond
					// the right side of the display
					//
					
					float xOffset = 0.0f;
					
					//
					// if the last pothole is alive then use its width to adjust
					// the position of the new pothole if the last pothole
					// is too close to the right of the screen. this is to
					// give the player some breathing room.
					//
					
					if (lastPothole.alive) {
						
						float tmp = lastPothole.x + lastPothole.w;
						
						if (tmp > width) {
							tmp = tmp - width;
							xOffset = tmp + random(10.0f);
						}
						else {
							tmp = width - tmp;								
							if (tmp < 20.0f) {
								xOffset = tmp + random(10.0f);
							}
						}
					}

					p.spawn(xOffset);						
					lastPothole = p;						
					break;
				}
			}

			spawnPotholeTicks = System.currentTimeMillis();
		}
	}
}
