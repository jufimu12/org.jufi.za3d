package org.jufi.za3d;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

public class Web extends Block {

    private int health;

    public Web(float px, float py, float pz) {
        super(px, py, pz, 3, 1.0f, 1.0f, 1.0f);
        health = 10000;
    }

    public boolean tick() {
        for (int i = 0; i < Main.game.getZombies().size(); i++) {
            if (Math.abs(Main.game.getZombies().get(i).getPx() - px) < 0.5f && Math.abs(Main.game.getZombies().get(i).getPz() - pz) < 0.5f) {
                Main.game.getZombies().get(i).doNotMove(4);
                health--;
            }
        }
        return health > 0;
    }

    public void render() {
        cr = health / 10000f;
        cg = health / 10000f;
        cb = health / 10000f;
        float xmin = px - 0.5f;
        float xmax = px + 0.5f;
        float ymin = py;
        float ymax = py + 1.0f;
        float zmin = pz - 0.5f;
        float zmax = pz + 0.5f;
    	glBindTexture(GL_TEXTURE_2D, 0);
        GL11.glBegin(GL_QUADS);
	        FloatBuffer color = BufferUtils.createFloatBuffer(4);
	        color.put(cr).put(cg).put(cb).put(1).flip();
	        GL11.glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, color);
	        color = BufferUtils.createFloatBuffer(4);
	        color.put(0).put(0).put(0).put(1).flip();
	        GL11.glMaterial(GL_FRONT, GL_SPECULAR, color);
	        
	        GL11.glNormal3f(-1, 0, 0);
	        GL11.glVertex3f(xmin, ymin, zmax);
	        GL11.glVertex3f(xmin, ymax, zmax);
	        GL11.glVertex3f(xmin, ymax, zmin);
	        GL11.glVertex3f(xmin, ymin, zmin);
	        GL11.glNormal3f(1, 0, 0);
	        GL11.glVertex3f(xmax, ymin, zmin);
	        GL11.glVertex3f(xmax, ymax, zmin);
	        GL11.glVertex3f(xmax, ymax, zmax);
	        GL11.glVertex3f(xmax, ymin, zmax);
	        GL11.glNormal3f(0, 0, -1);
	        GL11.glVertex3f(xmin, ymin, zmin);
	        GL11.glVertex3f(xmin, ymax, zmin);
	        GL11.glVertex3f(xmax, ymax, zmin);
	        GL11.glVertex3f(xmax, ymin, zmin);
	        GL11.glNormal3f(0, 0, 1);
	        GL11.glVertex3f(xmax, ymin, zmax);
	        GL11.glVertex3f(xmax, ymax, zmax);
	        GL11.glVertex3f(xmin, ymax, zmax);
	        GL11.glVertex3f(xmin, ymin, zmax);
	        GL11.glNormal3f(0, -1, 0);
	        GL11.glVertex3f(xmin, ymin, zmax);
	        GL11.glVertex3f(xmin, ymin, zmin);
	        GL11.glVertex3f(xmax, ymin, zmin);
	        GL11.glVertex3f(xmax, ymin, zmax);
	        GL11.glNormal3f(0, 1, 0);
	        GL11.glVertex3f(xmin, ymax, zmin);
	        GL11.glVertex3f(xmin, ymax, zmax);
	        GL11.glVertex3f(xmax, ymax, zmax);
	        GL11.glVertex3f(xmax, ymax, zmin);
        GL11.glEnd();
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
