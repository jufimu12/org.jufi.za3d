package org.jufi.za3d;

import org.jufi.lwjglutil.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.input.Keyboard.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;

public class Game extends Thread {
	private Camera cam;
	private ArrayList<Block> blocks = new ArrayList<Block>();
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private ArrayList<Grenade> grenades = new ArrayList<Grenade>();
	private ArrayList<Zombie> zombies = new ArrayList<Zombie>();
	private FPSCounter fps = new FPSCounter();
	private float[][] physmap, pphysmap;
	private float spawnSpeed = 1, backgred = 0, runtimeleft = 0, runtimereg = 0.1f, runfactor = 2, spawnSpeedAdd, fallspeed = 0.1f, accuracy = 6, recoil = 0;
	private int shootCooldown = 0, shootSpeed = 2, spawnCooldown = 0, defaultBulletHealth = 1, bulletSpeed = 1, multiShot = 1, maxHp = 100, armor = 5, grenadeamt = 1, runtimecap = 360;
	private int shootSpeedCost = 40, accuracylvl = 6, recoillvl = 10;
	private int hp = 100, aimstatus = 0, initresX = 1280, initresY = 720, hpblinking = 0;
	private int[] items = {0, 0, 0, 1};
	private String[] itemnames = {"nothing", "Sentrygun", "C4", "Web"};
	private short selecteditem = 0;
	private long score = 0, coins = 150, medikitCost = 10;
	private boolean lctrlDown = false, gamePaused = true, gameOver = false, aiming = false, initfullscreen = false;
	private boolean menulmousedown = false, canmodifyrunfactor = false, runfasterdown = false, runslowerdown = false, fDown = false, jetpack = false;
	
	public Game(float spawnSpeedAdd, float coinsFactor, int resMode) {
		switch (resMode) {
		case 0:
			initresX = 1280;
			initresY = 720;
			initfullscreen = false;
			break;
		case 1:
			initresX = 1600;
			initresY = 900;
			initfullscreen = false;
			break;
		case 2:
			initresX = Display.getDesktopDisplayMode().getWidth();
			initresY = Display.getDesktopDisplayMode().getHeight();
			initfullscreen = true;
			break;
		default:
			System.out.println("Unknown resolution-Mode. Loading default");
			initresX = 1600;
			initresY = 900;
			initfullscreen = false;
	}
		this.spawnSpeedAdd = spawnSpeedAdd;
		this.coins = (long) (coinsFactor * coins);
	}
	
