package com.android.sofla.drj;

import android.graphics.Canvas;

class Pothole {

	float x, y;
	float w, h;
	boolean alive;
	
	Game game;

	public Pothole(Game game) {		
		alive = false;
		this.game = game;
		y = game.groundY;
		h = game.groundHeight;
	}

	public void reset() {		
		alive = false;
	}

	public void spawn(float xOffset) {
		
		//
		// spawn a pothole starting beyond right side of the display
		// apply additional xOffset and vary the width of the pothole
		//
		
		w = game.random(game.MIN_POTHOLE_WIDTH, game.MAX_POTHOLE_WIDTH);
		x = game.width + w + xOffset;
		alive = true;
	}

	public void update() {
		
		//
		// potholes move from right to left
		//
		
		x -= 10.0f;
		if (x < -w) {
			alive = false;
		}
	}

	public void draw(Canvas canvas) {
		canvas.drawRect(x, y, x + w, y + h, game.clearPaint);
	}
}
