package me.ministrie.gui.types.holders.emoticon;

import org.bukkit.inventory.Inventory;

import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.types.emoticon.EmoticonGui;

public class EmoticonGuiHolder implements ScreenHolder{

	private EmoticonGui gui;
	private boolean previous = true;
	
	public EmoticonGuiHolder(EmoticonGui gui){
		this.gui = gui;
	}

	@Override
	public void setPossiblePreviousScreen(boolean possible){
		this.previous = possible;
	}

	@Override
	public boolean possiblePreviousScreen(){
		return previous;
	}

	@Override
	public Screen getScreen(){
		return gui;
	}

	@Override
	public Inventory getInventory(){
		return null;
	}
}
