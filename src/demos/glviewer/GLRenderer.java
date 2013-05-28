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

    private float[] mMVPMatrix = new float[16];		    // Matrix View Projection
    private float[] mProjMatrix = new float[16];		// Projection Matrix
    private float[] mVMatrix = new float[16];			// View Matrix
    private float[] mRotationMatrix = new float[16];	// Rotation Matrix
    private float[] mRotationMatX = new float[16];	// Rotation Matrix
    private float[] mRotationMatY = new float[16];	// Rotation Matrix
    private float[] mLightModelMatrix = new float[16];  // Copy of the model matrix for the light position
    private float[] mTemp = new float[16];              // Temporary Matrix for mid calculations

    private float[] mAccumulatedRotation = new float[16]; // Store the accumulated rotation
    private float[] mCurrentRotation = new float[16];     // Store the current rotation

    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;
    public volatile float mAngleX;
    public volatile float mAngleY;



	@Override
	public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
		
		// Set the background frame color
		GLES20.glClearColor(0f, 0f, 0f, 1.0f);

        // Using Depth test
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // User culling to remove back faces
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        //GLES20.glCullFace(GLES20.GL_BACK);
        //GLES20.glFrontFace(GLES20.GL_CCW);

        // Position the eye in fron the origin
        final float eyeX = 0f;
        final float eyeY = 0f;
        final float eyeZ = -0.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

		// initialize
	    mTriangle = new Triangle();
        mSquare = new Square();
        mObject = new GLObject();

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(mAccumulatedRotation, 0);
	}

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        float fovy = 45;
        float near = 1f;
        float far = 15f;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.perspectiveM(mProjMatrix, 0, fovy, ratio, near, far);
        //Matrix.orthoM(mProjMatrix,0,   -ratio, ratio,  -1,1, 1,15);
    }
	
	public void onDrawFrame(GL10 unused) {
		
		// Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        //Matrix.setLookAtM(mVMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -5, 0, 0, 0, 0, 1, 0);

        // Set a matrix that contains the current rotation
        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.setRotateM(mRotationMatX, 0, mAngleX, 0f, 1f, 0f);
        Matrix.setRotateM(mRotationMatY, 0, mAngleY, -1f, 0f, 0f);
        mAngleX = 0f;
        mAngleY = 0f;

        Matrix.multiplyMM(mCurrentRotation, 0, mRotationMatY, 0, mRotationMatX, 0);

        // Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result
        Matrix.multiplyMM(mTemp, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemp, 0, mAccumulatedRotation, 0, 16);

        // Rotate taking the overall rotation into account
        Matrix.multiplyMM(mTemp, 0, mVMatrix, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemp, 0, mVMatrix, 0, 16);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

        // Draw triangle
        mObject.draw(mMVPMatrix, mVMatrix);
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

    /**
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
    */

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +    // ModelViewProjectionMatrix
            "uniform mat4 uMVMatrix;" +     // ModelViewMatrix
            "uniform vec3 uLightPos;" +

            "attribute vec4 vPosition;" +
            "attribute vec4 color;" +
            "attribute vec3 vNormal;" +

            "varying vec4 fColor;" +

            "void main() {" +
                // transform the vertex into eye space
                "   vec3 modelViewVertex = vec3(uMVMatrix * vPosition);" +
                // transform the normal's orientation into eye space
                "   vec3 modelViewNormal = vec3(uMVMatrix * vec4(vNormal, 0.0));" +
                // will be used for attenuation
                "   float distance = length(uLightPos - modelViewVertex);" +
                // get a lighting direction vector from the light to the vertex
                "   vec3 lightVector = normalize(uLightPos - modelViewVertex);" +
                // calculate the dot product of the light vector and the vertex normal.
                // If the normal and light vector are pointing in the same direction
                // then it will get max illumination
                "   float diffuse = max(dot(modelViewNormal, lightVector), 0.1);" +
                // attenuate the light based on distance
                //"   diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));" +
                // multiply the color by the illumination level. It will be interpolated across the triangle
                "   fColor = color * diffuse;" +
                //"   fColor = vec4(uLightPos, 1.0);" +
                // gl_position is a special variable used to store the final position.
                // multiply the vertex by the matrix to get the final point in normalized screen coordinates
                "  gl_Position = uMVPMatrix * vPosition;" +
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
    private int mLightPosHandle;
    private int mMVMatrixHandle;
	private int mMVPMatrixHandle;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
    static final int mColorDataSize = 4;
    static final int mNormalDataSize = 3;
    private int vnumber = 24;
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

    /** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     *  we multiply this by our transformation matrices. */
    private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};

    /** Used to hold the current position of the light in world space (after transformation via model matrix). */
    private final float[] mLightPosInWorldSpace = new float[4];

    /** Used to hold the transformed position of the light in eye space (after transformation via modelview matrix) */
    private final float[] mLightPosInEyeSpace = new float[4];

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
		} else if ( line.startsWith("n") ){
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

        if ( tokens[0].equals("n") ) {
            normals[ni] = Float.parseFloat(tokens[1]);
            normals[ni+1] = Float.parseFloat(tokens[2]);
            normals[ni+2] = Float.parseFloat(tokens[3]);
            ni += 3;
        }
    }

	public void draw(float[] mvpMatrix, float[] mvMatrix) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);
        GLRenderer.checkGlError("glUseProgram");

        // Get program handles
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorAttribHandle = GLES20.glGetAttribLocation(mProgram, "color");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "vNormal");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "uLightPos");
        GLRenderer.checkGlError("Handles");

        //Matrix.multiplyMM(mLightPosInWorldSpace, 0, mvpMatrix, 0, mLightPosInModelSpace, 0);
        //Matrix.multiplyMM(mLightPosInEyeSpace, 0, mvMatrix, 0, mLightPosInWorldSpace, 0);


		// Load vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLRenderer.checkGlError("Vertices");

        // Load colors
        GLES20.glEnableVertexAttribArray(mColorAttribHandle);
        GLRenderer.checkGlError("enableColors");
        GLES20.glVertexAttribPointer(mColorAttribHandle, 4, GLES20.GL_FLOAT, false, 4*4, colorBuffer);
        GLRenderer.checkGlError("Colors");

        // Set up the vertices normals
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false, 3*4, normalBuffer);
        GLRenderer.checkGlError("Normals");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        GLRenderer.checkGlError("glUniform4fv");

        // Load MVPMatrix
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		GLRenderer.checkGlError("MVPMatrix");

        // Load MVMatrix
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);
        GLRenderer.checkGlError("MVMatrix");

        // Pass in the light position in eye space
        //GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
        GLES20.glUniform3f(mLightPosHandle, 1f, 1f, 1f);

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