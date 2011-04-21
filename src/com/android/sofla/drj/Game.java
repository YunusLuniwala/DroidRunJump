package com.android.sofla.drj;

import java.util.Random;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;


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
	
	long spawnPotholeTime;
	final long SPAWN_POTHOLE_TIME = 750;
	
	//
	// Droid/Player resources
	//
	Droid droid;
	final float groundY = 400;
	final float groundHeight = 20;
	
	//
	// Pastry
	//
	Pastry pastry;
	long spawnPastryTime;
	final long SPAWN_PASTRY_TIME = 750;

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
	final int GAME_PAUSE = 4;

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
	// track time between save games
	//
	long saveGameTime;
	
	//
	// hiscore
	//
	int highScore;
	int curScore;
	
	long scoreTime;
	final long SCORE_TIME = 100;
	
	final int SCORE_DEFAULT = 500;
	final int SCORE_INC = 5;
	final int SCORE_PASTRY_BONUS = 200;

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
			potholes[i] = new Pothole(i, this);
		}
		
		pastry = new Pastry(this);
		
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
		case GAME_PAUSE:
			gamePause(canvas);
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

		droid.reset();
		
		spawnPotholeTime = System.currentTimeMillis();
		for (Pothole p : potholes) {
			p.reset();
		}
		
		pastry.reset();
		spawnPastryTime = System.currentTimeMillis();
		
		lastPothole = null;
		
		gameState = GAME_MENU;
		lastGameState = gameState;
		
		getReadyGoState = SHOW_GET_READY;
		getReadyGoTime = 0;
		
		curScore = 0;
	}
	
	public void initGameOver() {
		
		gameState = GAME_OVER;
		gameOverTime = System.currentTimeMillis();
		
		// update high score
		if (curScore > highScore) {
			highScore = curScore;
		}
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

		for (Pothole p : potholes) {
			if (p.alive) {
				p.update();
				p.draw(canvas);
			}
		}
		
		if (pastry.alive) {
			pastry.update();
			pastry.draw(canvas);
		}
		
		droid.update();
		droid.draw(canvas);

		spawnPothole();
		spawnPastry();
		
		doScore(canvas);		
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
				scoreTime = System.currentTimeMillis();
			}
			break;
		}
		
		// draw blank score
		canvas.drawText("SCORE: 0", 0, 40, greenPaint);
		
		// draw ground
		canvas.drawRect(0, groundY, width, groundY+groundHeight, greenPaint);
		
		droid.draw(canvas);					
	}

	private void gameMenu(Canvas canvas) {

		canvas.drawRect(0, 0, width, height, clearPaint);

		canvas.drawText("DROID-RUN-JUMP", (width/3)-40.0f, 100.0f, greenPaint);
		
		canvas.drawText("HI SCORE: " + highScore, (width/3)-20.0f, height/2, greenPaint);

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
		long now = System.currentTimeMillis() - spawnPotholeTime;

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

			spawnPotholeTime = System.currentTimeMillis();
		}
	}
	
	//
	// workshop2 code
	//
	
	int lastGameState;
	long pauseStartTime;
	
	private void gamePause(Canvas canvas) {

		// clear screen
		canvas.drawRect(0, 0, width, height, clearPaint);
		
		canvas.drawText("GAME PAUSED", width/3, height/2, greenPaint);
		
		if (playerTap) {
			playerTap = false;
			gameState = lastGameState;

			// determine time elapsed between pause and unpause
			long deltaTime = System.currentTimeMillis() - pauseStartTime;
			
			// adjust timer variables based on elapsed time delta 
			spawnPotholeTime += deltaTime;
			tapToStartTime += deltaTime;
			getReadyGoTime += deltaTime;
			gameOverTime += deltaTime;
			scoreTime += deltaTime;
			
			Log.w("DRJ", "un-Pause game: " + gameState);
		}
	}
	
	public void pause() {
		lastGameState = gameState;
		gameState = GAME_PAUSE;
		pauseStartTime = System.currentTimeMillis();
		Log.w("DRJ", "pause method called");
	}
		
	void spawnPastry() {		
		long now = System.currentTimeMillis() - spawnPastryTime;

		if (now > SPAWN_PASTRY_TIME) {
			// randomly determine whether or not to spawn a new pastry
			if ((int)random(10) > 7) {				
				if (!pastry.alive) {				
					pastry.spawn();					
				}				
			}			
			spawnPastryTime = System.currentTimeMillis();
		}
	}
	
	public void doPlayerEatPastry() {
		// play eat pastry sound
		
		// increase score
		curScore += SCORE_PASTRY_BONUS;
		
		// reset pastry and spawn time
		pastry.alive = false;
		spawnPastryTime = System.currentTimeMillis();
	}
	
	public void doScore(Canvas canvas) {
		
		// first update current score
		long now = System.currentTimeMillis() - scoreTime;
		
		if (now > SCORE_TIME) {
			curScore += SCORE_INC;
			scoreTime = System.currentTimeMillis();
		}
		
		// now draw it the screen
		StringBuilder buf = new StringBuilder("SCORE: ");
		buf.append(curScore);		
		canvas.drawText(buf.toString(), 0, 40, greenPaint);
	}
	
	public void restore(SharedPreferences savedState) {
		
		Log.w("DRJ", "restore method called");
		
		// restore game vars
		
		boolean savedGame = savedState.getBoolean("savedGame", false);
		
		if (savedGame == false) {
			
			//
			// only need to fetch highscore
			//
			highScore = savedState.getInt("game_highScore", SCORE_DEFAULT);
			
			return;
		}

		//
		// first clear saved game state in case player doesn't do a save when they exit
		//
		SharedPreferences.Editor editor = savedState.edit();
		editor.remove("savedGame");
				
		//
		// now start restoring game variables
		//
		
		int lastPotholeId = savedState.getInt("game_lastPotHole_id", -1);
		
		if (lastPotholeId != -1) {
			lastPothole = potholes[lastPotholeId];
		}
		else {
			lastPothole = null;
		}
		
		spawnPotholeTime = savedState.getLong("game_spawnPotholeTicks", 0);
		playerTap = savedState.getBoolean("game_playerTap", false);
		gameState = savedState.getInt("game_gameState", 0);
		tapToStartTime = savedState.getLong("game_tapToStartTime", 0);		
		showTapToStart = savedState.getBoolean("game_showTapToStart", false);
		getReadyGoTime = savedState.getLong("game_getReadyGoTime", 0);
		getReadyGoState = savedState.getInt("game_getReadyGoState", 0);
		gameOverTime = savedState.getLong("game_gameOverTime", 0);
		
		lastGameState = savedState.getInt("game_lastGameState", 1);
		pauseStartTime = savedState.getLong("game_pauseStartTime", 0);
		
		spawnPastryTime = savedState.getLong("game_spawnPastryTime", 0);
		
		scoreTime = savedState.getLong("game_scoreTime", 0);
		curScore = savedState.getInt("game_curScore", 0);
		
		// restore game entities		
		droid.restore(savedState);
		
		for (Pothole p : potholes) {
			p.restore(savedState);
		}
		
		pastry.restore(savedState);
		
		editor.commit();
	}
	
	public void save(SharedPreferences.Editor map, boolean onlyHighScore) {
		
		if (map == null) {			
			return;
		}
		
		Log.w("DRJ", "save method called");

		//
		// only highscore needs to be saved
		//
		if (onlyHighScore) {
			map.putInt("game_highScore", highScore);
			map.commit();
			return;			
		}
		
		map.putBoolean("savedGame", true);
		
		// save game vars
		if (lastPothole == null) {
			map.putInt("game_lastPotHole_id", -1);
		}
		else {
			map.putInt("game_lastPotHole_id", lastPothole.id);
		}
		
		map.putLong("game_spawnPotholeTicks", spawnPotholeTime);
		map.putBoolean("game_playerTap", playerTap);
		map.putInt("game_gameState", gameState);
		map.putLong("game_tapToStartTime", tapToStartTime);
		map.putBoolean("game_showTapToStart", showTapToStart);
		map.putLong("game_getReadyGoTime", getReadyGoTime);
		map.putInt("game_getReadyGoState", getReadyGoState);
		map.putLong("game_gameOverTime", gameOverTime);

		map.putInt("game_lastGameState", lastGameState);
		map.putLong("game_pauseStartTime", pauseStartTime);
		
		map.putLong("game_spawnPastryTime", spawnPastryTime);
		
		map.putLong("game_scoreTime", scoreTime);
		map.putInt("game_curScore", curScore);
		
		// save game entities
		
		droid.save(map);
		
		for (Pothole p : potholes) {
			p.save(map);
		}
		
		pastry.save(map);
		
		//
		// store saved variables
		//
		map.commit();
	}
}