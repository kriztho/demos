package demos.glviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.*;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

public class GLRenderer implements GLSurfaceView.Renderer {
	
	private static final String TAG = GLRenderer.class.getSimpleName();
	private Triangle mTriangle;
    private Square   mSquare;
    private GLObject mObject;

    private float[] mTemp = new float[16];                  // Temporary Matrix for mid calculations

    private final float[] mMVPMatrix = new float[16];		// Matrix View Projection
    private final float[] mProjMatrix = new float[16];		// Projection Matrix
    private final float[] mVMatrix = new float[16];			// View Matrix
    private final float[] mRotationMatrix = new float[16];	// Rotation Matrix

    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;

	@Override
	public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
		
		// Set the background frame color
		GLES20.glClearColor(0f, 0f, 0f, 1.0f);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glFrontFace(GLES20.GL_CCW);

		// initialize
	    mTriangle = new Triangle();
        mSquare = new Square();
        mObject = new GLObject();
	}
	
	public void onDrawFrame(GL10 unused) {
		
		// Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mVMatrix, 0, 0f, 0f, -8f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

        /*
        // Create a rotation for the triangle
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 1.0f, 1.0f, 1.0f);        
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);        
         */
        
        // Draw square
        //mSquare.draw(mMVPMatrix);

        /*
        // Create a rotation for the triangle
        time = SystemClock.uptimeMillis() % 4000L;
        angle = 0.090f * ((int) time);
        */
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0.0f, -1.0f, 0.0f);
        mTemp = mMVPMatrix.clone();

        // Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(mMVPMatrix, 0, mTemp, 0, mRotationMatrix, 0);

        // Draw triangle
        //mTriangle.draw(mMVPMatrix);
        mObject.draw(mMVPMatrix);
	}
	
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		 // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        //Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -ratio, ratio, 1, 10);
        Matrix.perspectiveM(mProjMatrix, 0, 45, ratio, 1, 15);
        //Matrix.orthoM(mProjMatrix,0,   -ratio, ratio,  -1,1, 1,15);
	}
	
	public static int loadShader(int type, String shaderCode){

	    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	    int shader = GLES20.glCreateShader(type);

	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shader, shaderCode);
	    GLES20.glCompileShader(shader);

	    return shader;
	}
	
	/**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

}




class Triangle {

    private final String vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +

        "attribute vec4 vPosition;" +
        "void main() {" +
        // the matrix must be included as a modifier of gl_Position
        "  gl_Position = vPosition * uMVPMatrix;" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "void main() {" +
        "  gl_FragColor = vColor;" +
        "}";

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = { // in counterclockwise order:
         0.0f,  0.622008459f, 0.0f,   // top
        -0.5f, -0.311004243f, 0.0f,   // bottom left
         0.5f, -0.311004243f, 0.0f    // bottom right
    };
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Triangle() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                                   vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                                     fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}





class Square {

    private final String vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +

        "attribute vec4 vPosition;" +
        "void main() {" +
        // the matrix must be included as a modifier of gl_Position
        "  gl_Position = vPosition * uMVPMatrix;" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "void main() {" +
        "  gl_FragColor = vColor;" +
        "}";

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = { -0.5f,  0.5f, 0.0f,   // top left
                                    -0.5f, -0.5f, 0.0f,   // bottom left
                                     0.5f, -0.5f, 0.0f,   // bottom right
                                     0.5f,  0.5f, 0.0f }; // top right

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };

    public Square() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                              GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}





class GLObject {

    private static final String TAG = GLObject.class.getSimpleName();

    private final String vertexShaderCode =
			// This matrix member variable provides a hook to manipulate
			// the coordinates of the objects that use this vertex shader
			"uniform mat4 uMVPMatrix;" +
	        "attribute vec4 vPosition;" +
            "attribute vec4 color;" +
            "attribute vec3 vNormal;" +
            "varying vec4 fColor;" +
	        "void main() {" +
	        // the matrix must be included as a modifier of gl_Position
	        "  gl_Position = uMVPMatrix * vPosition;" +
            "  fColor = color;" +
	        "}";

	private final String fragmentShaderCode =
			"precision mediump float;" +
            "varying vec4 fColor;" +
					"uniform vec4 vColor;" +
					"void main() {" +
					//"  vec3 val = gl_FragCoord.xyz / 1000.0;" +
                    //"  gl_FragColor = vec4(val, 1.0);" +
                    "  gl_FragColor = fColor;" +
					"}";

    // Buffers
	private final FloatBuffer vertexBuffer;
    private final ShortBuffer faceBuffer;
    private final FloatBuffer colorBuffer;
    private final FloatBuffer normalBuffer;

    private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
    private int mColorAttribHandle;
    private int mNormalHandle;
	private int mMVPMatrixHandle;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
    static final int COLORCOORDS_PER_VERTEX = 4;
    private int vnumber = 8;
    private int fnumber = 12;

    //private ArrayList<Float> verts;
    //private ArrayList<Integer> faces;
    //private float[] coords = new float[550000];
    //private int[] faces = new int[720000];

    // Arrays
    private float[] coords = new float[vnumber*3];
    private short[] faces = new short[fnumber*3];
    private float[] colors = new float[vnumber*4];
    private float[] normals = new float[vnumber*3];

    // Indices
    private int vi = 0;
    private int fi = 0;
    private int ci = 0;
    private int ni = 0;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

	// Set color with red, green, blue and alpha (opacity) values
	float color[] = { 0.2f, 0.709803922f, 0.898039216f, 0.2f };

	public GLObject() {
		
		// Read and parse file to generate float[]
		//verts = new ArrayList<Float>();
		//faces = new ArrayList<Integer>();
		objParser();


		// initialize vertex byte buffer for shape coordinates
        // (# of coordinate values * 4 bytes per float)
		ByteBuffer bb = ByteBuffer.allocateDirect( coords.length * 4 );
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(coords);
		vertexBuffer.position(0);

        // Faces Buffer
        ByteBuffer bf = ByteBuffer.allocateDirect( faces.length * 2 );
        bf.order(ByteOrder.nativeOrder());
        faceBuffer = bf.asShortBuffer();
        faceBuffer.put(faces);
        faceBuffer.position(0);

        // Colors Buffer
        ByteBuffer bc = ByteBuffer.allocateDirect( colors.length * 4 );
        bc.order(ByteOrder.nativeOrder());
        colorBuffer = bc.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        // Normals Buffer
        ByteBuffer bn = ByteBuffer.allocateDirect( normals.length * 4 );    //Float is 4bytes
        bn.order(ByteOrder.nativeOrder());
        normalBuffer = bn.asFloatBuffer();
        normalBuffer.put(normals);
        normalBuffer.position(0);

		// prepare shaders and OpenGL program
		int vertexShader = GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
		int fragmentShader = GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);

		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
	}
	
	public void objParser() {
    	
    	//Find the directory for the SD Card using the API
    	File sdcard = Environment.getExternalStorageDirectory();
    	
    	//Get the text file
    	//File file = new File(sdcard,"Droids/xtho.obj");
        //File file = new File(sdcard,"Droids/cube-wireframe.obj");
        File file = new File(sdcard,"Droids/cube-faces.obj");

    	try {
    	    BufferedReader br = new BufferedReader(new FileReader(file));
    	    String line;

    	    while ((line = br.readLine()) != null) {
                //Log.e(TAG, "vi: "+vi+" fi: "+fi);
    	    	parse(line);
    	    }
            //Log.e(TAG, "¡¡¡¡¡¡¡¡Finished loading object file!!!!!!!!!");
            //Log.e(TAG, "vi: "+ vi + " fi: "+fi + "vs. " + "v's>: " + vnumber + "f's: "+ fnumber);

            // In case is necessary to close the reader
            br.close();
    	}
    	catch (IOException e) {
    	    Log.w(TAG, e.getMessage());
    	    e.printStackTrace();
    	}
    }
	
	public void parse(String line) {
	
		if ( line.startsWith("v") ) {
			readVertex(line);
		} else if ( line.startsWith("c") ) {
	        readColor(line);
		} else if ( line.startsWith("f") ){
			readFace(line);
		} else if ( line.startsWith("vn") ){
            readNormal(line);
        } else {
			// For now, disregard everything else
		}
	}
    
    public void readVertex(String line) {
    	
    	String delims = "[ ]+";
    	String[] tokens = line.split(delims);
    	
    	if ( tokens[0].equals("v") ) {
    		/*
    		verts.add(Float.parseFloat(tokens[1]));
    		verts.add(Float.parseFloat(tokens[2]));
    		verts.add(Float.parseFloat(tokens[3]));
    		*/
    		coords[vi] = Float.parseFloat(tokens[1]);
    		coords[vi+1] = Float.parseFloat(tokens[2]);
    		coords[vi+2] = Float.parseFloat(tokens[3]);
            vi += 3;
    	}
    }
    
    public void readFace(String line) {
    	String delims = "[ ]+";
    	String[] tokens = line.split(delims);
    	
    	if ( tokens[0].equals("f") ) {
    		faces[fi] = Short.parseShort(tokens[1]);
    		faces[fi+1] = Short.parseShort(tokens[2]);
    		faces[fi+2] = Short.parseShort(tokens[3]);

            /*
            if ( tokens.length < 5 )
                faces[fi+3] = 0;    // Adding -1's whenever there are only 3 tokens. Waste of space for now
            else
    		    faces[fi+3] = Integer.parseInt(tokens[4]);
    		*/
            fi += 3;
    	}
    }

    public void readColor(String line){
        String delims = "[ ]+";
        String[] tokens = line.split(delims);

        if ( tokens[0].equals("c") ) {
    		colors[ci] = Float.parseFloat(tokens[1]);
            colors[ci+1] = Float.parseFloat(tokens[2]);
            colors[ci+2] = Float.parseFloat(tokens[3]);
            colors[ci+3] = Float.parseFloat(tokens[4]);
            ci += 4;
        }
    }

    public void readNormal(String line) {

        String delims = "[ ]+";
        String[] tokens = line.split(delims);

        if ( tokens[0].equals("vn") ) {
            normals[ni] = Float.parseFloat(tokens[1]);
            normals[ni+1] = Float.parseFloat(tokens[2]);
            normals[ni+2] = Float.parseFloat(tokens[3]);
            ni += 3;
        }
    }

	public void draw(float[] mvpMatrix) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);
        GLRenderer.checkGlError("glUseProgram");

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLRenderer.checkGlError("glGetAttribLocation");
		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLRenderer.checkGlError("glEnableVertexAttribArray");
		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLRenderer.checkGlError("glVertexAttribPointer");


        // Set up vertices colors
        mColorAttribHandle = GLES20.glGetAttribLocation(mProgram, "color");
        GLRenderer.checkGlError("glGetAttribLocation");
        GLES20.glEnableVertexAttribArray(mColorAttribHandle);
        GLRenderer.checkGlError("glEnableVertexAttribArray");
        GLES20.glVertexAttribPointer(mColorAttribHandle, 4, GLES20.GL_FLOAT, false, 4*4, colorBuffer);
        GLRenderer.checkGlError("glVertexAttribPointer");

/*
        // Set up the vertices normals
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "vNormal");
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(mNormalHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, normalBuffer);
*/

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLRenderer.checkGlError("glGetUniformLocation vColor");
		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        GLRenderer.checkGlError("glUniform4fv");


		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		GLRenderer.checkGlError("glGetUniformLocation");
		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		GLRenderer.checkGlError("glUniformMatrix4fv");

		// Draw the cube
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, faces.length, GLES20.GL_UNSIGNED_SHORT, faceBuffer);
        GLRenderer.checkGlError("glDrawElements");

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLRenderer.checkGlError("glDisableVertexAttribArray");
        GLES20.glDisableVertexAttribArray(mColorAttribHandle);
        GLRenderer.checkGlError("glDisableVertexAttribArray");
	}
}