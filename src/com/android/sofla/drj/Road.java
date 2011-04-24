package com.android.sofla.drj;

import android.content.SharedPreferences;
import android.graphics.Canvas;

public class Road {
	
	Game game;
	float y;
	float h;
		
	final int MAX_DIVIDERS = 11;
	float [] dividerX;
		
	public Road(Game game) {
		this.game = game;
		y = game.groundY;
		h = game.groundHeight;
		
		dividerX = new float[MAX_DIVIDERS];		
	}
	
	public void reset() {
		float xOffset = 0.0f;		
		for (int i=0; i<MAX_DIVIDERS; i++) {
			dividerX[i] = xOffset;
			xOffset += 80.0f; 			
		}		
	}
	
	public void update() {
		for (int i=0; i<MAX_DIVIDERS; i++) {
			dividerX[i] -= 10.0f;
			if (dividerX[i] < -60.0f) {
				dividerX[i] = game.width + 10.0f;				
			}
		}
	}
	
	public void draw(Canvas canvas) {
		canvas.drawBitmap(game.roadImage, 0, y, game.emptyPaint);
		
		for (int i=0; i<MAX_DIVIDERS; i++) {			
			canvas.drawBitmap(game.dividerImage, dividerX[i], y+10.0f, game.emptyPaint);			
		}		
	}
	
	public void restore(SharedPreferences savedState) {
		/*
		x = savedState.getFloat("droid_x", 0);
		y = savedState.getFloat("droid_y", 0);
		vy = savedState.getFloat("droid_vy", 0);
		jumping = savedState.getBoolean("droid_jumping", false);
		falling = savedState.getBoolean("droid_falling", false);
		yAdjust = savedState.getFloat("droid_yAdjust", 0);
		*/
	}
	
	public void save(SharedPreferences.Editor map) {
		/*
		map.putFloat("droid_x", x);
		map.putFloat("droid_y", y);
		map.putFloat("droid_vy", vy);
		map.putBoolean("droid_jumping", jumping);
		map.putBoolean("droid_falling", falling);
		map.putFloat("droid_yAdjust", yAdjust);
		*/
	}	
}
