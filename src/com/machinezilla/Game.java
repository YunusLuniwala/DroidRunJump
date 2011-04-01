package com.machinezilla;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.machinezilla.Droid;
import com.machinezilla.Chasm;

public class Game {

	final int MAX_CHASMS = 5;
	Chasm [] chasms;
	Droid droid = new Droid(this);

	boolean jump;
	long spawnChasmTicks;
	float lastWidth = 0;

	final int GAME_MENU = 0;
	final int GAME_READY = 1;
	final int GAME_PLAY = 2;
	final int GAME_PAUSE = 3;
	final int GAME_OVER = 4;

	int gameState;

	long showTime;
	boolean show;

	Paint textPaint;
	Paint clearPaint;
	Paint greenPaint;
	
	Random rng;
	
	int canvasWidth;
	int canvasHeight;
	
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
		
		showTime = System.currentTimeMillis();
		show = true;
		
		droid = new Droid(this);
		
		chasms = new Chasm[MAX_CHASMS];
		for (int i=0; i<MAX_CHASMS; i++) {
			chasms[i] = new Chasm(this);
		}

		resetGame();
		
		gameState = GAME_MENU;
	}

	public void doDraw(Canvas canvas) {
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
		case GAME_PAUSE:
			gamePause(canvas);
			break;
		case GAME_OVER:
			gameOver(canvas);
			break;
		}
	}


	private void resetGame() {
		showTime = System.currentTimeMillis();
		show = true;
		jump = false;
		lastWidth = 0;
		spawnChasmTicks = System.currentTimeMillis();
		droid.reset();
		for (Chasm c : chasms) {
			c.reset();
		}
	}

	private void gameOver(Canvas canvas) {
		//textSize(48);
		canvas.drawRect(0, 0, canvasWidth, canvasHeight, clearPaint);
		canvas.drawText("GAME OVER", canvasWidth/3, canvasHeight/2, greenPaint);

		long now = System.currentTimeMillis() - showTime;
		if (now > 2000) {
			gameState = GAME_MENU;
			resetGame();
		}
	}

	private void gamePause(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	private void gamePlay(Canvas canvas) {
		canvas.drawRect(0, 0, canvasWidth, canvasHeight, clearPaint);

		// draw ground
		canvas.drawRect(0, 400, canvasWidth, 420, greenPaint);

		droid.update();
		droid.draw(canvas);

		for (Chasm c : chasms) {
			if (c.alive) {
				c.update();
				c.draw(canvas);
			}
		}

		spawnChasm();
	}

	private void gameReady(Canvas canvas) {
		// TODO Auto-generated method stub
		gameState = GAME_PLAY;			
	}

	private void gameMenu(Canvas canvas) {

		//textSize(48);
		canvas.drawRect(0, 0, canvasWidth, canvasHeight, clearPaint);

		canvas.drawText("DROID-RUN-JUMP", (canvasWidth/3)-40.0f, 100.0f, greenPaint);


		if (jump) {
			gameState = GAME_READY;
			jump = false;
		}

		long now = System.currentTimeMillis() - showTime;
		if (now > 550) {
			showTime = System.currentTimeMillis();
			show = !show;
		}

		if (show) {
			//textSize(36);				
			canvas.drawText("TAP TO START", canvasWidth/3, canvasHeight-100.0f, greenPaint);
		}			
	}


	public float random(float a) {
		return rng.nextFloat() * a;
	}

	public float random(float a, float b) {
		return a + (rng.nextFloat() * b);
	}

	void spawnChasm() {
		long now = System.currentTimeMillis() - spawnChasmTicks;

		if (now > 750) {

			if ((int)random(10) > 3) {
				for (Chasm c : chasms) {
					if (!c.alive) {
						c.spawn();
						break;
					}
				}
			}

			spawnChasmTicks = System.currentTimeMillis();
		}
	}
}
