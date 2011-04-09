package workshop.drj;

import workshop.drj.Pothole;
import android.graphics.Canvas;

class Droid {

	float x;
	float y;
	float vy;
	boolean jumping;
	boolean falling;
	
	final float w = 40.0f;
	final float h = 45.0f;
	
	float yAdjust;
	
	Game game;

	public Droid(Game game) {		
		this.game = game;
		reset();		
	}

	public void reset() {
		jumping = false;
		falling = false;
		x = 380.0f;
		y = 352.5f;
		
		// since droid is floating a little bit above the ground need
		// to take this into account for collision purposes
		yAdjust = game.groundY - y - h; 
	}

	public void update() {

		if (!jumping) {

			float ey = y + h + yAdjust;

			for (Pothole p : game.potholes) {
				if (!p.alive) {
					continue;
				}

				float lx = x;
				float rx = x + w;

				if ((p.x < lx) && ((p.x + p.w) > rx) && (p.y <= ey)) {
					game.initGameOver();
				}
			}
		}

		if (falling) {
			vy += 1.0f;
			y += vy;
			float tmpY = y + h;
			if (tmpY > game.groundY) {
				y = 352.5f;
				falling = false;
			}
		}

		if (jumping) {
			y -= vy;
			vy -= 1.0f;
			if (vy <= 0.0f) {
				jumping = false;
				falling = true;
			}
		}

		if (game.playerTap && !jumping && !falling) {
			jumping = true;
			game.playerTap = false;
			vy = 15.0f;
		}
	}

	public void draw(Canvas canvas) {
		canvas.drawRect(x, y, x + w, y + h, game.greenPaint);
	}
}