	public void run() {
		FloatBuffer lPos = BufferUtils.createFloatBuffer(4);// Init Camera
		lPos.put(-0.8f).put(0.5f).put(-1).put(0).flip(); // x y z i; light at 0)infinity 1)a point
		cam = new Camera(70, 1600, 900, initresX, initresY, lPos, initfullscreen, initfullscreen, true, true);
		try {
			cam.initDisplay();
		} catch (Exception e) {
			e.printStackTrace();
			Main.exit(1);
		}
		cam.init();
		cam.init2d();
		SimpleText.drawString("LOADING", 770, 450);
		Display.update();
		
		try {// Load Resources
			Main.tex_zombey = ResourceLoader.loadTexture(System.getProperty("user.dir") + "/res/img/z0mb3y.png");
			Main.tex_floor = ResourceLoader.loadTexture(System.getProperty("user.dir") + "/res/img/floor.png");
			Main.tex_skybox = new int[5];
			Main.tex_skybox[0] = ResourceLoader.loadTexture(System.getProperty("user.dir") + "/res/img/skybox_top.png");
			Main.tex_skybox[1] = ResourceLoader.loadTexture(System.getProperty("user.dir") + "/res/img/skybox_front.png");
			Main.tex_skybox[2] = ResourceLoader.loadTexture(System.getProperty("user.dir") + "/res/img/skybox_left.png");
			Main.tex_skybox[3] = ResourceLoader.loadTexture(System.getProperty("user.dir") + "/res/img/skybox_back.png");
			Main.tex_skybox[4] = ResourceLoader.loadTexture(System.getProperty("user.dir") + "/res/img/skybox_right.png");
			
			
			Main.obj_gun = new Model[3];
			Main.obj_gun[0] = new Model(System.getProperty("user.dir") + "/res/obj/gun0.obj");
			Main.obj_gun[1] = new Model(System.getProperty("user.dir") + "/res/obj/gun1.obj");
			Main.obj_gun[2] = new Model(System.getProperty("user.dir") + "/res/obj/gun2.obj");
			Main.obj_bullet = new Model(System.getProperty("user.dir") + "/res/obj/bullet.obj");
			Main.obj_grenade = new Model(System.getProperty("user.dir") + "/res/obj/grenade.obj");
			Main.obj_item2 = new Model(System.getProperty("user.dir") + "/res/obj/item2.obj");
			Main.obj_fence = new Model(System.getProperty("user.dir") + "/res/obj/fence.obj");
			Main.obj_fencelong = new Model(System.getProperty("user.dir") + "/res/obj/fencelong.obj");
			Main.obj_map = new Model(System.getProperty("user.dir") + "/res/map/map.obj");
			physmap = loadPhysModel(System.getProperty("user.dir") + "/res/map/map.pma", false);
			pphysmap = loadPhysModel(System.getProperty("user.dir") + "/res/map/map.pma", true);
			
			Main.wavs_gunfire = new int[3];
			Main.wavs_gunfire[0] = ResourceLoader.getSourceFromWav(System.getProperty("user.dir") + "/res/wav/gun0_fire.wav", 0.15f);
			Main.wavs_gunfire[1] = ResourceLoader.getSourceFromWav(System.getProperty("user.dir") + "/res/wav/gun1_fire.wav", 0.1f);
			Main.wavs_gunfire[2] = ResourceLoader.getSourceFromWav(System.getProperty("user.dir") + "/res/wav/gun2_fire.wav", 0.15f);
			
		} catch (IOException e) {
			e.printStackTrace();
			Main.printError("Failed to load resources");
			Main.exit(1);
		}
		
		Main.dl_zombiebody = glGenLists(1); // Init DisplayLists
		glNewList(Main.dl_zombiebody, GL_COMPILE); Render.zombiebody(); glEndList();
		Main.dl_zombiehead = glGenLists(1);
		glNewList(Main.dl_zombiehead, GL_COMPILE); Render.zombiehead(); glEndList();
		Main.dl_bullet = glGenLists(1);
		glNewList(Main.dl_bullet, GL_COMPILE); Main.obj_bullet.render(); glEndList();
		Main.dl_floorandmap = glGenLists(1);
		glNewList(Main.dl_floorandmap, GL_COMPILE); Render.floor(); Main.obj_map.render(); glEndList();
		Main.dl_grenade = glGenLists(1);
		glNewList(Main.dl_grenade, GL_COMPILE); Main.obj_grenade.render(); glEndList();
		Main.dl_cexplosive = glGenLists(1);
		glNewList(Main.dl_cexplosive, GL_COMPILE); Main.obj_item2.render(); glEndList();
		Main.dl_gun = new int[3];
		Main.dl_gun[0] = glGenLists(1);
		glNewList(Main.dl_gun[0], GL_COMPILE); Main.obj_gun[0].render(); glEndList();
		Main.dl_gun[1] = glGenLists(1);
		glNewList(Main.dl_gun[1], GL_COMPILE); Main.obj_gun[1].render(); glEndList();
		Main.dl_gun[2] = glGenLists(1);
		glNewList(Main.dl_gun[2], GL_COMPILE); Main.obj_gun[2].render(); glEndList();
		
		System.gc();
		
		while (!Display.isCloseRequested()) {// Main Loop
			if (gameOver && Keyboard.isKeyDown(KEY_N)) {
				if (Display.isCreated()) Display.destroy();
				if (AL.isCreated()) AL.destroy();
				Main.launcher.setVisible(true);
				return;
			}
			forTicker();
			fps.tick();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glClearColor(0.4f, 0.2f, 0, 0);
			glLoadIdentity();
				cam.init3d();
				glEnable(GL_LIGHTING);
				renderRelative3d();
				cam.tick();
				render3d();
				glDisable(GL_LIGHTING);
				render3dNoLighting();
			glLoadIdentity();
				cam.init2d();
				glBindTexture(GL_TEXTURE_2D, 0);
				render2d();
			Display.update();
		}
		Main.exit();
	}
	
	private void forTicker() {
		Main.mouseX = Mouse.getX() * 1600 / Display.getWidth();
		Main.mouseY = Mouse.getY() * 900 / Display.getHeight();
		pausedetect();
		if (!gamePaused && !gameOver) {
			input();
			tick();
		}
		if (gamePaused && !gameOver) {
			listenmenu();
		}
	}
	
