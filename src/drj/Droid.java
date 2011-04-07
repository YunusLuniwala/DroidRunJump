package drj;

import android.graphics.Canvas;
import drj.Chasm;

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

			for (Chasm c : game.chasms) {
				if (!c.alive) {
					continue;
				}

				float lx = x;
				float rx = x + w;

				if ((c.x < lx) && ((c.x + c.w) > rx) && (c.y <= ey)) {
					game.gameState = game.GAME_OVER;
					game.tapToStartTime = System.currentTimeMillis();
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
