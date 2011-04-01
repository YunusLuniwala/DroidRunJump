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
	
	Game game;

	public Droid(Game game) {
		reset();
		this.game = game;
	}

	public void reset() {
		jumping = false;
		falling = false;
		x = 400.0f;
		y = 375.0f;
		prevY = y;
	}

	public void update() {

		if (!jumping) {

			float ey = y + 25.0f;

			for (Chasm c : game.chasms) {
				if (!c.alive) {
					continue;
				}

				float lx = x - 20.0f;
				float rx = x + 20.0f;

				if ((c.x < lx) && ((c.x + c.w) > rx) && (c.y <= ey)) {
					game.gameState = game.GAME_OVER;
					game.showTime = System.currentTimeMillis();
				}
			}
		}

		if (falling) {
			prevY = y;
			vy += 1.0f;
			y += vy;
			if (y > 380.0f) {
				y = 375.0f;
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

		if (game.jump && !jumping && !falling) {
			jumping = true;
			game.jump = false;
			vy = 15.0f;
		}
	}

	public void draw(Canvas canvas) {
		canvas.drawRect(x - 20.0f, y - 22.5f, x + 20.0f, y + 22.5f, game.greenPaint);
	}
}
