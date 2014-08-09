package org.jufi.lwjglutil;

import static org.lwjgl.opengl.GL11.*;

public class UseMaterial implements ModelCommand {
	
	private Material mtl;
	
	public UseMaterial(Material mtl) {
		this.mtl = mtl;
	}
	
	@Override
	public void execute(Model commander) {
		glEnd();
		glMaterial(GL_FRONT, GL_DIFFUSE, mtl.kd);
		glMaterial(GL_FRONT, GL_AMBIENT, mtl.ka);
		glMaterial(GL_FRONT, GL_SPECULAR, mtl.ks);
		glBindTexture(GL_TEXTURE_2D, mtl.texture);
		glBegin(GL_TRIANGLES);
	}
}
