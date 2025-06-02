package me.ministrie.gui.types.holders.image;

import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.types.image.ImageFrameCreateGui;

public class ImageFrameCreateGuiHolder implements ScreenHolder{

	private ImageFrameCreateGui gui;
	private boolean possible = true;
	
	public ImageFrameCreateGuiHolder(ImageFrameCreateGui gui){
		this.gui = gui;
	}

	@Override
	public void setPossiblePreviousScreen(boolean possible){
		this.possible = possible;
	}

	@Override
	public boolean possiblePreviousScreen(){
		return possible;
	}

	@Override
	public Screen getScreen(){
		return gui;
	}
}
