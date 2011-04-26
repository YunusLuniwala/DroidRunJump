package com.android.sofla.drj;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DroidRunJumpView extends SurfaceView implements SurfaceHolder.Callback {

	//
	// game thread
	//
	class DroidRunJumpThread extends Thread {
		
		private SurfaceHolder surfaceHolder;
		boolean run;
		Game game;
		
		public DroidRunJumpThread(SurfaceHolder surfaceHolder, Context context, Game game) {
			run = false;
			this.surfaceHolder = surfaceHolder;
			this.game = game;
		}
		
		public void setSurfaceSize(int width, int height) {
			synchronized (surfaceHolder) {
				game.setScreenSize(width, height);
			}
		}
		
		public void setRunning(boolean b) {
			run = b;
		}
		
		@Override
		public void run() {
			
			//
			// game loop
			//
			
			while (run) {
				Canvas c = null;				
				try {
					c = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder) {
						game.run(c);
					}
				} finally {
					if (c != null) {
						surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
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
		
		//
		// workshop2
		//
		
		public void pause() {
			synchronized (surfaceHolder) {				
				game.pause();
				run = false;
			}
		}
		
		public void restoreGame(SharedPreferences savedInstanceState) {
			synchronized (surfaceHolder) {
				game.restore(savedInstanceState);
			}
		}

		public void saveGame(SharedPreferences.Editor editor, boolean onlyHighScore) {
			synchronized (surfaceHolder) {
				game.save(editor, onlyHighScore);				
			}
		}
		
		// -- END workshop 2
	}
	
	//
	// game view
	//
	private DroidRunJumpThread thread;
	
	//
	// workshop 2
	//
	private Context context;
	private Game game;
	
	// -- END workshop 2
	
	public DroidRunJumpView(Context context, AttributeSet attrs) {		
		super(context, attrs);
		
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		//
		// workshop2 
		//
		
		this.context = context;
		game = new Game(context);
		thread = new DroidRunJumpThread(holder, context, game);		
		
		// -- END workshop 2
		
		setFocusable(true);		
	}

	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
	}

	
	public void surfaceCreated(SurfaceHolder holder) {
		//
		// workshop 2
		//
		
		if (!thread.run) {
			thread = new DroidRunJumpThread(holder, context, game);
		}
		
		// -- END workshop 2
		
		thread.setRunning(true);
		thread.start();
	}
	
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
	
	//
	// workshop2 code
	//
	
	public DroidRunJumpThread getThread() {
		return thread;
	}
	
	// -- END workshop 2
}
