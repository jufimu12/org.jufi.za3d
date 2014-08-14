package org.jufi.lwjglutil;

import static org.lwjgl.opengl.GL11.*;

public class FPSCounter {
    private int currentFPS;
    private int fps;
    private long start;
    
    public void tick() {
        currentFPS++;
        if (System.currentTimeMillis() - start >= 1000) {
            fps = currentFPS;
            currentFPS = 0;
            start = System.currentTimeMillis();
        }
    }
    
    public int getFPS() {
        return fps;
    }
    public void dispFPS(int resYOrtho, float size) {
    	glBindTexture(GL_TEXTURE_2D, ResourceLoader.whitePixelTexID);
    	int len = String.valueOf(fps).length() * 8;
    	
    	glTranslatef(0, resYOrtho, 0);
    	glScalef(size, size, 1);
    	
    	glColor3f(0, 0, 0);
		glBegin(GL_QUADS);
			glVertex2f(0, 0);
			glVertex2f(0, -10);
			glVertex2f(len, -10);
			glVertex2f(len, 0);
		glEnd();
		
		glColor3f(0, 1, 0);
		SimpleText.drawString(fps, 0, -9);
    }
}
