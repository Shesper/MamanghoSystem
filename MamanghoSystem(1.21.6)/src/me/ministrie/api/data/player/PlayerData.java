package me.ministrie.api.data.player;

import me.ministrie.handlers.data.player.PlayerDataHandler.DataEnum;

public interface PlayerData{
	
	public int getId();
	
	public void load();
	
	public <T> T getData(DataEnum type);

	public <T> T getData(DataEnum type, T def);
	
	public boolean setData(DataEnum type, Object value);
	
	public void saveShutdown();
	
	public void saveGracefully();
}
