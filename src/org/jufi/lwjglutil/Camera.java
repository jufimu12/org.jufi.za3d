package org.jufi.lwjglutil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public class Camera {
	private float tx, ty, tz, rx, ry, rz, fov;
	private FloatBuffer lightposition;
	private final String CAM_DISPLAY_TITLE = "ZombieApocalypse 3D";
	private final int CAM_RES_X, CAM_RES_Y;
	private final int CAM_DISPLAY_RES_X, CAM_DISPLAY_RES_Y;
	private final float CAM_ZNEAR = 0.01f;
	private final float CAM_ZFAR = 2000;
	private final boolean CAM_DISPLAY_ICON = true;
	private final boolean CAM_DISPLAY_VSYNC;
	private final boolean CAM_DISPLAY_FULLSCREEN;
	private final boolean CAM_DISPLAY_DESKTOP_DISPLAY_MODE;
	private final boolean CAM_DISPLAY_ANTIALISING;
	private final boolean CAM_INTI_OPENAL = true;
	
	public Camera(float fov, int resX, int resY, int resXDisplay, int resYDisplay, FloatBuffer lightpos, boolean fullscreen, boolean desktopDisplayMode, boolean antialising, boolean vsync) {
		tx = 64;
		ty = 0;
		tz = 64;
		rx = 0;
		ry = 0;
		rz = 0;
		this.fov = fov;
		this.CAM_RES_X = resX;
		this.CAM_RES_Y = resY;
		this.CAM_DISPLAY_RES_X = resXDisplay;
		this.CAM_DISPLAY_RES_Y = resYDisplay;
		this.lightposition = lightpos;
		this.CAM_DISPLAY_FULLSCREEN = fullscreen;
		this.CAM_DISPLAY_DESKTOP_DISPLAY_MODE = desktopDisplayMode;
		this.CAM_DISPLAY_ANTIALISING = antialising;
		this.CAM_DISPLAY_VSYNC = vsync;
	}
	
	public void tick() {
		rotate();
		translate();
		glLight(GL_LIGHT0, GL_POSITION, lightposition);
	}
	
	public void rotate() {
		glRotatef(-rx, 1, 0, 0);
		glRotatef(-ry, 0, 1, 0);
		glRotatef(-rz, 0, 0, 1);
	}
	public void translate() {
		glTranslatef(-tx, -ty - 2, -tz);
	}
	
	public void move(float dir, float amount, float[][] physmap) {
		float otz = tz;
		float otx = tx;
		tz -= amount * Math.sin(Math.toRadians(-ry + 90 * dir));
		tx -= amount * Math.cos(Math.toRadians(-ry + 90 * dir));
		if (tz < 0 || tz > 128) {
			tz = otz;
		}
		if (tx < 0 || tx > 128) {
			tx = otx;
		}
		for (float[] physobj : physmap) {
			if (collidesWith(physobj) && !collidesWith(physobj, otx, ty, otz)) {
				tx = otx;
				tz = otz;
				break;
			}
		}
	}
	public void moveNoClip(float dir, float amount) {
		if (dir == 1) {
			tx -= amount * Math.cos(Math.toRadians(-ry + 90)) * Math.cos(Math.toRadians(rx));
			ty += amount * Math.sin(Math.toRadians(rx));
			tz -= amount * Math.sin(Math.toRadians(-ry + 90)) * Math.cos(Math.toRadians(rx));
		} else {
			tx -= amount * Math.cos(Math.toRadians(-ry));
			tz -= amount * Math.sin(Math.toRadians(-ry));
		}
	}
	
	public void rotateY(float amount) {
		ry += amount;
		if (ry >= 360) {
			ry -= 360;
		}
		if (ry < 0) {
			ry += 360;
		}
	}
	
	public void rotateX(float amount) {
		rx -= amount;
		if (rx > 90) {
			rx = 90;
		}
		if (rx < -90) {
			rx = -90;
		}
	}
	
	public void init() {
		glMaterialf(GL_FRONT, GL_SHININESS, 64);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_LIGHT0);
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
	}
	
	public void init3d() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(fov, (float)Display.getWidth() / Display.getHeight(), CAM_ZNEAR, CAM_ZFAR);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
	}
	public void init2d() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, CAM_RES_X, 0, CAM_RES_Y, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_DEPTH_TEST);
	}
	
	public void initDisplay() throws LWJGLException, IOException {
		if (CAM_DISPLAY_DESKTOP_DISPLAY_MODE) Display.setDisplayMode(Display.getDesktopDisplayMode());
		else Display.setDisplayMode(new DisplayMode(CAM_DISPLAY_RES_X, CAM_DISPLAY_RES_Y));
		Display.setFullscreen(CAM_DISPLAY_FULLSCREEN);
		Display.setTitle(CAM_DISPLAY_TITLE);
		Display.setVSyncEnabled(CAM_DISPLAY_VSYNC);
		
		if (CAM_DISPLAY_ICON) {
			ByteBuffer[] icon = new ByteBuffer[2];
			icon[0] = ResourceLoader.loadTextureIntoByteBuffer(System.getProperty("user.dir") + "/res/img/icon16.png");
			icon[1] = ResourceLoader.loadTextureIntoByteBuffer(System.getProperty("user.dir") + "/res/img/icon32.png");
			Display.setIcon(icon);
		}
		
		if (CAM_DISPLAY_ANTIALISING) Display.create(new PixelFormat(8, 8, 0, 8));
		else Display.create();
		if (CAM_INTI_OPENAL) AL.create();
	}
	
	public float getTx() {
		return tx;
	}
	
	public void setTx(float tx) {
		this.tx = tx;
	}
	
	public float getTy() {
		return ty;
	}
	
	public void setTy(float ty) {
		this.ty = ty;
	}
	
	public boolean setTy(float ty, float[][] physmap) {
		float oty = this.ty;
		this.ty = ty;
		for (float[] physobj : physmap) {
			if (collidesWith(physobj)) {
				this.ty = oty;
				return true;
			}
		}
		return false;
	}
	
	public float getTz() {
		return tz;
	}
	
	public void setTz(float tz) {
		this.tz = tz;
	}
	
	public float getRx() {
		return rx;
	}
	
	public void setRx(float rx) {
		this.rx = rx;
	}
	
	public float getRy() {
		return ry;
	}
	
	public void setRy(float ry) {
		this.ry = ry;
	}
	
	public float getRz() {
		return rz;
	}
	
	public void setRz(float rz) {
		this.rz = rz;
	}
	
	public float getFov() {
		return fov;
	}
	
	public void setFov(float fov) {
		this.fov = fov;
	}
	
	public boolean collidesWith(float[] points) {
		// float[] points = {xmin, xmax, ymin, ymax, zmin, zmax};
		if (tx > points[0] && tx < points[1] && ty > points[2] && ty < points[3] && tz > points[4] && tz < points[5]) return true;
		else return false;
	}
	
	public boolean collidesWith(float[] points, float x, float y, float z) {
		// float[] points = {xmin, xmax, ymin, ymax, zmin, zmax};
		if (x > points[0] && x < points[1] && y > points[2] && y < points[3] && z > points[4] && z < points[5]) return true;
		else return false;
	}
	
	public boolean colliding(float[][] physmap) {
		for (float[] physobj : physmap) {
			if (collidesWith(physobj)) return true;
		}
		return false;
	}
	
	public boolean colliding(float[][] physmap, float x, float y, float z) {
		for (float[] physobj : physmap) {
			if (collidesWith(physobj, x, y, z)) return true;
		}
		return false;
	}
}
