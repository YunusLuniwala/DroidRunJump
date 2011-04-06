package com.machinezilla;

import android.graphics.Canvas;
import com.machinezilla.Chasm;

class Droid {

	float x;
	float y;
	float prevY;
	float vy;
	boolean jumping;
	boolean falling;
	
	final float w = 40.0f;
	final float h = 45.0f;
	
	Game game;

	public Droid(Game game) {
		reset();
		this.game = game;
	}

	public void reset() {
		jumping = false;
		falling = false;
		x = 380.0f;
		y = 352.5f;
		prevY = y;
	}

	public void update() {

		if (!jumping) {

			float ey = y + h + 2.5f;

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
			prevY = y;
			vy += 1.0f;
			y += vy;
			float tmpY = y + h;
			if (tmpY > 400.0f) {
				y = 352.5f;
				falling = false;
			}
		}

		if (jumping) {
			prevY = y;
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
