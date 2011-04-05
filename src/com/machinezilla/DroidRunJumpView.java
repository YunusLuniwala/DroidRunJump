package com.machinezilla;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DroidRunJumpView  extends SurfaceView implements SurfaceHolder.Callback {

	class DroidRunJumpThread extends Thread {
		
		private SurfaceHolder surfaceHolder;
		boolean run;
		Game game;
		
		public DroidRunJumpThread(SurfaceHolder surfaceHolder, Context context) {		
			this.surfaceHolder = surfaceHolder;
			game = new Game();
		}
		
		public void setSurfaceSize(int width, int height) {
			synchronized (surfaceHolder) {
				game.setScreenSize(width, height);
			}
		}
		
		public void setRunning(boolean b) {
			run = b;
		}
		
		public void pause() {
			synchronized (surfaceHolder) {
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
				
		private void doDraw(Canvas canvas) {
			game.doDraw(canvas);
		}

		boolean doTouchEvent(MotionEvent event) {
			boolean handled = false;

			synchronized (surfaceHolder) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					game.doTouch();
					handled = true;
					break;
				}
			}
			
			return handled;			
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
		thread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
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



