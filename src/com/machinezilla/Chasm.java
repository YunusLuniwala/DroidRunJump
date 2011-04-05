package com.machinezilla;

import android.graphics.Canvas;

class Chasm {

	float x, y;
	float w, h;
	boolean alive;
	
	Game game;

	public Chasm(Game game) {
		h = 20.0f;
		y = 400.0f;
		alive = false;
		this.game = game; 
	}

	public void reset() {
		x = game.width;
		alive = false;
	}

	public void spawn() {
		w = game.random(100, 201);
		x = game.width + w + game.lastWidth;
		game.lastWidth = w + 10.0f;
		alive = true;
	}

	public void update() {
		x -= 10.0f;
		if (x < -w) {
			alive = false;
		}
	}

	public void draw(Canvas canvas) {
		canvas.drawRect(x, y, x + w, y + h, game.clearPaint);
	}
}
