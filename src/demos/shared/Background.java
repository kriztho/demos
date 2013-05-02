package demos.shared;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.R;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.OpenableColumns;
import android.renderscript.Float2;
import android.util.Xml;

public class Background {
	
	private Bitmap bitmap;
	private int width;
	private int height;
	private ArrayList<Rect> obstacles;
	private boolean collisionDetection = true;
	private Rect frameBox;
	private static final String ns = null;
	XmlPullParser parser = null;
	Context context = null;
	
	
	
	///////////////////////////////////////////////////////
	// Constructors
	///////////////////////////////////////////////////////
	public Background(Rect frameBox, String XMLFile, Context context) {
		// Assign resources for bitmap processing
		this.context = context;
		this.frameBox = frameBox;
		
		// Create obstacles array
		obstacles = new ArrayList<Rect>();
		
		// Create parser and parse
		parser = Xml.newPullParser();
		try {
			parse(XMLFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
	
	
	
	
	
	///////////////////////////////////////////////////////
	// Setters and getters
	///////////////////////////////////////////////////////
	
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
	
	
	
	

	
	///////////////////////////////////////////////////////
	// XML reading routines
	///////////////////////////////////////////////////////
	public void parse(String XMLFile) throws IOException, XmlPullParserException {
		
		AssetManager manager = context.getResources().getAssets();
	    InputStream input = manager.open("bg_rpg.xml");
		parser.setInput(input, null);
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if(eventType == XmlPullParser.START_DOCUMENT) {
				System.out.println("Start document");
			} else if(eventType == XmlPullParser.END_DOCUMENT) {
				System.out.println("End document");
			} else if(eventType == XmlPullParser.START_TAG) {
				System.out.println("Start tag "+parser.getName());
				if ( parser.getName().equalsIgnoreCase("background")){
					readBackground(parser);
				}
			} else if(eventType == XmlPullParser.END_TAG) {
				System.out.println("End tag "+parser.getName());
			} else if(eventType == XmlPullParser.TEXT) {
				System.out.println("Text "+parser.getText());
			}
			eventType = parser.next();
		}
	}
	
	public void readBackground(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "background");
		
		//For processing the file tag
		Rect activeLimits = new Rect();	// Tells which limits are colliding
		
		while ( parser.next() != XmlPullParser.END_TAG ) {
			if ( parser.getEventType() != XmlPullParser.START_TAG ) {
				continue;
			}
			
			String name = parser.getName();
			if ( name.equals("file")){
				readFile(parser, activeLimits);				
			} else if (name.equals("obstacles")) {
				readObstacle(parser);
			} else {
				skip(parser);
			}
		}
		//parser.nextTag();		// Generates error messages
		parser.require(XmlPullParser.END_TAG, ns, "background");
	}
	
	void readFile(XmlPullParser parser, Rect activeLimits) throws XmlPullParserException, IOException {
		
		parser.require(XmlPullParser.START_TAG, ns, "file");
		
		String filename = parser.getAttributeValue(null, "name");
		width = Integer.parseInt(parser.getAttributeValue(null, "width"));
		height = Integer.parseInt(parser.getAttributeValue(null, "height"));
		String path = context.getPackageName()+":drawable/"+filename;
		int resID = context.getResources().getIdentifier(path, null, null);
		bitmap = BitmapFactory.decodeResource(context.getResources(), resID);
		
		// Add framebox
		int left = Integer.parseInt(parser.getAttributeValue(null, "leftLimit"));
		int top = Integer.parseInt(parser.getAttributeValue(null, "topLimit"));
		int right = Integer.parseInt(parser.getAttributeValue(null, "rightLimit"));
		int bottom = Integer.parseInt(parser.getAttributeValue(null, "bottomLimit"));
		activeLimits.set(left, top, right, bottom);
		obstacles.add(activeLimits);
		
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, ns, "file");
	}
	
	public void readObstacle(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "obstacles");
		
		while ( parser.next() != XmlPullParser.END_TAG ) {
			if ( parser.getEventType() != XmlPullParser.START_TAG ) {
				continue;
			}
			
			String name = parser.getName();
			if (name.equals("item")) {
				parser.require(XmlPullParser.START_TAG, ns, "item");
				int left = Integer.parseInt(parser.getAttributeValue(null, "left"));
				int top = Integer.parseInt(parser.getAttributeValue(null, "top"));
				int right = Integer.parseInt(parser.getAttributeValue(null, "right"));
				int bottom = Integer.parseInt(parser.getAttributeValue(null, "bottom"));
				obstacles.add(new Rect(left, top, right, bottom));
				parser.nextTag();
				parser.require(XmlPullParser.END_TAG, ns, "item");
			} else {
				skip(parser);
			}
		}
		//parser.nextTag();
		parser.require(XmlPullParser.END_TAG, ns, "obstacles");
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
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
		//obstacles.add(new Rect(1, 480, 1193, 687));
		obstacles.add(new Rect(125, 219, 125 + 261, 219 + 39));
		obstacles.add(new Rect(59, 251, 59 + 421, 251 + 37));
		obstacles.add(new Rect(28, 282, 28 + 452, 282 + 38));
		
		// Hills
		//obstacles.add(new Rect(319 / bitmap.getWidth() * frameBox.width(), 0, frameBox.width(), 222 / bitmap.getHeight() * frameBox.height()));
		//obstacles.add(new Rect(800, 1, 1193, 480));
		//obstacles.add(new Rect(1, 1, 160, 270));
		obstacles.add(new Rect(319, 0, 319 + 161, 222));
		obstacles.add(new Rect(0, 0, 31, 33));
		obstacles.add(new Rect(0, 32, 64, 32 + 94));
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

	
	
	
	
	
	///////////////////////////////////////////////////////
	// Displaying
	///////////////////////////////////////////////////////
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
