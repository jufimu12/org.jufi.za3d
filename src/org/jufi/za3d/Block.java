package org.jufi.za3d;

public abstract class Block {
	protected final int type;
	protected final float px, py, pz;
	protected float cr, cg, cb;
	
	public Block(float px, float py, float pz, int type, float cr, float cg, float cb) {
		this.px = px;
		this.py = py;
		this.pz = pz;
		this.type = type;
		this.cr = cr;
		this.cg = cg;
		this.cb = cb;
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
	public int getType() {
		return type;
	}
	public float getCr() {
		return cr;
	}
	public float getCg() {
		return cg;
	}
	public float getCb() {
		return cb;
	}
	public void setCr(float cr) {
		this.cr = cr;
	}
	public void setCg(float cg) {
		this.cg = cg;
	}
	public void setCb(float cb) {
		this.cb = cb;
	}
	
	public abstract boolean tick();
	
	public abstract void render();
	
	public static Block getByType(float px, float py, float pz, int type) {
		switch (type) {
		case 1:
			return new Sentry(px, py, pz);
		case 2:
			return new CExplosive(px, py, pz);
		case 3:
			return new Web(px, py, pz);
		default:
			return null;
		}
	}
}
