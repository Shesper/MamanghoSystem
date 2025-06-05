package me.ministrie.gui.types.holders.skin;

import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.types.skin.SkinCrossbowWeaponGui;

public class SkinCrossbowWeaponGuiHolder implements ScreenHolder{

	private SkinCrossbowWeaponGui gui;
	private boolean previous = true;
	
	public SkinCrossbowWeaponGuiHolder(SkinCrossbowWeaponGui gui){
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
}
