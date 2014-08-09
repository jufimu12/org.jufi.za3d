package org.jufi.za3d;

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
	
	public boolean tick(float[][] physmap) {
		timer--;
		vy -= 0.0002f;
		if (!colliding(physmap)) {
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
		float vxz = (float) Math.cos(Math.toRadians(rx)) * speed;
		float vy = (float) Math.sin(Math.toRadians(rx)) * speed;
		float vx = (float) -Math.sin(Math.toRadians(ry)) * vxz;
		float vz = (float) -Math.cos(Math.toRadians(ry)) * vxz;
		return new Grenade(vx, vy, vz, px, py, pz);
	}
	
	public boolean collidesWith(float[] points) {
		// float[] points = {xmin, xmax, ymin, ymax, zmin, zmax};
		if (px > points[0] && px < points[1] && py > points[2] && py < points[3] && pz > points[4] && pz < points[5]) return true;
		else return false;
	}
	public boolean colliding(float[][] physmap) {
		for (float[] physobj : physmap) {
			if (collidesWith(physobj)) return true;
		}
		return false;
	}
}
