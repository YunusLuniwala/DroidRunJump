package drj;

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
		alive = false;
	}

	public void spawn(float xOffset) {
		w = game.random(100, 201);
		x = game.width + w + xOffset;
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
