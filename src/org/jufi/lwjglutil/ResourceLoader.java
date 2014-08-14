package org.jufi.lwjglutil;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL20.*;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.WaveData;

public class ResourceLoader {
	public static int whitePixelTexID;// Texture
	
	private static int generateId() {
		return glGenTextures();
	}
	public static int loadTexture(String path) throws IOException {
		int texId = generateId();
		
		BufferedImage image = ImageIO.read(new File(path));
		int[] pixels = new int[image.getHeight() * image.getWidth()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
		
		int pixel = 0;
		for (int y = 0; y < image.getWidth(); y++) {
			for (int x = 0; x < image.getHeight(); x++) {
				pixel = pixels[y * image.getHeight() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		
		buffer.flip();
		
		glBindTexture(GL_TEXTURE_2D, texId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		return texId;
	}
	public static ByteBuffer loadTextureIntoByteBuffer(String path) throws IOException {
		BufferedImage image = ImageIO.read(new File(path));
		int[] pixels = new int[image.getHeight() * image.getWidth()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
		
		int pixel = 0;
		for (int y = 0; y < image.getWidth(); y++) {
			for (int x = 0; x < image.getHeight(); x++) {
				pixel = pixels[y * image.getHeight() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		
		buffer.flip();
		
		return buffer;
	}
	public static void initWhitePixelTexID() {
		int texId = generateId();
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(4);
		buffer.put((byte) -1).put((byte) -1).put((byte) -1).put((byte) -1).flip();
		glBindTexture(GL_TEXTURE_2D, texId);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		whitePixelTexID = texId;
	}
	
	public static int[] loadShader(String vertexShaderPath, String fragmentShaderPath) throws IOException {// Shader
		int shaderProgram = glCreateProgram();
		int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		
		StringBuilder vertexShaderSource = new StringBuilder();
		StringBuilder fragmentShaderSource = new StringBuilder();
		{
			BufferedReader reader = new BufferedReader(new FileReader(vertexShaderPath));
			String line;
			while ((line = reader.readLine()) != null) {
				vertexShaderSource.append(line).append('\n');
			}
			reader.close();
		}
		{
			BufferedReader reader = new BufferedReader(new FileReader(fragmentShaderPath));
			String line;
			while ((line = reader.readLine()) != null) {
				fragmentShaderSource.append(line).append('\n');
			}
			reader.close();
		}
		glShaderSource(vertexShader,  vertexShaderSource);
		glCompileShader(vertexShader);
		glShaderSource(fragmentShader, fragmentShaderSource);
		glCompileShader(fragmentShader);
		glAttachShader(shaderProgram, vertexShader);
		glAttachShader(shaderProgram, fragmentShader);
		glLinkProgram(shaderProgram);
		glValidateProgram(shaderProgram);
		
		return new int[]{shaderProgram, vertexShader, fragmentShader};
	}
	
	
	
	public static int getSourceFromWav(String path, float volume) throws FileNotFoundException {// Sound
		WaveData data = WaveData.create(new BufferedInputStream(new FileInputStream(path)));
		int buffer = alGenBuffers();
		alBufferData(buffer, data.format, data.data, data.samplerate);
		data.dispose();
		
		int source = alGenSources();
		alSourcei(source, AL_BUFFER, buffer);
		alSourcef(source, AL_GAIN, volume);
		alDeleteBuffers(buffer);
		return source;
	}
	
	public static float[][] loadPhysMap(String path, boolean forPlayer) throws IOException {// Physics
		BufferedReader res = new BufferedReader(new FileReader(path));
		ArrayList<float[]> vertices = new ArrayList<float[]>();
		ArrayList<float[]> objects = new ArrayList<float[]>();
		
		String line;
		while ((line = res.readLine()) != null) {
			if (!line.isEmpty() && !line.startsWith("#")) {
				
				String[] args = line.split(" ");
				
				if (args[0].equals("v") && args.length == 4) {
					float[] entry = {Float.valueOf(args[1]), Float.valueOf(args[2]), Float.valueOf(args[3])};
					vertices.add(entry);
				}
				if (args[0].equals("o") && args.length == 3) {
					float x1 = vertices.get(Integer.valueOf(args[1]) - 1)[0];
					float x2 = vertices.get(Integer.valueOf(args[2]) - 1)[0];
					float y1 = vertices.get(Integer.valueOf(args[1]) - 1)[1];
					float y2 = vertices.get(Integer.valueOf(args[2]) - 1)[1];
					float z1 = vertices.get(Integer.valueOf(args[1]) - 1)[2];
					float z2 = vertices.get(Integer.valueOf(args[2]) - 1)[2];
					
					float xmin, xmax, ymin, ymax, zmin, zmax;
					
					if (x1 > x2) {
						xmin = x2;
						xmax = x1;
					} else {
						xmin = x1;
						xmax = x2;
					}
					if (y1 > y2) {
						ymin = y2;
						ymax = y1;
					} else {
						ymin = y1;
						ymax = y2;
					}
					if (z1 > z2) {
						zmin = z2;
						zmax = z1;
					} else {
						zmin = z1;
						zmax = z2;
					}
					
					if (forPlayer) ymin -= 2; // Player height
					
					float[] entry = {xmin, xmax, ymin, ymax, zmin, zmax};
					objects.add(entry);
				}
			}
		}
		res.close();
		
		float[][] output = new float[objects.size()][6];
		for (int i = 0; i < objects.size(); i++) {
			output[i] = objects.get(i);
		}
		
		return output;
	}
	public static boolean colliding(float[] points, float x, float y, float z) {
		if (x > points[0] && x < points[1] && y > points[2] && y < points[3] && z > points[4] && z < points[5]) return true;
		else return false;
	}
	public static boolean colliding(float[][] physmap, float x, float y, float z) {
		for (float[] physobj : physmap) {
			if (x > physobj[0] && x < physobj[1] && y > physobj[2] && y < physobj[3] && z > physobj[4] && z < physobj[5]) return true;
		}
		return false;
	}
}
