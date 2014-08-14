package org.jufi.lwjglutil;

import java.util.ArrayList;

public interface ModelCommand {
	public void execute(Model commander);
	public void execute(Model commander, ArrayList<Byte> b);
}
