package me.ministrie.gui.types.emoticon;

import me.ministrie.emoticon.Emoticon;

public abstract class EmoticonScreen{

	protected Emoticon hookFirst;
	
	public void hookPairFirst(Emoticon first){
		this.hookFirst = first;
	}
	
	public Emoticon getHookPairFirst(){
		return this.hookFirst;
	}
}
