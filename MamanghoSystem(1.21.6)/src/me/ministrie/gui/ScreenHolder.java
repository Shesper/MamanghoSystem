package me.ministrie.gui;

import org.bukkit.inventory.InventoryHolder;

public interface ScreenHolder extends InventoryHolder, TaskHolder{

	public void setPossiblePreviousScreen(boolean possible);

	public boolean possiblePreviousScreen();
	
	public Screen getScreen();
}
