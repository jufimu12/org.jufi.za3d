package org.jufi.lwjglutil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

public class Material {
	public final String name;
	public FloatBuffer ka = BufferUtils.createFloatBuffer(4);
	public FloatBuffer kd = BufferUtils.createFloatBuffer(4);
	public FloatBuffer ks = BufferUtils.createFloatBuffer(4);
	public int texture = 0;
	
	public Material(String name) {
		this.name = name;
		ka.put(1).put(1).put(1).put(1).flip();
		kd.put(1).put(1).put(1).put(1).flip();
		ks.put(1).put(1).put(1).put(1).flip();
	}
	
	public static void readFile(ArrayList<Material> materials, String path) throws IOException {
		BufferedReader mtlfile = new BufferedReader(new FileReader(path));
		String line;
		
		while ((line = mtlfile.readLine()) != null) {
			if (!(line.isEmpty() || line.startsWith("#"))) {
				String[] args = line.split(" ");
				
				if (args[0].equals("newmtl") && args.length == 2) {
					materials.add(new Material(args[1]));
				}
				if (args[0].equals("Ka") && args.length == 4) {
					materials.get(materials.size() - 1).ka = BufferUtils.createFloatBuffer(4);
					materials.get(materials.size() - 1).ka.put(Float.valueOf(args[1])).put(Float.valueOf(args[2])).put(Float.valueOf(args[3])).put(1).flip();
				}
				if (args[0].equals("Kd") && args.length == 4) {
					materials.get(materials.size() - 1).kd = BufferUtils.createFloatBuffer(4);
					materials.get(materials.size() - 1).kd.put(Float.valueOf(args[1])).put(Float.valueOf(args[2])).put(Float.valueOf(args[3])).put(1).flip();
				}
				if (args[0].equals("Ks") && args.length == 4) {
					materials.get(materials.size() - 1).ks = BufferUtils.createFloatBuffer(4);
					materials.get(materials.size() - 1).ks.put(Float.valueOf(args[1])).put(Float.valueOf(args[2])).put(Float.valueOf(args[3])).put(1).flip();
				}
				if ((args[0].equals("map_Ka") || args[0].equals("map_Kd")) && args.length == 2) {
					int texture = ResourceLoader.loadTexture(System.getProperty("user.dir") + "/res/" + args[1]);
					materials.get(materials.size() - 1).texture = texture;
				}
			}
		}
		
		mtlfile.close();
	}
}
