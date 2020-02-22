package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {

    private static final int CREATE_WIDTH = 1920;
    private static final int CREATE_HEIGHT = 1080;
    private static final int FPS_CAP = 60;

    private static long lastFrameTime;
    private static float delta;
    
    private static long oldNanoTime = 0;
    private static int frames = 0;
    
    private static boolean vsync = false;

    public static void createDisplay(String title) {

        ContextAttribs attribs = new ContextAttribs(3, 2)
                .withForwardCompatible(true)
                .withProfileCore(true);

        try {
            /*
            DisplayMode[] modes = Display.getAvailableDisplayModes();

            for (int i=0;i<modes.length;i++) {
                DisplayMode current = modes[i];
                System.out.println(current.getWidth() + "x" + current.getHeight() + "x" +
                        current.getBitsPerPixel() + " " + current.getFrequency() + "Hz");
            }
            */

            /*
            -If the edges of the water quad look a bit jagged (especially when you zoom out with the camera)
            then you may have a problem with the precision of the depth buffer. This can be fixed by increasing
            the NEAR_PLANE value of your projection matrix in the MasterRenderer, or by changing "new PixelFormat()"
             to "new PixelFormat().withDepthBits(24)" when creating the display.
             */



            DisplayMode mode = new DisplayMode(CREATE_WIDTH, CREATE_HEIGHT);
            //DisplayMode mode = Display.getDesktopDisplayMode();
            Display.setDisplayMode(mode);
            Display.setResizable(true);
            Display.create(new PixelFormat(), attribs);
            Display.setTitle(title);
            GL11.glEnable(GL13.GL_MULTISAMPLE);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        GL11.glViewport(0,0, CREATE_WIDTH, CREATE_HEIGHT);
        lastFrameTime = getCurrentTime();
    }

    // returns number of frames drawn since fps printout
    // 0 means fps was just printed out
    public static int updateDisplay() {
    	if (vsync) {
    		Display.sync(FPS_CAP);
    	}
        
        if (Display.wasResized()) {
        	GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        }
        
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;

        // fps calculation
        frames += 1;
        long nanoTime = System.nanoTime();
        long deltaTime = nanoTime - oldNanoTime;
        if (deltaTime > 1000000000) {
        	if (oldNanoTime > 0) {
        		double seconds = deltaTime * 1e-9;
        		double fps = frames / seconds;
        		System.out.println("fps = " + fps);
        		frames = 0;
        	}
        	oldNanoTime = nanoTime;
        }
        
        return frames;
    }

    public static float getFrameTimeSeconds() {
        return delta;
    }

    public static void closeDisplay() {
        Display.destroy();
    }

    // returns current time in milliseconds
    private static long getCurrentTime() {
        return Sys.getTime()*1000/Sys.getTimerResolution();
    }
}
