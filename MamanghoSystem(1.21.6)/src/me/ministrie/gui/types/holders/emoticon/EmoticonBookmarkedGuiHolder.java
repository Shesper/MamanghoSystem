package me.ministrie.gui.types.holders.emoticon;

import org.bukkit.inventory.Inventory;

import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.types.emoticon.EmoticonBookmarkedGui;
import me.ministrie.gui.types.emoticon.EmoticonScreen;
import me.ministrie.thread.ThreadUtils;

public class EmoticonBookmarkedGuiHolder implements ScreenHolder{

	private EmoticonBookmarkedGui gui;
	private boolean previous = true;
	
	public EmoticonBookmarkedGuiHolder(EmoticonBookmarkedGui gui){
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
	
	@Override
	public void openTask(){
		if(this.gui.getPreviousScreen() != null){
			EmoticonScreen screen = (EmoticonScreen) this.gui.getPreviousScreen();
			gui.hookPairFirst(screen.getHookPairFirst());
			ThreadUtils.runThreadSafe((v) -> this.gui.refresh(), 1);
		}
	}
	
	@Override
	public void closeTask(){
		if(this.gui.getPreviousScreen() != null){
			EmoticonScreen screen = (EmoticonScreen) this.gui.getPreviousScreen();
			screen.hookPairFirst(this.gui.getHookPairFirst());
		}
	}
}
