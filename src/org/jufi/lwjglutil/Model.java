package org.jufi.lwjglutil;

import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Model {
	private ArrayList<float[]> vertices = new ArrayList<float[]>();
	private ArrayList<float[]> vtextures = new ArrayList<float[]>();
	private ArrayList<float[]> normals = new ArrayList<float[]>();
	private ArrayList<ModelCommand> commands = new ArrayList<ModelCommand>();
	private ArrayList<Material> materials = new ArrayList<Material>();
	
	public Model(String path) throws IOException {
			BufferedReader res = new BufferedReader(new FileReader(path));
			String line;
			
			float[] firstValue = {0, 0, 0};
			float[] vfirstValue = {0, 0};
			vertices.add(firstValue);
			vtextures.add(vfirstValue);
			normals.add(firstValue);
			while ((line = res.readLine()) != null) {
				readln(line);
			}
			res.close();
	}
	
	public void render() {
		glBegin(GL_TRIANGLES);
			for (ModelCommand command : commands) {
				command.execute(this);
			}
		glEnd();
	}
	
	public void renderFace(Face face) {
		if (face.getNormals()[0] != 0) glNormal3f(normals.get(face.getNormals()[0])[0], normals.get(face.getNormals()[0])[1], normals.get(face.getNormals()[0])[2]);
		if (face.getTextures()[0] != 0) glTexCoord2f(vtextures.get(face.getTextures()[0])[0], vtextures.get(face.getTextures()[0])[1]);
		glVertex3f(vertices.get(face.getVertices()[0])[0], vertices.get(face.getVertices()[0])[1], vertices.get(face.getVertices()[0])[2]);
		
		if (face.getNormals()[1] != 0) glNormal3f(normals.get(face.getNormals()[1])[0], normals.get(face.getNormals()[1])[1], normals.get(face.getNormals()[1])[2]);
		if (face.getTextures()[1] != 0) glTexCoord2f(vtextures.get(face.getTextures()[1])[0], vtextures.get(face.getTextures()[1])[1]);
		glVertex3f(vertices.get(face.getVertices()[1])[0], vertices.get(face.getVertices()[1])[1], vertices.get(face.getVertices()[1])[2]);
		
		if (face.getNormals()[2] != 0) glNormal3f(normals.get(face.getNormals()[2])[0], normals.get(face.getNormals()[2])[1], normals.get(face.getNormals()[2])[2]);
		if (face.getTextures()[2] != 0) glTexCoord2f(vtextures.get(face.getTextures()[2])[0], vtextures.get(face.getTextures()[2])[1]);
		glVertex3f(vertices.get(face.getVertices()[2])[0], vertices.get(face.getVertices()[2])[1], vertices.get(face.getVertices()[2])[2]);
	}
	
	private void readln(String line) throws IOException {
		if (line.isEmpty() || line.startsWith("#")) {
			return;
		}
		
		String[] args = line.split(" ");
		
		if (args[0].equals("v") && args.length == 4) {
			float[] value = {Float.valueOf(args[1]), Float.valueOf(args[2]), Float.valueOf(args[3])};
			vertices.add(value);
		}
		if (args[0].equals("vn") && args.length == 4) {
			float[] value = {Float.valueOf(args[1]), Float.valueOf(args[2]), Float.valueOf(args[3])};
			normals.add(value);
		}
		if (args[0].equals("vt") && args.length == 3) {
			float[] value = {Float.valueOf(args[1]), Float.valueOf(args[2])};
			vtextures.add(value);
		}
		if (args[0].equals("f") && args.length == 4) {
			int[] v = new int[args.length - 1];
			int[] vt = new int[args.length - 1];
			int[] vn = new int[args.length - 1];
			for (int i = 0; i < vt.length; i++) vt[i] = 0;
			for (int i = 1; i <= 3; i++) {
				if (args[i].contains("/")) {
					if (args[i].contains("//")) {
						String[] splitted = args[i].split("//");
						v[i - 1] = Integer.valueOf(splitted[0]);
						vn[i - 1] = Integer.valueOf(splitted[1]);
					} else {
						String[] splitted = args[i].split("/");
						v[i - 1] = Integer.valueOf(splitted[0]);
						if (splitted.length >= 2) {
							vt[i - 1] = Integer.valueOf(splitted[1]);
						}
						if (splitted.length == 3) {
							vn[i - 1] = Integer.valueOf(splitted[2]);
						}
					}
				} else {
					v[i - 1] = Integer.valueOf(args[i]);
				}
			}
			commands.add(new Face(v, vt, vn));
		}
		if (args[0].equals("mtllib") && args.length == 2) {
			Material.readFile(materials, System.getProperty("user.dir") + "/res/" + args[1]);
		}
		if (args[0].equals("usemtl") && args.length == 2) {
			Material mtl = null;
			for (Material mat : materials) {
				if (mat.name.equals(args[1])) mtl = mat;
			}
			commands.add(new UseMaterial(mtl));
		}
	}
}
