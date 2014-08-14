package org.jufi.za3d;

import java.util.ArrayList;

import org.jufi.lwjglutil.*;

public class Zombie {
	private float px, py, pz, red, green, blue, speed;
	private int health, coins, doNotMove, shootCooldown;
	private final boolean shootAtPlayer;
	
	public Zombie(float px, float pz, int level) {
		this.px = px;
		this.py = 0;
		this.pz = pz;
		this.doNotMove = 0;
		this.shootCooldown = 2500;
		switch (level) {
			case 1:// 2xSpeed
				this.red = 0;
				this.green = 1;
				this.blue = 0;
				this.speed = 0.024f;
				this.health = 1;
				this.coins = 2;
				this.shootAtPlayer = false;
				break;
			case 2:// 5xHealth
				this.red = 0;
				this.green = 0.2f;
				this.blue = 1;
				this.speed = 0.012f;
				this.health = 5;
				this.coins = 3;
				this.shootAtPlayer = false;
				break;
			case 3:// 3xSpeed, 2x Health
				this.red = 0.2f;
				this.green = 1;
				this.blue = 0;
				this.speed = 0.036f;
				this.health = 2;
				this.coins = 6;
				this.shootAtPlayer = false;
				break;
			case 4:// 20xHealth
				this.red = 0;
				this.green = 0;
				this.blue = 1;
				this.speed = 0.012f;
				this.health = 20;
				this.coins = 8;
				this.shootAtPlayer = false;
				break;
			case 5:// 3xSpeed, 50xHealth
				this.red = 0.7f;
				this.green = 0;
				this.blue = 1;
				this.speed = 0.036f;
				this.health = 50;
				this.coins = 12;
				this.shootAtPlayer = false;
				break;
			case 6:// 3xSpeed, 250xHealth
				this.red = 0.7f;
				this.green = 0.5f;
				this.blue = 1;
				this.speed = 0.036f;
				this.health = 250;
				this.coins = 20;
				this.shootAtPlayer = true;
				break;
			case 7:// 3xSpeed, 1000xHealth
				this.red = 0;
				this.green = 1;
				this.blue = 1;
				this.speed = 0.036f;
				this.health = 1000;
				this.coins = 100;
				this.shootAtPlayer = true;
				break;
			case 8:// 5xSpeed, 1000xHealth
				this.red = 1;
				this.green = 0;
				this.blue = 0;
				this.speed = 0.06f;
				this.health = 1000;
				this.coins = 200;
				this.shootAtPlayer = false;
				break;
			default:
				this.red = 1;
				this.green = 1;
				this.blue = 1;
				this.speed = 0.012f;
				this.health = 1;
				this.coins = 1;
				this.shootAtPlayer = false;
		}
	}
	
	public boolean tick(ArrayList<Bullet> bullets, float playerX, float playerY, float playerZ, PhysMap physmap) {
		for (int i = 0; i < bullets.size(); i++) {
			if (bullets.get(i).getPx() > px - 0.4f && bullets.get(i).getPx() < px + 0.4f && bullets.get(i).getPy() > py && bullets.get(i).getPy() < py + 2 && bullets.get(i).getPz() > pz - 0.4f && bullets.get(i).getPz() < pz + 0.4f && !bullets.get(i).isShotbyzombie()) {
				bullets.get(i).decreaseHealth(1);
				health--;
				Main.game.increaseScore(1);
				if (bullets.get(i).shotByPlayer()) {
					Main.game.increaseCoins(coins);
				}
				if (health <= 0) {
					return false;
				}
			}
		}
		if (playerX > px - 0.7f && playerX < px + 0.7f && playerY > py - 2 && playerY < py + 2 && playerZ > pz - 0.7f && playerZ < pz + 0.7f) {
			Main.game.decreaseHealth();
			Main.game.setBackgred(1);
			health -= 5;
			if (health <= 0) {
				return false;
			}
		}
		if (health <= 0) {
			return false;
		}
		
		if (doNotMove <= 0) {
			float a = playerX - px;
			float b = playerZ - pz;
			float c = (float) Math.sqrt((a * a) + (b * b));
			float opx = px, opz = pz;
			px += speed / c * (a);
			pz += speed / c * (b);
			if (physmap.collides(px, py, pz) && !physmap.collides(opx, py, opz)) {
				px = opx;
				pz = opz;
			}
		}
		if (doNotMove > 0) {
			doNotMove--;
		} else {
			doNotMove = 0;
		}
		if (shootAtPlayer) {
			shootCooldown--;
			if (shootCooldown <= 0) {
				shoot();
				shootCooldown = 2000;
			}
		}
		return true;
	}
	
	
	private void shoot() {
		float dx = Main.game.getPx() - px;
		float dy = Main.game.getPy() + 0.6f - py;
		float dz = Main.game.getPz() - pz;
		float dxyz = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		float vx = dx * 0.1f / dxyz;
		float vy = dy * 0.1f / dxyz;
		float vz = dz * 0.1f / dxyz;
		Bullet bullet = new Bullet(vx, vy, vz, px, py + 1.8f, pz, 1, false);
		bullet.setShotbyzombie(true);
		Main.game.getBullets().add(bullet);
	}
	public float getPx() {
		return px;
	}
	public void setPx(float px) {
		this.px = px;
	}
	public float getPy() {
		return py;
	}
	public void setPy(float py) {
		this.py = py;
	}
	public float getPz() {
		return pz;
	}
	public void setPz(float pz) {
		this.pz = pz;
	}
	public float getCr() {
		return red;
	}
	public float getCg() {
		return green;
	}
	public float getCb() {
		return blue;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public void doNotMove(int time) {
		this.doNotMove = time;
	}
	public boolean collidesWith(float[] points) {
		// float[] points = {xmin, xmax, ymin, ymax, zmin, zmax};
		if (px > points[0] && px < points[1] && py > points[2] && py < points[3] && pz > points[4] && pz < points[5]) return true;
		else return false;
	}
	
	public boolean collidesWith(float[] points, float x, float y, float z) {
		// float[] points = {xmin, xmax, ymin, ymax, zmin, zmax};
		if (x > points[0] && x < points[1] && y > points[2] && y < points[3] && z > points[4] && z < points[5]) return true;
		else return false;
	}
}
