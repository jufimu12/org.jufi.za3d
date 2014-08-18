package org.jufi.za3d;

import org.jufi.lwjglutil.*;

public class Grenade {
	private float px, py, pz, vx, vy, vz, burstspeed = 0.2f;
	private int timer = 480;
	
	public Grenade(float vx, float vy, float vz, float px, float py, float pz) {
		this.vx = vx;
		this.vy = vy;
		this.vz = vz;
		this.px = px;
		this.py = py;
		this.pz = pz;
	}
	
	public boolean tick(PhysMap physmap) {
		timer--;
		vy -= 0.0002f;
		if (!physmap.collides(px, py, pz)) {
			px += vx;
			py += vy;
			pz += vz;
		}
		if (py <= 0.2f && vy < 0.1f) {
			vy *= -0.5f;
			vx *= 0.5f;
			vz *= 0.5f;
		}
		if (timer <= 0) {
			for (int i = 1; i < 2500; i++) {
				Main.game.getBullets().add(new Bullet(((float) Math.random() - 0.5f) * burstspeed, ((float) Math.random() - 0.5f) * burstspeed, ((float) Math.random() - 0.5f) * burstspeed, px, py, pz, 2, false));
			}
			return false;
		}
		if (px < 0 || px > 128 || py > 128 || pz < 0 || pz > 128) {
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
	
	public static Grenade getByRotation(float rx, float ry, float px, float py, float pz, float speed) {
		float vxz = (float) MathLookup.cos(rx) * speed;
		float vy = (float) MathLookup.sin(rx) * speed;
		float vx = (float) -MathLookup.sin(ry) * vxz;
		float vz = (float) -MathLookup.cos(ry) * vxz;
		return new Grenade(vx, vy, vz, px, py, pz);
	}
}
