package me.ministrie.gui.types.emoticon;

import me.ministrie.emoticon.Emoticon;

public abstract class EmoticonScreen{

	protected static final String COOLDOWN_FORMAT = "[{\"text\": \"%s\", \"color\": \"red\"}]";
	
	protected Emoticon hookFirst;
	
	public void hookPairFirst(Emoticon first){
		this.hookFirst = first;
	}
	
	public Emoticon getHookPairFirst(){
		return this.hookFirst;
	}
}
