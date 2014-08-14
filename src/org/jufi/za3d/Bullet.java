package org.jufi.za3d;

import org.jufi.lwjglutil.*;

public class Bullet {
	private float px, py, pz, vx, vy, vz;
	private int health;
	private final boolean shotByPlayer;
	private boolean shotByZombie = false;
	
	public Bullet(float vx, float vy, float vz, float px, float py, float pz, int initialHealth, boolean shotByPlayer) {
		this.vx = vx;
		this.vy = vy;
		this.vz = vz;
		this.px = px;
		this.py = py;
		this.pz = pz;
		this.health = initialHealth;
		this.shotByPlayer = shotByPlayer;
	}
	
	public boolean tick(float playerX, float playerY, float playerZ, PhysMap physmap) {
		vy -= 0.0001f;
		px += vx;
		py += vy;
		pz += vz;
		if (px < Render.ZA_FLOOR_START || px > Render.ZA_FLOOR_END || py < 0 || py > 128 || pz < Render.ZA_FLOOR_START || pz > Render.ZA_FLOOR_END || health <= 0 || physmap.collides(px, py, pz)) {
			return false;
		}
		if (Math.floor(playerX - px) < 0.4f && Math.floor(playerZ - pz) < 0.4f && playerY < py && playerY + 2 > py && shotByZombie) {
			Main.game.decreaseHealth();
			Main.game.setBackgred(1);
			return false;
		}
		return true;
	}
	
	public float getPx() {
		return px;
	}
	public float getPy() {
		return py;
	}
	public float getPz() {
		return pz;
	}
	public boolean shotByPlayer() {
		return this.shotByPlayer;
	}
	public void decreaseHealth(int amount) {
		health -= amount;
	}
	public void setShotbyzombie(boolean shotByZombie) {
		this.shotByZombie = shotByZombie;
	}
	public boolean isShotbyzombie() {
		return shotByZombie;
	}
	
	public static Bullet getByRotation(float rx, float ry, float px, float py, float pz, int initialHealth, float speed, boolean shotByPlayer) {
		float vxz = (float) Math.cos(Math.toRadians(rx)) * speed;
		float vy = (float) Math.sin(Math.toRadians(rx)) * speed;
		float vx = (float) -Math.sin(Math.toRadians(ry)) * vxz;
		float vz = (float) -Math.cos(Math.toRadians(ry)) * vxz;
		return new Bullet(vx, vy, vz, px, py, pz, initialHealth, shotByPlayer);
	}
}