	private void pausedetect() {
		if (Keyboard.isKeyDown(KEY_LCONTROL) || Keyboard.isKeyDown(KEY_E)) {
			if (!lctrlDown) {
				gamePaused = !gamePaused;
				Mouse.setGrabbed(!Mouse.isGrabbed());
			}
			lctrlDown = true;
		} else {
			lctrlDown = false;
		}
		if (Mouse.isButtonDown(0) && Main.mouseX >= 400 && Main.mouseX <= 450 && Main.mouseY >= 728 && Main.mouseY <= 740) Main.exit(0);
	}
	private void input() {
		if (Main.enabledebugmode && Keyboard.isKeyDown(KEY_LMENU)) {
			if (Keyboard.isKeyDown(KEY_C)) coins *= 2;
			if (Keyboard.isKeyDown(KEY_H)) hp += 1;
			if (Keyboard.isKeyDown(KEY_N)) hp -= 1;
		}
		if (Keyboard.isKeyDown(KEY_W)) {
			if (Keyboard.isKeyDown(KEY_LSHIFT) && runtimeleft >= 1 && !Mouse.isButtonDown(1)) {
				runtimeleft--;
				cam.move(1, 0.1f * runfactor, pphysmap);
				if (cam.getFov() < 80) {
					cam.setFov(cam.getFov() + 1);
				}
			} else {
				cam.move(1, 0.1f, pphysmap);
				if (cam.getFov() > 70) {
					cam.setFov(cam.getFov() - 1);
				}
			}
		}
		if (Keyboard.isKeyDown(KEY_S)) {
			cam.move(1, -0.1f, pphysmap);
		}
		if (Keyboard.isKeyDown(KEY_A)) {
			cam.move(0, 0.1f, pphysmap);
		}
		if (Keyboard.isKeyDown(KEY_D)) {
			cam.move(0, -0.1f, pphysmap);
		}
		if (Keyboard.isKeyDown(KEY_ADD)) {
			if (canmodifyrunfactor && !runfasterdown && runfactor + 0.5f <= 5) {
				runfactor += 0.5f;
			}
			runfasterdown = true;
		} else {
			runfasterdown = false;
		}
		if (Keyboard.isKeyDown(KEY_SUBTRACT)) {
			if (canmodifyrunfactor && !runslowerdown && runfactor - 0.5f >= 0) {
				runfactor -= 0.5f;
			}
			runslowerdown = true;
		} else {
			runslowerdown = false;
		}
		
		if (Keyboard.isKeyDown(KEY_G)) {
			if (shootCooldown <= 0 && grenadeamt > 0) {
				throwGrenade();
				grenadeamt--;
				shootCooldown = 90;
			}
		}
		
		if (Keyboard.isKeyDown(KEY_SPACE)) {
			if (runtimeleft > 5 && fallspeed > -0.25f && jetpack && cam.getTy() < 32) {
				fallspeed -= 0.05f;
				runtimeleft -= 5;
			} else if (cam.getTy() <= 0 || cam.colliding(pphysmap, cam.getTx(), cam.getTy() - 0.05f, cam.getTz())) {
				fallspeed = -0.2f;
			}
		}
		
		if (Keyboard.isKeyDown(KEY_F)) {
			if (!fDown) {
				if (items[selecteditem] > 0) {
					float vxz = (float) Math.cos(Math.toRadians(cam.getRx()));
					float vy = (float) (0.01 * Math.sin(Math.toRadians(cam.getRx())));
					float vx = (float) (0.01 * Math.sin(Math.toRadians(cam.getRy() - 180)) * vxz);
					float vz = (float) (0.01 * Math.cos(Math.toRadians(cam.getRy() - 180)) * vxz);
					float x = cam.getTx();
					float y = cam.getTy() + 2;
					float z = cam.getTz();
					int attempt = 0;
					while (attempt < 1000) {
						attempt++;
						x += vx;
						y += vy;
						z += vz;
						if (cam.colliding(physmap, x, y, z)) {
							if (cam.colliding(physmap, x - vx, y, z - vz) && x >= 0 && x < 128 && z >= 0 && z < 128) {
								blocks.add(Block.getByType(x, y, z, selecteditem));
								items[selecteditem]--;
							}
							break;
						}
					}
				}
			}
			fDown = true;
		} else {
			fDown = false;
		}
		
		if (cam.setTy(cam.getTy() - fallspeed, pphysmap)) fallspeed = 0;
		fallspeed += 0.01f;
		if (cam.getTy() > 32) {
			cam.setTy(32);
		}
		if (cam.getTy() < 0) {
			if (fallspeed > 0.1f) {
				fallspeed = 0.1f;
			}
			cam.setTy(0);
		}
		
		if (Mouse.isGrabbed()) {
			int dx = Mouse.getDX();
			int dy = Mouse.getDY();
			if (dx != 0){
				cam.rotateY(-(float)dx / 15);
			}
			if (dy != 0){
				cam.rotateX(-(float)dy / 15);
			}
		}
		
		if (Mouse.isButtonDown(1)) {
			accuracy = accuracylvl / 8f;
			aiming = true;
		} else {
			accuracy = accuracylvl;
			aiming = false;
		}
		if (aiming && aimstatus < 10) aimstatus++;
		if (!aiming && aimstatus > 0) aimstatus--;
		if (cam.getFov() <= 70) cam.setFov(70 - aimstatus * 3);
		
		if (Mouse.isButtonDown(0)) {
			if (shootCooldown <= 0) {
				shoot();
				if (shootSpeed > 25) alSourcePlay(Main.wavs_gunfire[2]);
				else if (shootSpeed > 5) alSourcePlay(Main.wavs_gunfire[1]);
				else alSourcePlay(Main.wavs_gunfire[0]);
				if (recoil < recoillvl) recoil += recoillvl / 25f;
				shootCooldown = 60 / shootSpeed;
			}
		} else {
			recoil = 0;
		}
		if (Keyboard.isKeyDown(KEY_0)) {
			selecteditem = 0;
		} else if (Keyboard.isKeyDown(KEY_1)) {
			selecteditem = 1;
		} else if (Keyboard.isKeyDown(KEY_2)) {
			selecteditem = 2;
		} else if (Keyboard.isKeyDown(KEY_3)) {
			selecteditem = 3;
		}
		
	}
	private void tick() {
		if (hpblinking > 0) hpblinking--;
		else if (hp < maxHp / 5) hpblinking = 30;
		if (runtimeleft + runtimereg <= runtimecap) {
			runtimeleft += runtimereg;
		}
		if (shootCooldown > 0) {
			shootCooldown--;
		}
		if (spawnCooldown > 0) {
			spawnCooldown--;
		}
		if (spawnCooldown <= 0) {
			for (int i = 0; i < (int) Math.floor(spawnSpeed); i++) {
				spawnZombie();
			}
			spawnSpeed += spawnSpeedAdd;
			spawnCooldown = 60;
			System.gc();
		}
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < grenades.size(); i++) {
				if (!grenades.get(i).tick(physmap)) {
					grenades.remove(i);
				}
			}
			for (int i = 0; i < bullets.size(); i++) {
				if (!bullets.get(i).tick(cam.getTx(), cam.getTy(), cam.getTz(), physmap)) {
					bullets.remove(i);
				}
			}
			for (int i = 0; i < zombies.size(); i++) {
				if (!zombies.get(i).tick(bullets, cam.getTx(), cam.getTy(), cam.getTz(), pphysmap)) {
					zombies.remove(i);
				}
			}
		}
		if (hp <= 0) {
			hp = 0;
			gameOver = true;
		}
		if (backgred - 0.02f >= 0) {
			backgred -= 0.02f;
		} else {
			backgred = 0;
		}
		for (int i = 0; i < blocks.size(); i++) {
			if (!blocks.get(i).tick()) {
				blocks.remove(i);
			}
		}
	}
	private void renderRelative3d() {
		glRotatef(recoil / 4f, 1, 0, 0);
		Render.gun(shootSpeed, aimstatus);
		
		if (!aiming || shootSpeed > 5) {
			glDisable(GL_LIGHTING);
			glColor3f(backgred, 1 - backgred, 0);
			
			glLoadIdentity();
			glRotatef(-accuracy - recoil, 0, 1, 0);
			glTranslated(0, -0.0015, -0.1);
			glBegin(GL_QUADS);
			glVertex3d(0, -0.0001, 0);
			glVertex3d(0.003, -0.0001, 0);
			glVertex3d(0.003, 0.0001, 0);
			glVertex3d(0, 0.0001, 0);
			glEnd();
			
			glLoadIdentity();
			glRotatef(accuracy + recoil, 0, 1, 0);
			glTranslated(0, -0.0015, -0.1);
			glBegin(GL_QUADS);
			glVertex3d(0, -0.0001, 0);
			glVertex3d(-0.003, -0.0001, 0);
			glVertex3d(-0.003, 0.0001, 0);
			glVertex3d(0, 0.0001, 0);
			glEnd();
			
			glLoadIdentity();
			glRotatef(-accuracy - recoil, 1, 0, 0);
			glTranslated(0, -0.0015, -0.1);
			glBegin(GL_QUADS);
			glVertex3d(-0.0001, 0, 0);
			glVertex3d(-0.0001, -0.003, 0);
			glVertex3d(0.0001, -0.003, 0);
			glVertex3d(0.0001, 0, 0);
			glEnd();
			
			glLoadIdentity();
			glRotatef(accuracy + recoil, 1, 0, 0);
			glTranslated(0, -0.0015, -0.1);
			glBegin(GL_QUADS);
			glVertex3d(-0.0001, 0, 0);
			glVertex3d(-0.0001, 0.003, 0);
			glVertex3d(0.0001, 0.003, 0);
			glVertex3d(0.0001, 0, 0);
			glEnd();
			glEnable(GL_LIGHTING);
		}
		
		glLoadIdentity();
		glRotatef(recoil / 4f, -1, 0, 0);
	}
	private void render3d() {
		glCallList(Main.dl_floorandmap);
		for (Bullet e : bullets) {
			glPushMatrix();
				glTranslatef(e.getPx(), e.getPy(), e.getPz());
				glCallList(Main.dl_bullet);
			glPopMatrix();
		}
		for (Grenade e : grenades) {
			glPushMatrix();
				glTranslatef(e.getPx(), e.getPy(), e.getPz());
				glCallList(Main.dl_grenade);
			glPopMatrix();
		}
		for (Block block : blocks) {
			block.render();
		}
	}
	private void render3dNoLighting() {
		glBindTexture(GL_TEXTURE_2D, Main.tex_zombey);
		for (Zombie zombie : zombies) {
			glPushMatrix();
			glTranslatef(zombie.getPx(), zombie.getPy(), zombie.getPz());
				glBegin(GL_QUADS);
					glColor3f(1, 1, 1);
					glCallList(Main.dl_zombiebody);
					glColor3f(zombie.getCr(), zombie.getCg(), zombie.getCb());
					glCallList(Main.dl_zombiehead);
				glEnd();
			glPopMatrix();
		}
		Render.skybox();
	}
	private void render2d() {
		Render.minimap(cam.getTx(), cam.getTz(), zombies);// Minimap, left-bottom
		glColor3f(0, 1, 0);// Hp and Stamina bars, right-bottom
		glBegin(GL_LINE_LOOP);
			glVertex2i(1350, 50);
			glVertex2i(1550, 50);
			glVertex2i(1550, 80);
			glVertex2i(1350, 80);
		glEnd();
		glBegin(GL_LINE_LOOP);
			glVertex2i(1350, 90);
			glVertex2i(1550, 90);
			glVertex2i(1550, 120);
			glVertex2i(1350, 120);
		glEnd();
		glBegin(GL_QUADS);
		float barwidth = (float) hp / (float) maxHp * 190;
			if (hpblinking > 15) glColor3f(1, 0, 0);
			else glColor3f(0, 0, 1);
			glVertex2f(1355, 55);
			glVertex2f(1355 + barwidth, 55);
			glVertex2f(1355 + barwidth, 75);
			glVertex2f(1355, 75);
		barwidth = (float) runtimeleft / (float) runtimecap * 190;
			glColor3f(0, 0, 1);
			glVertex2f(1355, 95);
			glVertex2f(1355 + barwidth, 95);
			glVertex2f(1355 + barwidth, 115);
			glVertex2f(1355, 115);
		glEnd();
		glColor3f(0.0f, 0.5f, 1.0f);// Texts
		SimpleText.drawString("HP", 1330, 62);
		SimpleText.drawString("STAMINA", 1290, 102);
		SimpleText.drawString("ITEM SELECTED: " + itemnames[selecteditem] + " - " + items[selecteditem] + " left", 1250, 142);
		SimpleText.drawString("GRENADES: " + grenadeamt, 1250,  162);
		SimpleText.drawString("HP    " + hp, 1, 890);
		SimpleText.drawString("Score " + score, 1, 880);
		SimpleText.drawString("Coins " + coins, 1, 870);
		SimpleText.drawString("Level " + String.valueOf((int) Math.floor(spawnSpeed)), 1, 860);
		SimpleText.drawString("FPS  " + String.valueOf(fps.getFPS()), 1500, 890);
		Runtime runtime = Runtime.getRuntime();
		SimpleText.drawString("RAM  " + String.valueOf((runtime.totalMemory() - runtime.freeMemory()) / 1048576), 1500, 880);
		SimpleText.drawString("posX " + String.valueOf(Math.round(cam.getTx())), 1500, 870);
		SimpleText.drawString("posY " + String.valueOf(Math.round(cam.getTy())), 1500, 860);
		SimpleText.drawString("posZ " + String.valueOf(Math.round(cam.getTz())), 1500, 850);
		if (gameOver) {
			SimpleText.drawString("Game Over", 750, 500);
			SimpleText.drawString("Your Score " + String.valueOf(score), 720, 475);
			SimpleText.drawString("Press N for a new Game", 690, 450);
		} else if (gamePaused) {
			SimpleText.drawString("Game paused", 750, 500);
			SimpleText.drawString("Press LCTRL or E to continue", 690, 475);
		}
		if (gamePaused) {
			rendermenu();
		}
	}
	
	private void shoot() {
		float rx = cam.getRx();
		float ry = cam.getRy();
		float tx = cam.getTx();
		float ty = cam.getTy();
		float tz = cam.getTz();
		if (aiming) {
			if (shootSpeed > 25) {
				tx += 0.16f * (float) Math.cos(Math.toRadians(-cam.getRy()));
				ty += 1.8f;
				tz += 0.16f * (float) Math.sin(Math.toRadians(-cam.getRy()));
			} else if (shootSpeed > 5) {
				tx += 0.09f * (float) Math.cos(Math.toRadians(-cam.getRy()));
				ty += 1.9f;
				tz += 0.09f * (float) Math.sin(Math.toRadians(-cam.getRy()));
			} else {
				ty += 1.96f;
			}
		} else {
			tx += 0.09f * (float) Math.cos(Math.toRadians(-cam.getRy()));
			ty += 1.915f;
			tz += 0.09f * (float) Math.sin(Math.toRadians(-cam.getRy()));
		}
		switch (multiShot) {
		case 1:
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry     + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			break;
		case 2:
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry - 2 + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty , tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry + 2 + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			break;
		case 3:
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry - 3 + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry     + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry + 3 + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			break;
		case 4:
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry - 3 + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry - 1 + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry + 1 + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry + 3 + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			break;
		case 5:
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry - 6 + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry - 3 + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry     + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry + 3 + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			bullets.add(Bullet.getByRotation(rx + (float) ((Math.random() - 0.5) * (accuracy + recoil)), ry + 6 + (float) ((Math.random() - 0.5) * (accuracy + recoil)), tx, ty, tz, defaultBulletHealth, bulletSpeed * 0.1f, true));
			break;
		default:
			Main.printWarning("Failed to shoot multishot(" + String.valueOf(multiShot) + ")");
		}
	}
	private void throwGrenade() {
		grenades.add(Grenade.getByRotation(cam.getRx(), cam.getRy(), cam.getTx(), cam.getTy() + 2, cam.getTz(), 0.05f));
	}
	private void spawnZombie() {
		int edge = 1 + (int) (Math.random() * ((4 - 1) + 1));
		int x = 1 + (int) (Math.random() * ((127 - 0) + 1));
		int lvl = (int) (Math.floor(Math.random() * spawnSpeed / 10));
		switch (edge) {
		case 1:
			zombies.add(new Zombie(x, 1, lvl));
			break;
		case 2:
			zombies.add(new Zombie(x, 127, lvl));
			break;
		case 3:
			zombies.add(new Zombie(1, x, lvl));
			break;
		case 4:
			zombies.add(new Zombie(127, x, lvl));
			break;
		default:
			Main.printWarning("Failed to spawn Zombie: Unknown Edge");
		}
	}
	
	private void rendermenu() {
		glColor3f(0, 1, 0);// Help
		glBegin(GL_QUADS);
			glVertex2i(400, 850);
			glVertex2i(400, 750);
			glVertex2i(1200, 750);
			glVertex2i(1200, 850);
			
			glVertex2i(400, 740);
			glVertex2i(400, 728);
			glVertex2i(450, 728);
			glVertex2i(450, 740);
		glEnd();
		
		glColor3f(0, 1, 0);// Control
		glBegin(GL_LINE_LOOP);
			glVertex2i(1250, 850);
			glVertex2i(1250, 250);
			glVertex2i(1550, 250);
			glVertex2i(1550, 850);
		glEnd();
		
		glColor3f(0, 1, 0);// Menu
		glBegin(GL_LINE_LOOP);
			glVertex2i(50, 850);
			glVertex2i(50, 250);
			glVertex2i(350, 250);
			glVertex2i(350, 850);
		glEnd();
		
		glBegin(GL_QUADS);
			glVertex2i(180, 840);
			glVertex2i(180, 826);
			glVertex2i(220, 826);
			glVertex2i(220, 840);
			
			glVertex2i(150, 820);
			glVertex2i(150, 806);
			glVertex2i(250, 806);
			glVertex2i(250, 820);
			
			if (shootSpeed < 30) {// shootSpeed // Buttons
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(70, 790);
			glVertex2i(70, 762);
			glVertex2i(330, 762);
			glVertex2i(330, 790);
			
			if (defaultBulletHealth < 10) {// defaultBulletHealth
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(70, 750);
			glVertex2i(70, 722);
			glVertex2i(330, 722);
			glVertex2i(330, 750);
			
			if (bulletSpeed < 4) {// bulletSpeed
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(70, 710);
			glVertex2i(70, 682);
			glVertex2i(330, 682);
			glVertex2i(330, 710);

			if (!canmodifyrunfactor) {// canmodifyruntimefactor
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(70, 670);
			glVertex2i(70, 642);
			glVertex2i(330, 642);
			glVertex2i(330, 670);
			
			if (multiShot < 5) {// multiShot
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(70, 630);
			glVertex2i(70, 602);
			glVertex2i(330, 602);
			glVertex2i(330, 630);
			
			if (maxHp < 1000) {// maxHp
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(70, 590);
			glVertex2i(70, 562);
			glVertex2i(330, 562);
			glVertex2i(330, 590);
			
			if (runtimereg < 0.6f) {// runtimereg
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(70, 550);
			glVertex2i(70, 522);
			glVertex2i(330, 522);
			glVertex2i(330, 550);
			
			if (armor > 1) {// armor
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(70, 510);
			glVertex2i(70, 482);
			glVertex2i(330, 482);
			glVertex2i(330, 510);

			if (!jetpack) {// jetpack
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(70, 470);
			glVertex2i(70, 442);
			glVertex2i(330, 442);
			glVertex2i(330, 470);

			if (runtimecap < 3600) {// stamina capacity
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(70, 430);
			glVertex2i(70, 402);
			glVertex2i(330, 402);
			glVertex2i(330, 430);

			if (accuracylvl > 1) {// accuracy
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(70, 390);
			glVertex2i(70, 362);
			glVertex2i(330, 362);
			glVertex2i(330, 390);

			if (recoillvl > 4) {// recoil
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(70, 350);
			glVertex2i(70, 322);
			glVertex2i(330, 322);
			glVertex2i(330, 350);
			
			if (hp < maxHp) {// Medipack
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(1270, 790);
			glVertex2i(1270, 762);
			glVertex2i(1530, 762);
			glVertex2i(1530, 790);
			
			if (items[1] < 100) {// Sentry
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(1270, 750);
			glVertex2i(1270, 722);
			glVertex2i(1530, 722);
			glVertex2i(1530, 750);
			
			if (items[2] < 100) {// C4
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(1270, 710);
			glVertex2i(1270, 682);
			glVertex2i(1530, 682);
			glVertex2i(1530, 710);
			
			if (items[3] < 100) {// Web
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(1270, 670);
			glVertex2i(1270, 642);
			glVertex2i(1530, 642);
			glVertex2i(1530, 670);
			
			if (grenadeamt < 100) {// Grenade
				glColor3f(0, 1, 0);
			} else {
				glColor3f(1, 0, 0);
			}
			
			glVertex2i(1270, 630);
			glVertex2i(1270, 602);
			glVertex2i(1530, 602);
			glVertex2i(1530, 630);
		glEnd();
		glBindTexture(GL_TEXTURE_2D, 0);
		
		glColor3f(0, 0, 1);// Text
		SimpleText.drawString("SHOP", 184, 828);
		SimpleText.drawString("COINS:" + String.valueOf(coins), 152, 808);
		
		SimpleText.drawString("SHOOTING FREQUENCY - " + String.valueOf(shootSpeedCost) + " COINS", 72, 778);
		SimpleText.drawString("INCREASES SHOOTING FREQUENCY", 72, 764);
		
		SimpleText.drawString("MULTIHIT - 500 COINS", 72, 738);
		SimpleText.drawString("BULLETS CAN HIT MULTIPLE ENEMIES", 72, 724);
		
		SimpleText.drawString("BULLET SPEED - 100 COINS", 72, 698);
		SimpleText.drawString("BULLETS FLY FASTER", 72, 684);
		
		SimpleText.drawString("RUN SPEED MODIFIER - 750 COINS", 72, 658);
		SimpleText.drawString("+ FOR INCREASE, - FOR DECREASE", 72, 644);
		
		SimpleText.drawString("MULTISHOT - 500 COINS", 72, 618);
		SimpleText.drawString("FIRES MULTIPLE BULLETS AT ONCE", 72, 604);
		
		SimpleText.drawString("INCREASE MAX HP - 5000 COINS", 72, 578);
		SimpleText.drawString("INCREASES MAXIMUM HEALTH POINTS", 72, 564);
		
		SimpleText.drawString("INCREASE STAMINA REG - 100 COINS", 72, 538);
		SimpleText.drawString("STAMINA REGENERATES FASTER", 72, 524);
		
		SimpleText.drawString("ARMOR - 10000 COINS", 72, 498);
		SimpleText.drawString("YOU WILL GET LESS DAMAGE", 72, 484);
		
		SimpleText.drawString("JETPACK - 1500 COINS", 72, 458);
		SimpleText.drawString("LET YOU FLY, PRESS SPACE TO USE", 72, 444);
		
		SimpleText.drawString("INCREASE STAMINA CAP - 250 COINS", 72, 418);
		SimpleText.drawString("YOU CAN STORE MORE STAMINA", 72, 404);
		
		SimpleText.drawString("ACCURACY - 100 COINS", 72, 378);
		SimpleText.drawString("YOU SHOOT MORE ACCURATE", 72, 364);
		
		SimpleText.drawString("RECOIL - 200 COINS", 72, 338);
		SimpleText.drawString("YOU HAVE LESS RECOIL", 72, 324);
		
		SimpleText.drawString("MEDIPACK - " + String.valueOf(medikitCost) + " COINS", 1272, 778);
		SimpleText.drawString("FILLS UP YOUR HP", 1272, 764);
		
		SimpleText.drawString("SENTRYGUN - ITEM - 500 COINS", 1272, 738);
		SimpleText.drawString("SHOOTS AT YOUR ENEMIES", 1272, 724);
		
		SimpleText.drawString("C4 EXPLOSIVE - ITEM - 100 COINS", 1272, 698);
		SimpleText.drawString("DETONATE BY PRESSING C", 1272, 684);
		
		SimpleText.drawString("10 WEBS - ITEM - 250 COINS", 1272, 658);
		SimpleText.drawString("IT CAN HOLD YOUR ENEMIES BACK", 1272, 644);
		
		SimpleText.drawString("GRENADE - ITEM - 100 COINS", 1272, 618);
		SimpleText.drawString("THROW BY PRESSING G", 1272, 604);
		
		
		SimpleText.drawString("CONTROLS", 410, 830);
		SimpleText.drawString("W A S D", 410, 810);
		SimpleText.drawString("LEFT MOUSE", 410, 800);
		SimpleText.drawString("F", 410, 790);
		SimpleText.drawString("NUMBERS", 410, 780);
		SimpleText.drawString("WALK", 500, 810);
		SimpleText.drawString("SHOOT", 500, 800);
		SimpleText.drawString("USE ITEM", 500, 790);
		SimpleText.drawString("SELECT ITEM", 500, 780);
		
		SimpleText.drawString("EXIT", 402, 730);
	}
	
	private void listenmenu() {
		if (Mouse.isButtonDown(0)) {
			if (!menulmousedown || Mouse.isButtonDown(1)) {
				if (Main.mouseX >= 70 && Main.mouseX <= 330 && Main.mouseY >= 762 && Main.mouseY <= 790 && shootSpeed < 30) {
					if (coins >= shootSpeedCost) {
						coins -= shootSpeedCost;
						shootSpeed++;
						shootSpeedCost = (int) Math.pow(1.4, shootSpeed - 3) + 40;
					}
				} else if (Main.mouseX >= 70 && Main.mouseX <= 330 && Main.mouseY >= 722 && Main.mouseY <= 750 && defaultBulletHealth < 10) {
					if (coins >= 500) {
						coins -= 500;
						defaultBulletHealth++;
					}
				} else if (Main.mouseX >= 70 && Main.mouseX <= 330 && Main.mouseY >= 682 && Main.mouseY <= 710 && bulletSpeed < 4) {
					if (coins >= 100) {
						coins -= 100;
						bulletSpeed++;
					}
				} else if (Main.mouseX >= 70 && Main.mouseX <= 330 && Main.mouseY >= 642 && Main.mouseY <= 670 && !canmodifyrunfactor) {
					if (coins >= 750) {
						coins -= 750;
						canmodifyrunfactor = true;
					}
				} else if (Main.mouseX >= 70 && Main.mouseX <= 330 && Main.mouseY >= 602 && Main.mouseY <= 630 && multiShot < 5) {
					if (coins >= 500) {
						coins -= 500;
						multiShot++;
					}
				} else if (Main.mouseX >= 70 && Main.mouseX <= 330 && Main.mouseY >= 562 && Main.mouseY <= 590 && maxHp < 1000) {
					if (coins >= 5000) {
						coins -= 5000;
						maxHp += 100;
					}
				} else if (Main.mouseX >= 70 && Main.mouseX <= 330 && Main.mouseY >= 522 && Main.mouseY <= 550 && runtimereg < 0.6f) {
					if (coins >= 100) {
						coins -= 100;
						runtimereg += 0.1f;
					}
				} else if (Main.mouseX >= 70 && Main.mouseX <= 330 && Main.mouseY >= 482 && Main.mouseY <= 510 && armor > 1) {
					if (coins >= 10000) {
						coins -= 10000;
						armor--;
					}
				} else if (Main.mouseX >= 70 && Main.mouseX <= 330 && Main.mouseY >= 442 && Main.mouseY <= 470 && !jetpack) {
					if (coins >= 1500) {
						coins -= 1500;
						jetpack = true;
					}
				} else if (Main.mouseX >= 70 && Main.mouseX <= 330 && Main.mouseY >= 402 && Main.mouseY <= 430 && runtimecap < 3600) {
					if (coins >= 250) {
						coins -= 250;
						runtimecap += 360;
					}
				} else if (Main.mouseX >= 70 && Main.mouseX <= 330 && Main.mouseY >= 362 && Main.mouseY <= 390 && accuracylvl > 1) {
					if (coins >= 100) {
						coins -= 100;
						accuracylvl--;
					}
				} else if (Main.mouseX >= 70 && Main.mouseX <= 330 && Main.mouseY >= 322 && Main.mouseY <= 350 && recoillvl > 4) {
					if (coins >= 200) {
						coins -= 200;
						recoillvl--;
					}
				} else if (Main.mouseX >= 1270 && Main.mouseX <= 1530 && Main.mouseY >= 762 && Main.mouseY <= 790 && hp < maxHp) {
					if (coins >= medikitCost) {
						coins -= medikitCost;
						hp = maxHp;
						medikitCost *= 10;
					}
				} else if (Main.mouseX >= 1270 && Main.mouseX <= 1530 && Main.mouseY >= 722 && Main.mouseY <= 750 && items[1] < 100) {
					if (coins >= 500) {
						coins -= 500;
						items[1]++;
					}
				} else if (Main.mouseX >= 1270 && Main.mouseX <= 1530 && Main.mouseY >= 682 && Main.mouseY <= 710 && items[2] < 100) {
					if (coins >= 100) {
						coins -= 100;
						items[2]++;
					}
				} else if (Main.mouseX >= 1270 && Main.mouseX <= 1530 && Main.mouseY >= 642 && Main.mouseY <= 670 && items[3] < 100) {
					if (coins >= 250) {
						coins -= 250;
						items[3] += 10;
					}
				} else if (Main.mouseX >= 1270 && Main.mouseX <= 1530 && Main.mouseY >= 602 && Main.mouseY <= 630 && grenadeamt < 100) {
					if (coins >= 100) {
						coins -= 100;
						grenadeamt++;
					}
				}
			}
			menulmousedown = true;
		} else {
			menulmousedown = false;
		}
	}
	
	public float getPx() {
		return cam.getTx();
	}
	public float getPy() {
		return cam.getTy();
	}
	public float getPz() {
		return cam.getTz();
	}
	public void increaseScore(int amount) {
		score += amount;
	}
	public void increaseCoins(int amount) {
		coins += amount;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public void decreaseHealth() {
		this.hp -= armor;
	}
	public void setBackgred(float backgred) {
		this.backgred = backgred;
	}
	public ArrayList<Zombie> getZombies() {
		return zombies;
	}
	public ArrayList<Bullet> getBullets() {
		return bullets;
	}
	
	public float[][] loadPhysModel(String path, boolean forPlayer) throws IOException {
		BufferedReader res = new BufferedReader(new FileReader(path));
		ArrayList<float[]> vertices = new ArrayList<float[]>();
		ArrayList<float[]> objects = new ArrayList<float[]>();
		
		String line;
		while ((line = res.readLine()) != null) {
			if (!line.isEmpty() && !line.startsWith("#")) {
				
				String[] args = line.split(" ");
				
				if (args[0].equals("v") && args.length == 4) {
					float[] entry = {Float.valueOf(args[1]), Float.valueOf(args[2]), Float.valueOf(args[3])};
					vertices.add(entry);
				}
				if (args[0].equals("o") && args.length == 3) {
					float x1 = vertices.get(Integer.valueOf(args[1]) - 1)[0];
					float x2 = vertices.get(Integer.valueOf(args[2]) - 1)[0];
					float y1 = vertices.get(Integer.valueOf(args[1]) - 1)[1];
					float y2 = vertices.get(Integer.valueOf(args[2]) - 1)[1];
					float z1 = vertices.get(Integer.valueOf(args[1]) - 1)[2];
					float z2 = vertices.get(Integer.valueOf(args[2]) - 1)[2];
					
					float xmin, xmax, ymin, ymax, zmin, zmax;
					
					if (x1 > x2) {
						xmin = x2;
						xmax = x1;
					} else {
						xmin = x1;
						xmax = x2;
					}
					if (y1 > y2) {
						ymin = y2;
						ymax = y1;
					} else {
						ymin = y1;
						ymax = y2;
					}
					if (z1 > z2) {
						zmin = z2;
						zmax = z1;
					} else {
						zmin = z1;
						zmax = z2;
					}
					
					if (forPlayer) ymin -= 2; // Player height
					
					float[] entry = {xmin, xmax, ymin, ymax, zmin, zmax};
					objects.add(entry);
				}
			}
		}
		res.close();
		
		float[][] output = new float[objects.size()][6];
		for (int i = 0; i < objects.size(); i++) {
			output[i] = objects.get(i);
		}
		
		return output;
	}
}
