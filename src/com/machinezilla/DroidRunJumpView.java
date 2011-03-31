package com.machinezilla;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DroidRunJumpView  extends SurfaceView implements SurfaceHolder.Callback {

	class DroidRunJumpThread extends Thread {
		
		private SurfaceHolder surfaceHolder;
		boolean run;
		int canvasWidth;
		int canvasHeight;
		
		Paint textPaint;
		Paint clearPaint;
		Paint greenPaint;
		
		Random rng;
		
		public DroidRunJumpThread(SurfaceHolder surfaceHolder, Context context) {		
			this.surfaceHolder = surfaceHolder;
			
			greenPaint = new Paint();
			greenPaint.setAntiAlias(true);
			greenPaint.setARGB(255, 0, 255, 0);
			greenPaint.setFakeBoldText(true);		
			greenPaint.setTextSize(42.0f);

			clearPaint = new Paint();
			clearPaint.setARGB(255, 0, 0, 0);
			clearPaint.setAntiAlias(true);
			
			rng = new Random();
		}
		
		public void setSurfaceSize(int width, int height) {
			synchronized (surfaceHolder) {				
				canvasWidth = width;
				canvasHeight = height;				
			}
		}
		
		public void setRunning(boolean b) {
			run = b;
		}
		
		public void pause() {
			synchronized (surfaceHolder) {
				// check mode if running then set state to pause
			}
		}
		
		public void unpause() {
			synchronized (surfaceHolder) {			
			}
		}
		
		public synchronized void restoreState(Bundle savedState) {
			synchronized (surfaceHolder) {
			}
		}
		
		@Override
		public void run() {
			while (run) {
				Canvas c = null;				
				try {
					c = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder) {
						//update physics
						doDraw(c);
					}
				} finally {
					if (c != null) {
						surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
		
		public Bundle saveState(Bundle map) {
			synchronized (surfaceHolder) {
				if (map != null) {					
				}
			}
			return map;
		}
				
		private void doStart() {
			synchronized (surfaceHolder) {
				showTime = System.currentTimeMillis();
				show = true;

				for (int i=0; i<MAX_CHASMS; i++) {
					chasms[i] = new Chasm();
				}

				gameState = GAME_MENU;

				resetGame();
			}
		}

		
		private void doDraw(Canvas canvas) {
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
		
		boolean doTouchEvent(MotionEvent event) {
			boolean handled = false;

			synchronized (surfaceHolder) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					jump = true;
					handled = true;
					break;
				}
			}
			
			return handled;			
		}
		
		final int MAX_CHASMS = 5;
		Chasm [] chasms = new Chasm[MAX_CHASMS];
		Droid droid = new Droid();
		
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

		class Droid {

			float x;
			float y;
			float prevY;
			float vy;
			boolean jumping;
			boolean falling;

			public Droid() {
				reset();
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

					for (Chasm c : chasms) {
						if (!c.alive) {
							continue;
						}

						float lx = x - 20.0f;
						float rx = x + 20.0f;

						if ((c.x < lx) && ((c.x + c.w) > rx) && (c.y <= ey)) {          
							gameState = GAME_OVER;
							showTime = System.currentTimeMillis();
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

				if (jump && !jumping && !falling) {
					jumping = true;
					jump = false;
					vy = 15.0f;
				}
			}

			public void draw(Canvas canvas) {
				canvas.drawRect(x-20.0f, y-22.5f, x+20.0f, y+22.5f, greenPaint);
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

		class Chasm {

			float x, y;
			float w, h;
			boolean alive;

			public Chasm() {
				h = 20.0f;
				y = 400.0f;
				alive = false;
			}

			public void reset() {
				x = canvasWidth;
				alive = false;
			}

			public void spawn() {
				w = random(100, 201);
				x = canvasWidth + w + lastWidth;
				lastWidth = w + 10.0f;
				alive = true;
			}

			public void update() {
				x -= 10.0f;
				if (x < -w) {
					alive = false;
				}
			}

			public void draw(Canvas canvas) {
				canvas.drawRect(x, y, x+w, y+h, clearPaint);
			}
		}		
	}
	
	private DroidRunJumpThread thread;
	
	public DroidRunJumpView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);		
		thread = new DroidRunJumpThread(holder, context);		
		setFocusable(true);		
	}

	public DroidRunJumpThread getThread() {
		return thread;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		thread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		thread.setRunning(true);
		thread.doStart();
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}		
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		return thread.doTouchEvent(event);
	}
}



