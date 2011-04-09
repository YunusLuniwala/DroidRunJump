package workshop.drj;

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
