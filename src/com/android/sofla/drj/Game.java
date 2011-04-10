package com.android.sofla.drj;

import java.util.Random;

import com.android.sofla.drj.Droid;
import com.android.sofla.drj.Pothole;


import android.graphics.Canvas;
import android.graphics.Paint;


public class Game {

	final int MAX_potholes = 10;
	Pothole [] potholes;
	Pothole lastPothole;
	
	Droid droid = new Droid(this);
	final float groundY = 400;
	final float groundHeight = 20;

	boolean playerTap;
	long spawnChasmTicks;
	final long SPAWN_TIME = 750;

	final int GAME_MENU = 0;
	final int GAME_READY = 1;
	final int GAME_PLAY = 2;
	final int GAME_OVER = 3;

	int gameState;

	long tapToStartTime;
	boolean showTapToStart;
	
	final int SHOW_GET_READY = 0;
	final int SHOW_GO = 1;
	
	long getReadyGoTime;
	int getReadyGoState;

	Paint textPaint;
	Paint clearPaint;
	Paint greenPaint;
	
	Random rng;
	
	int width;
	int height;
	
	public Game() {
		
		greenPaint = new Paint();
		greenPaint.setAntiAlias(true);
		greenPaint.setARGB(255, 0, 255, 0);
		greenPaint.setFakeBoldText(true);		
		greenPaint.setTextSize(42.0f);

		clearPaint = new Paint();
		clearPaint.setARGB(255, 0, 0, 0);
		clearPaint.setAntiAlias(true);
		
		rng = new Random();
		
		tapToStartTime = System.currentTimeMillis();
		showTapToStart = true;
		
		droid = new Droid(this);
		
		potholes = new Pothole[MAX_potholes];
		for (int i=0; i<MAX_potholes; i++) {
			potholes[i] = new Pothole(this);
		}
		
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
		
		spawnChasmTicks = System.currentTimeMillis();
		
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
		tapToStartTime = System.currentTimeMillis();		
	}

	private void gameOver(Canvas canvas) {
		//textSize(48);
		canvas.drawRect(0, 0, width, height, clearPaint);
		canvas.drawText("GAME OVER", width/3, height/2, greenPaint);

		long now = System.currentTimeMillis() - tapToStartTime;
		if (now > 2000) {
			resetGame();
		}
	}

	private void gamePlay(Canvas canvas) {
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
		long now = System.currentTimeMillis() - spawnChasmTicks;

		if (now > SPAWN_TIME) {

			if ((int)random(10) > 2) {
				for (Pothole p : potholes) {
					
					if (p.alive) {
						continue;
					}
					
					float xOffset = 0.0f;
					
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

			spawnChasmTicks = System.currentTimeMillis();
		}
	}
}
