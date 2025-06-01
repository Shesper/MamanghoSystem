package me.ministrie.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

import me.ministrie.api.player.MamanghoPlayer;

public interface Screen{

	public MamanghoPlayer getViewer();
	
	public void open();
	
	public void open(Screen previous);
	
	public void close();
	
	public void refresh();
	
	public default void closeAbsolutly(){
		this.getHolder().setPossiblePreviousScreen(false);
		this.close();
	}
	
	public Screen getPreviousScreen();
	
	public ScreenHolder getHolder();
	
	public void click(InventoryClickEvent event);
	
	public default boolean isCancelledDefault(){
		return true;
	}
}
