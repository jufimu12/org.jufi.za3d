package org.jufi.za3d;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL11;

public class Sentry extends Block {
    private int health;
    
    public Sentry(float px, float py, float pz) {
        super(px, py, pz, 1, 1.0f, 0.0f, 0.0f);
        health = 10800;
    }

    public boolean tick() {
        if (Main.game.getZombies().size() > 0) {
            shootAt(Main.game.getZombies().get((int)(Math.random() * Main.game.getZombies().size())));
            health--;
        }
        setCr(health / 10800f);
        return health > 0;
    }

    private void shootAt(Zombie e) {
        float dx = e.getPx() - px;
        float dy = (e.getPy() + 1.0f) - py;
        float dz = e.getPz() - pz;
        float dxyz = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
        float vx = (dx * 0.5f) / dxyz;
        float vy = (dy * 0.5f) / dxyz;
        float vz = (dz * 0.5f) / dxyz;
        Main.game.getBullets().add(new Bullet(vx, vy, vz, px, py + 0.5f, pz, 2, false));
    }

    public void render() {
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
	        color.put(0.3f).put(0.3f).put(0.3f).put(1).flip();
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
}
