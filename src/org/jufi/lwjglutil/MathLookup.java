package org.jufi.lwjglutil;

public class MathLookup {
	private static double[] sin = new double[361];
	private static double[] cos = new double[361];
	private static double[] tan = new double[361];
	
	static {
		for (int i = 0; i <= 360; i++) {
			sin[i] = Math.sin(Math.toRadians(i));
			cos[i] = Math.cos(Math.toRadians(i));
	        tan[i] = Math.tan(Math.toRadians(i));
		}
	}
	
	public static double sin(int angle) {
		return sin[angle % 360];
	}
	public static double cos(int angle) {
		return cos[angle % 360];
	}
	public static double tan(int angle) {
		return tan[angle % 360];
	}
	
	public static double sin(float angle) {
		return sin[(int)angle % 360];
	}
	public static double cos(float angle) {
		return cos[(int)angle % 360];
	}
	public static double tan(float angle) {
		return tan[(int)angle % 360];
	}
	
	public static double sin(double angle) {
		return sin[(int)angle % 360];
	}
	public static double cos(double angle) {
		return cos[(int)angle % 360];
	}
	public static double tan(double angle) {
		return tan[(int)angle % 360];
	}
	
	public static double sin(byte angle) {
		return sin[(int)angle % 360];
	}
	public static double cos(byte angle) {
		return cos[(int)angle % 360];
	}
	public static double tan(byte angle) {
		return tan[(int)angle % 360];
	}

	public static double sin(short angle) {
		return sin[(int)angle % 360];
	}
	public static double cos(short angle) {
		return cos[(int)angle % 360];
	}
	public static double tan(short angle) {
		return tan[(int)angle % 360];
	}

	public static double sin(long angle) {
		return sin[(int)angle % 360];
	}
	public static double cos(long angle) {
		return cos[(int)angle % 360];
	}
	public static double tan(long angle) {
		return tan[(int)angle % 360];
	}
	
	
	private MathLookup() {
		
	}
}
