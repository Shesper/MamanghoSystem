package me.ministrie.gui.proccess;

import me.ministrie.gui.Screen;

public interface ProcessScreen extends Screen{

	public int getProcess();
	
	public boolean writeValue(int process, Object value);
	
	public <T> T getValue(int process);
	
	public boolean hasValue(int process);
	
	public void cancelProcess();
}
