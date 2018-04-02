package graphic;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import game.GameEventListener;
import game.Map.Point;
import util.Rect;

import java.util.ArrayList;

public class JoglCanvas extends GLCanvas implements GLEventListener, graphListener {
    private GL2 gl;
    private GLU glu;

    private Rect camera;

    ArrayList<myVertex> vertexes;
    private GameEventListener gameEventListener;
    static float red = 0;
    static float blue = 1;
    static float green = 0;

    public JoglCanvas(GLCapabilities capabilities, int width, int height) {
        super(capabilities);
        setSize(width, height);
        addGLEventListener(this);

        camera = new Rect(0, 0, 512, 512);

        vertexes = new ArrayList<>();
        vertexes.add(new myVertex(5, 5));
        vertexes.add(new myVertex(5, -5));
        vertexes.add(new myVertex(-5, 5));
        vertexes.add(new myVertex(-5, -5));
    }

    public void init(GLAutoDrawable drawable) {
        gl = (GL2) drawable.getGL();
        drawable.setGL(gl);

        glu = new GLU();

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION_MATRIX);
        gl.glLoadIdentity();
        glu.gluOrtho2D(camera.getBottom(), camera.getUp(), camera.getLeft(), camera.getRight());

        gl.glClearDepth(10.0f); // Set depth's clear-value to farthest
        gl.glEnable(GL2.GL_DEPTH_TEST); // Enables depth-buffer for hidden
        // surface removal
        //gl.glDepthFunc(GL2.GL_LEQUAL); // The type of depth testing to do
        //gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);   // nice perspective view, avoid texture distortion.
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        //gl.glShadeModel(GL2.GL_SMOOTH); // Enable smooth shading of color
        //gl.glDisable(GL2.GL_DITHER); // Disable dithering for better performance
        gl.glEnable(GL2.GL_TEXTURE_2D);

        TexturePool.getInstance().loadTexture(gl);

        // Start animator (which should be a field).
        FPSAnimator animator = new FPSAnimator(drawable, 60);
        animator.start();

        //gl.glBitmap();
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {
        GL gl = glAutoDrawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public void display(GLAutoDrawable drawable) {
        gl = (GL2) drawable.getGL();
        // gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION_MATRIX);
        gl.glLoadIdentity();
        //glu.gluOrtho2D(0, 512, 0, 512);
        glu.gluOrtho2D(camera.getBottom(), camera.getUp(), camera.getLeft(), camera.getRight());

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        Texture t = TexturePool.getInstance().getTexture("mapTexture");
        if (t != null) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, t.getTextureObject());
        }

        //gl.glBegin(GL.GL_TRIANGLES);
        if (gameEventListener != null) {
            gameEventListener.GameEvent(this, "Draw");
        }
        //gl.glEnd();

        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

        gl.glColor3f(1f, 0f, 0f);

        gl.glBegin(GL.GL_LINE_LOOP);

        gl.glVertex2d(camera.getBottom(), camera.getLeft());
        gl.glVertex2d(camera.getBottom(), camera.getRight());
        gl.glVertex2d(camera.getUp(), camera.getRight());
        gl.glVertex2d(camera.getUp(), camera.getLeft());

        gl.glEnd();
    }

    /*private void setCamera(GL2 gl, GLU glu, float distance) {
        // Change to projection matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_MATRIX_MODE);
        gl.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, 1, 1, 100);
        glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);

        // Change back to model view matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
    }*/

    private void setCamera(Point point) {
        float cameraSize = 20;
        //gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION_MATRIX);
        //gl.glLoadIdentity();
        //glu.gluOrtho2D(point.x - cameraSize, point.x + cameraSize, point.y - cameraSize, point.y + cameraSize);
        camera = new Rect(point.y - cameraSize, point.x - cameraSize, point.y + cameraSize, point.x + cameraSize);
    }

    public boolean inCameraRect(Rect rect) {
        return camera.included(rect);
    }

    public GL2 getGl() throws Exception {
        if (gl != null)
            return gl;
        else throw new Exception("GL не был инициализирован");
    }

    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    //@Override
    public void AwesomeEvent(String command, Object sender) {
        red = 0;
        blue = 0;
        green = 0;
        switch (command) {
            case "red":
                red = 1f;
                break;
            case "blue":
                blue = 1f;
                break;
            case "green":
                green = 1f;
                break;

            case "setCamera":
                if (sender.getClass() == Point.class) {
                    setCamera((Point) sender);
                }
                break;
        }
    }
}