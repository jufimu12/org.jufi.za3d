package org.jufi.za3d;

import org.lwjgl.opengl.Display;
import org.lwjgl.openal.AL;

public class Main {
	
	public static GameLauncher launcher;
	public static Game game;
	public static int tex_zombey, tex_floor;
	public static int[] tex_skybox;
	public static int[] wavs_gunfire;
	public static int dl_zombiebody, dl_zombiehead, dl_bullet, dl_floorandmap, dl_grenade, dl_cexplosive;
	public static int[] dl_gun;
	public static int mouseX, mouseY;
	public static boolean enabledebugmode;
	
	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir") + "/natives");
		if (args.length > 0) {
			for (String line : args) {
				if (line.equals("debug")) enabledebugmode = true;
			}
		}
		launcher = new GameLauncher();
	}
	
	public static void onExit() {
		Main.printInfo("Exiting ZombieApocalypse 3D");
		if (AL.isCreated()) AL.destroy();
	}
	public static void exit() {
		Main.printInfo("Exiting ZombieApocalypse 3D");
		if (Display.isCreated()) Display.destroy();
		if (AL.isCreated()) AL.destroy();
		System.exit(0);
	}
	public static void exit(int arg0) {
		Main.printInfo("Exiting ZombieApocalypse 3D (" + arg0 + ")");
		if (Display.isCreated()) Display.destroy();
		if (AL.isCreated()) AL.destroy();
		System.exit(arg0);
	}
	
	public static void printInfo(String msg) {
		System.out.println("[INFO]    " + msg);
	}
	public static void printWarning(String msg) {
		System.out.println("[WARNING] " + msg);
	}
	public static void printError(String msg) {
		System.out.println("[ERROR]   " + msg);
	}
}
// TODO Menu texture, shading