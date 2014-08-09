package org.jufi.za3d;

import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glCallList;

public class CExplosive extends Block {

	

	public CExplosive(float px, float py, float pz) {
		super(px, py, pz, 2, 0, 0.2f, 0.1f);
	}

	@Override
	public boolean tick() {
		if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
			for (int i = 0; i < Main.game.getZombies().size(); i++) {
				float dx = Main.game.getZombies().get(i).getPx() - px;
				float dy = Main.game.getZombies().get(i).getPy() + 1 - py;
				float dz = Main.game.getZombies().get(i).getPz() - pz;
				float dxyz = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
				Main.game.getZombies().get(i).setHealth(Main.game.getZombies().get(i).getHealth() - (int) Math.floor(15 / dxyz));
			}
			return false;
		}
		return true;
	}

	@Override
	public void render() {
		glPushMatrix();
			glTranslatef(px, py, pz);
			glCallList(Main.dl_cexplosive);
		glPopMatrix();
	}

}
