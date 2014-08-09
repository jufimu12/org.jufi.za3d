package org.jufi.lwjglutil;

public class Face implements ModelCommand {
	private final int[] vertices;
	private final int[] textures;
	private final int[] normals;
	
	public Face(int[] vertices, int[] textures, int[] normals) {
		this.vertices = vertices;
		this.textures = textures;
		this.normals = normals;
	}
	
	public int[] getVertices() {
		return vertices;
	}
	
	public int[] getTextures() {
		return textures;
	}
	
	public int[] getNormals() {
		return normals;
	}

	@Override
	public void execute(Model commander) {
		commander.renderFace(this);
	}
}
