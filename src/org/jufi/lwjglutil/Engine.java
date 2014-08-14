package org.jufi.lwjglutil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.input.Keyboard.*;

import java.io.IOException;

import org.jufi.lwjglutil.Camera.CameraMode;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.Display;

public abstract class Engine extends Thread {
	// Static stuff
	
	public static void exit(int exitArg) {
		Camera.cleanup();
		System.exit(exitArg);
	}
	
	// Non-Static stuff
	protected Camera cam;
	protected int[] sh_main;// null to disable
	protected boolean exitmainloop = false;
	protected boolean printfps = true;
	private FPSCounter fps = new FPSCounter();
	private int timetogc = 1000;
	
	public Engine() {
		System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir") + "/natives");
	}
	
	public final void run() {
		initEverything();
		
		while (!Display.isCloseRequested()) {// Main loop
			if (exitmainloop) return;
			timetogc--;
			if (timetogc <= 0) {
				System.gc();
				timetogc = 1000;
			}
			input();
			tick();
			fps.tick();
			if (sh_main == null) {
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				
				glLoadIdentity();
					glBindTexture(GL_TEXTURE_2D, ResourceLoader.whitePixelTexID);
					cam.init3d();
					render3dRelativeNoLighting();
					glEnable(GL_LIGHTING);
					render3dRelative();
					cam.tick();
					render3d();
					glDisable(GL_LIGHTING);
					render3dNoLighting();
					
				glLoadIdentity();
					glBindTexture(GL_TEXTURE_2D, ResourceLoader.whitePixelTexID);
					cam.init2d();
					render2d();
					if (printfps) fps.dispFPS(cam.getResY(), 3);
					
				Display.update();
				Display.sync(60);
			} else {
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				
				glLoadIdentity();
					glBindTexture(GL_TEXTURE_2D, ResourceLoader.whitePixelTexID);
					cam.init3d();
					ARBShaderObjects.glUseProgramObjectARB(sh_main[1]);
					render3dRelativeNoLighting();
					ARBShaderObjects.glUseProgramObjectARB(sh_main[0]);
					render3dRelative();
					cam.tick();
					render3d();
					ARBShaderObjects.glUseProgramObjectARB(sh_main[1]);
					render3dNoLighting();
					
				glLoadIdentity();
					glBindTexture(GL_TEXTURE_2D, ResourceLoader.whitePixelTexID);
					cam.init2d();
					ARBShaderObjects.glUseProgramObjectARB(sh_main[2]);
					render2d();
					if (printfps) fps.dispFPS(cam.getResY(), 3);
					
				Display.update();
				Display.sync(60);
			}
		}
		onExit();
		exit(0);
	}
	
	private final void input() {
		if (Mouse.isGrabbed()) {
			cam.rotateY(-(float)Mouse.getDX() / 15);
			cam.rotateX(-(float)Mouse.getDY() / 15);
		}
		move();
	}
	protected void move() {
		if (isKeyDown(KEY_W)) cam.moveNoClip(true, 0.1f);
		if (isKeyDown(KEY_S)) cam.moveNoClip(true, -0.1f);
		if (isKeyDown(KEY_A)) cam.moveNoClip(false, 0.1f);
		if (isKeyDown(KEY_D)) cam.moveNoClip(false, -0.1f);
	}
	private final void initEverything() {
		preInit();
		
		cam = new Camera(initCameraMode());
		try {
			cam.initDisplay();
		} catch (LWJGLException e) {
			e.printStackTrace();
			exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			exit(2);
		}
		cam.init();
		
		cam.init2d();
		SimpleText.drawString("LOADING", (cam.getResX() - 56) / 2, (cam.getResY() - 10) / 2);
		Display.update();
		
		ResourceLoader.initWhitePixelTexID();
		postInit();
		
		System.gc();
	}
	protected abstract void render3dRelative();
	protected abstract void render3dRelativeNoLighting();
	protected abstract void render3d();
	protected abstract void render3dNoLighting();
	protected abstract void render2d();
	protected abstract void tick();
	protected abstract void preInit();
	protected abstract void postInit();
	protected abstract CameraMode initCameraMode();
	protected abstract void onExit();
}
