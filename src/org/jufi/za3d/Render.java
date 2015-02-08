package org.jufi.za3d;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;

import org.jufi.lwjglutil.*;
import org.lwjgl.BufferUtils;

public class Render {
	
	private Render() {
		
	}
	
	public static final int ZA_FLOOR_RES = 8; // (ZA_FLOOR_END - ZA_FLOOR_START) / ZA_FLOOR_RES
	public static final int ZA_FLOOR_START = -256;
	public static final int ZA_FLOOR_END = 384;
	
	public static void floor() {
		glBindTexture(GL_TEXTURE_2D, Main.tex_floor);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		FloatBuffer color = BufferUtils.createFloatBuffer(4);
		color.put(1).put(0.8f).put(0.6f).put(1).flip();
		glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, color);
		color = BufferUtils.createFloatBuffer(4);
		color.put(0).put(0).put(0).put(1).flip();
		glMaterial(GL_FRONT, GL_SPECULAR, color);
		glBegin(GL_QUADS);
		glNormal3f(0, 1, 0);
			for (int a = ZA_FLOOR_START; a < ZA_FLOOR_END; a += ZA_FLOOR_RES) {
				for (int b = ZA_FLOOR_START; b < ZA_FLOOR_END; b += ZA_FLOOR_RES) {
					glTexCoord2f(0, 0);
					glVertex3f(a, 0, b);
					glTexCoord2f(0, 1);
					glVertex3f(a, 0, b + ZA_FLOOR_RES);
					glTexCoord2f(1, 1);
					glVertex3f(a + ZA_FLOOR_RES, 0, b + ZA_FLOOR_RES);
					glTexCoord2f(1, 0);
					glVertex3f(a + ZA_FLOOR_RES, 0, b);
				}
			}
		glEnd();
		
		glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
		color = BufferUtils.createFloatBuffer(4);
		color.put(1).put(0.8f).put(0.6f).put(1).flip();
		glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, color);
	}
	public static void initDisplayLists() throws IOException {
		Main.dl_zombiebody = glGenLists(1);
		glNewList(Main.dl_zombiebody, GL_COMPILE); zombiebody(); glEndList();
		Main.dl_zombiehead = glGenLists(1);
		glNewList(Main.dl_zombiehead, GL_COMPILE); zombiehead(); glEndList();
		Main.dl_bullet = Model.getDL(System.getProperty("user.dir") + "/res/obj/bullet.obj");
		Main.dl_grenade = Model.getDL(System.getProperty("user.dir") + "/res/obj/grenade.obj");
		Main.dl_cexplosive = Model.getDL(System.getProperty("user.dir") + "/res/obj/item2.obj");
		Main.dl_gun = new int[3];
		Main.dl_gun[0] = Model.getDL(System.getProperty("user.dir") + "/res/obj/gun0.obj");
		Main.dl_gun[1] = Model.getDL(System.getProperty("user.dir") + "/res/obj/gun1.obj");
		Main.dl_gun[2] = Model.getDL(System.getProperty("user.dir") + "/res/obj/gun2.obj");
		Main.dl_floorandmap = glGenLists(1);
		glNewList(Main.dl_floorandmap, GL_COMPILE);
		floor();
		new Model(System.getProperty("user.dir") + "/res/map/map.obj").render();
		glEndList();
	}
	
	public static void zombiebody() {
		glTexCoord2f(0.25f, 0.625f); glVertex3f(-0.2f, 0.825f, -0.2f);// Legs
		glTexCoord2f(0.75f, 0.625f); glVertex3f(0.2f, 0.825f, 0.2f);
		glTexCoord2f(0.75f, 1);	glVertex3f(0.2f, 0, 0.2f);
		glTexCoord2f(0.25f, 1);	glVertex3f(-0.2f, 0, -0.2f);
		glTexCoord2f(0, 0.25f); glVertex3f(-0.4f, 1.65f, -0.4f);// Body
		glTexCoord2f(1, 0.25f); glVertex3f(0.4f, 1.65f, 0.4f);
		glTexCoord2f(1, 0.625f); glVertex3f(0.4f, 0.825f, 0.4f);
		glTexCoord2f(0, 0.625f); glVertex3f(-0.4f, 0.825f, -0.4f);
		
		glTexCoord2f(0.25f, 0.625f); glVertex3f(-0.2f, 0.825f, 0.2f);// Legs
		glTexCoord2f(0.75f, 0.625f); glVertex3f(0.2f, 0.825f, -0.2f);
		glTexCoord2f(0.75f, 1);	glVertex3f(0.2f, 0, -0.2f);
		glTexCoord2f(0.25f, 1);	glVertex3f(-0.2f, 0, 0.2f);
		glTexCoord2f(0, 0.25f); glVertex3f(-0.4f, 1.65f, 0.4f);// Body
		glTexCoord2f(1, 0.25f); glVertex3f(0.4f, 1.65f, -0.4f);
		glTexCoord2f(1, 0.625f); glVertex3f(0.4f, 0.825f, -0.4f);
		glTexCoord2f(0, 0.625f); glVertex3f(-0.4f, 0.825f, 0.4f);
	}
	public static void zombiehead() {
		glTexCoord2f(0.25f, 0); glVertex3f(-0.2f, 2.2f, -0.2f);// Head
		glTexCoord2f(0.75f, 0); glVertex3f(0.2f, 2.2f, 0.2f);
		glTexCoord2f(0.75f, 0.25f);	glVertex3f(0.2f, 1.65f, 0.2f);
		glTexCoord2f(0.25f, 0.25f);	glVertex3f(-0.2f, 1.65f, -0.2f);
		
		glTexCoord2f(0.25f, 0); glVertex3f(-0.2f, 2.2f, 0.2f);// Head
		glTexCoord2f(0.75f, 0); glVertex3f(0.2f, 2.2f, -0.2f);
		glTexCoord2f(0.75f, 0.25f);	glVertex3f(0.2f, 1.65f, -0.2f);
		glTexCoord2f(0.25f, 0.25f);	glVertex3f(-0.2f, 1.65f, 0.2f);
	}
	
	
	public static void gun(int shootSpeed, int aimstatus) {
		if (shootSpeed > 25) {
			glPushMatrix();
				glTranslatef(0.25f - 0.0172f * aimstatus, -0.55f + 0.012f * aimstatus, -0.5f);
				glCallList(Main.dl_gun[2]);
			glPopMatrix();
		} else if (shootSpeed > 5) {
			glPushMatrix();
				glTranslatef(0.25f - 0.0172f * aimstatus, -0.55f + 0.012f * aimstatus, -0.5f);
				glCallList(Main.dl_gun[1]);
			glPopMatrix();
		} else {
			glPushMatrix();
				glTranslatef(0.25f - 0.0172f * aimstatus, -0.55f + 0.012f * aimstatus, -0.5f);
				glCallList(Main.dl_gun[0]);
			glPopMatrix();
		}
	}
	
	public static void minimap(float playerX, float playerZ, List<Zombie> zombies) {
		glColor3f(0, 0, 0);
		glBegin(GL_QUADS);
			glVertex2i(64, 64);
			glVertex2i(192, 64);
			glVertex2i(192, 192);
			glVertex2i(64, 192);
		glEnd();
		glBegin(GL_POINTS);
			for (int i = 0; i < zombies.size(); i++) {
				glColor3f(zombies.get(i).getCr(), zombies.get(i).getCg(), zombies.get(i).getCb());
				glVertex2f(zombies.get(i).getPx() + 64, (64 - zombies.get(i).getPz()) + 128);
			}
			glColor3f(1, 1, 1);
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					glVertex2f(playerX + 64 + i, (64 - playerZ) + 128 + j);
				}
			}
		glEnd();
		glColor3f(0, 1, 0);
		glBegin(GL_LINE_LOOP);
			glVertex2i(64, 64);
			glVertex2i(192, 64);
			glVertex2i(192, 192);
			glVertex2i(64, 192);
		glEnd();
	}
	
	public static void skybox() {
		glColor3f(1, 1, 1);
		glBindTexture(GL_TEXTURE_2D, Main.tex_skybox[0]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 0); glVertex3f(-1000, 1000, -1000);
			glTexCoord2f(1, 0); glVertex3f(1000, 1000, -1000);
			glTexCoord2f(1, 1); glVertex3f(1000, 1000, 1000);
			glTexCoord2f(0, 1); glVertex3f(-1000, 1000, 1000);
		glEnd();
		glBindTexture(GL_TEXTURE_2D, Main.tex_skybox[1]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 1); glVertex3f(-1000, -1000, 1000);
			glTexCoord2f(0, 0); glVertex3f(-1000, 1000, 1000);
			glTexCoord2f(1, 0); glVertex3f(1000, 1000, 1000);
			glTexCoord2f(1, 1); glVertex3f(1000, -1000, 1000);
		glEnd();
		glBindTexture(GL_TEXTURE_2D, Main.tex_skybox[2]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 1); glVertex3f(-1000, -1000, -1000);
			glTexCoord2f(0, 0); glVertex3f(-1000, 1000, -1000);
			glTexCoord2f(1, 0); glVertex3f(-1000, 1000, 1000);
			glTexCoord2f(1, 1); glVertex3f(-1000, -1000, 1000);
		glEnd();
		glBindTexture(GL_TEXTURE_2D, Main.tex_skybox[3]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glBegin(GL_QUADS);
			glTexCoord2f(1, 1); glVertex3f(-1000, -1000, -1000);
			glTexCoord2f(0, 1); glVertex3f(1000, -1000, -1000);
			glTexCoord2f(0, 0); glVertex3f(1000, 1000, -1000);
			glTexCoord2f(1, 0); glVertex3f(-1000, 1000, -1000);
		glEnd();
		glBindTexture(GL_TEXTURE_2D, Main.tex_skybox[4]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glBegin(GL_QUADS);
			glTexCoord2f(1, 1); glVertex3f(1000, -1000, -1000);
			glTexCoord2f(0, 1); glVertex3f(1000, -1000, 1000);
			glTexCoord2f(0, 0); glVertex3f(1000, 1000, 1000);
			glTexCoord2f(1, 0); glVertex3f(1000, 1000, -1000);
		glEnd();
	}
}
