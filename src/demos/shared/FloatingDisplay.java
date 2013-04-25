package demos.shared;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class FloatingDisplay {
	
	private static final String TAG = FloatingDisplay.class.getSimpleName();

	private int paramNumber;
	private ArrayList<Param> paramArray;
	private String paramString;
	private int x;
	private int y;
	private String position;
	private Paint paint;
	private int digitSize = 20; // 5 pixels per character
	private int canvasWidth;
	private int canvasHeight;
	
	public FloatingDisplay(int paramNumber, String position, int color, int canvasWidth, int canvasHeight) {
		
		this.paramNumber = paramNumber;
		this.paramArray = new ArrayList<Param>();
		this.paramString = "";
		this.position = position;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		
		this.paint = new Paint();
		paint.setColor(color);
		paint.setTextSize(30);
	}
	
	public FloatingDisplay(int paramNumber, int x, int y, int color ) {
		
		this.paramNumber = paramNumber;
		this.paramArray = null;
		this.paramString = "";
		
		this.x = x;
		this.y = y;
		
		this.paint = new Paint();
		paint.setColor(color);
	}
	
	protected void setPositionFromShortcut(int canvasWidth, int canvasHeight, String position) {
		if ( position == "topleft") {
			x = 10;
			y = 30;
		} else if ( position == "topright") {
			x = canvasWidth - (paramString.length() * digitSize) - 12;
			y = 30;
		} else if ( position == "bottomleft") {
			x = 10;
			y = canvasHeight - digitSize - 3;
		} else if ( position == "bottomright") {
			x = canvasWidth - (paramString.length() * digitSize) - 8;
			y = canvasHeight - digitSize - 3;
		}
	}
	
	public void addParam(String paramName, int paramValue ) {
		paramArray.add(new Param(paramArray.size(), paramName, paramValue));
		paramNumber = paramArray.size();
		
		collapseToString();
		
		setPositionFromShortcut(canvasWidth, canvasHeight, position);
	}
	
	public void addParam(String paramName, float paramValue ) {
		paramArray.add(new Param(paramArray.size(), paramName, paramValue));
		paramNumber = paramArray.size();
		
		collapseToString();
		
		setPositionFromShortcut(canvasWidth, canvasHeight, position);
	}
	
	public void addParam(String paramName, double paramValue ) {
		paramArray.add(new Param(paramArray.size(), paramName, paramValue));
		paramNumber = paramArray.size();
		
		collapseToString();
		
		setPositionFromShortcut(canvasWidth, canvasHeight, position);
	}
	
	public void addParam(String paramName, String paramValue ) {
		paramArray.add(new Param(paramArray.size(), paramName, paramValue));
		paramNumber = paramArray.size();
		
		collapseToString();
		
		setPositionFromShortcut(canvasWidth, canvasHeight, position);
	}
	
	public int findParam(String paramName) {
		int index = 0;
		try {
			while( index <= paramArray.size() ) {
				if ( paramArray.get(index).getParamName() == paramName ){
					break;
				}
				index++;
			}
		} catch (Exception e) {
			Log.d(TAG, "Exception: " + e.toString());
			index = paramNumber;
		} 
		return index;
	}
	
	public boolean updateParam(String paramName, int paramNewValue) {
		
		int index = findParam(paramName);
		if ( index < paramNumber ) {
			paramArray.get(index).setParamValue(paramNewValue);
			collapseToString();
			return true;
		} 
		return false;
	}
	
	public boolean updateParam(String paramName, float paramNewValue) {
		
		int index = findParam(paramName);
		if ( index < paramNumber ) {
			paramArray.get(index).setParamValue(paramNewValue);
			collapseToString();
			return true;
		} 
		return false;
	}
	
	public boolean updateParam(String paramName, double paramNewValue) {
		
		int index = findParam(paramName);
		if ( index < paramNumber ) {
			paramArray.get(index).setParamValue(paramNewValue);
			collapseToString();
			return true;
		} 
		return false;
	}
	
	public boolean updateParam(String paramName, String paramNewValue) {
		
		int index = findParam(paramName);
		if ( index < paramNumber ) {
			paramArray.get(index).setParamValue(paramNewValue);
			collapseToString();
			return true;
		} 
		return false;
	}
	
	
	public void deleteParam(int paramId){
		paramArray.remove(paramId);
		paramNumber = paramArray.size();
	}
	
	public void collapseToString() {
		paramString = "";
		for (int i = 0; i < paramNumber; i++) {	
			paramString += paramArray.get(i).getParamName().toString() + ": ";
			
			if ( paramArray.get(i).getParamValue().getClass() == Double.class ) {
				DecimalFormat df = new DecimalFormat("0.##");
				paramString += df.format(paramArray.get(i).getParamValue()) + " ";
			} else if ( paramArray.get(i).getParamValue().getClass() == Float.class ) {
				DecimalFormat df = new DecimalFormat("0.##");
				paramString += df.format(paramArray.get(i).getParamValue()) + " ";
			} else
				paramString += paramArray.get(i).getParamValue().toString() + " ";
		}
	}
	
	public boolean display( Canvas canvas ) {
		
		if ( canvas != null ) {			
			
			Paint backPaint = new Paint();
			backPaint.setColor(Color.DKGRAY);
			backPaint.setAlpha(200);
			canvas.drawRect(x - 5, y-25, x + paramString.length() * 14 - 3, y + 10, backPaint);
			canvas.drawText(paramString, x, y, paint);
			return true;
		}
		return false;
	}
}
