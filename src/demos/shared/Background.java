package demos.shared;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Background {
	
	private Bitmap bitmap;
	private int width;
	private int height;
	private ArrayList<Rect> obstacles;
	private boolean collisionDetection = true;
	private Rect frameBox;
	
	public Background(Bitmap bitmap, Rect frameBox) {
		this.bitmap = bitmap;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
		this.frameBox = frameBox;
		
		this.collisionDetection = true;
		obstacles = new ArrayList<Rect>();
		loadObstacles();
	}

	public Background(Bitmap bitmap, Rect frameBox, ArrayList<Rect> obstacles) {
		this.bitmap = bitmap;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
		this.frameBox = frameBox;
		
		this.collisionDetection = true;
		this.obstacles = obstacles; 
		//obstacles = new ArrayList<Rect>();
		//loadObstacles();
	}
	
	public void loadObstacles() {
		
		// adding the outer frame
		obstacles.add(frameBox);
		
		/*
		// Trees
		int twidth = 34;
		int theight = 26;
		obstacles.add(new Rect(64, 0, 64 + twidth, 0 + theight));
		obstacles.add(new Rect(114, 37, 114 + twidth, 37 + theight));
		obstacles.add(new Rect(177, 35, 177 + twidth, 35 + theight));
		obstacles.add(new Rect(196, 103, 196 + twidth, 103 + theight));
		obstacles.add(new Rect(161, 134, 161 + twidth, 134 + theight));		
		obstacles.add(new Rect(127, 197, 127 + twidth, 197 + theight));		
		obstacles.add(new Rect(33, 197, 33 + twidth, 197 + theight));
		obstacles.add(new Rect(1, 261, 1 + twidth, 261 + theight));
		*/
		
		// Water
		obstacles.add(new Rect(1, 480, 1193, 687));
		
		// Hills
		//obstacles.add(new Rect(319 / bitmap.getWidth() * frameBox.width(), 0, frameBox.width(), 222 / bitmap.getHeight() * frameBox.height()));
		obstacles.add(new Rect(800, 1, 1193, 480));
		obstacles.add(new Rect(1, 1, 160, 270));
	}
	
	// It should load from a file or something static way
	public void loadObstacles(ArrayList<Rect> obstacles) {
		
		// Outer frame
		obstacles.add(new Rect(0,0,getBitmap().getWidth(), getBitmap().getHeight()));
		
		// Trees
		int twidth = 34;
		int theight = 26;
		obstacles.add(new Rect(64, 0, 64 + twidth, 0 + theight));
		obstacles.add(new Rect(114, 37, 114 + twidth, 37 + theight));
		obstacles.add(new Rect(177, 35, 177 + twidth, 35 + theight));
		obstacles.add(new Rect(196, 103, 196 + twidth, 103 + theight));
		obstacles.add(new Rect(161, 134, 161 + twidth, 134 + theight));		
		obstacles.add(new Rect(127, 197, 127 + twidth, 197 + theight));		
		obstacles.add(new Rect(33, 197, 33 + twidth, 197 + theight));
		obstacles.add(new Rect(1, 261, 1 + twidth, 261 + theight));
		
		// Water
		obstacles.add(new Rect(125, 219, 125 + 261, 219 + 39));
		obstacles.add(new Rect(59, 251, 59 + 421, 251 + 37));
		obstacles.add(new Rect(28, 282, 28 + 452, 282 + 38));
		
		// Hills
		obstacles.add(new Rect(319, 0, 319 + 161, 222));
		obstacles.add(new Rect(0, 0, 31, 33));
		obstacles.add(new Rect(0, 32, 64, 32 + 94));
	}
	
	public void addObstacle(Rect obstacle) {
		obstacles.add(obstacle);
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public Drawable getDrawable() {
		return new BitmapDrawable(bitmap);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ArrayList<Rect> getObstacles() {
		return obstacles;
	}

	public boolean isCollisionDetection() {
		return collisionDetection;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setObstacles(ArrayList<Rect> obstacles) {
		this.obstacles = obstacles;
	}

	public void setCollisionDetection(boolean collisionDetection) {
		this.collisionDetection = collisionDetection;
	}

	public void render(Canvas canvas) {
		//canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.drawBitmap(bitmap, null, frameBox, null);
		
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(3);
		for ( int i = 1 ; i < obstacles.size(); i++)
			canvas.drawRect(obstacles.get(i), paint);
	}
}
