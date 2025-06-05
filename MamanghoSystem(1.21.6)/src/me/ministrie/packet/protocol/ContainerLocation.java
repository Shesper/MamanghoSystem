package me.ministrie.packet.protocol;

public enum ContainerLocation {

	TOP, BOTTOM;
	
	public static ContainerLocation fromSlot(int topSize, int rawSlot){
		if(rawSlot <= topSize-1) return TOP;
		return BOTTOM;
	}
}
