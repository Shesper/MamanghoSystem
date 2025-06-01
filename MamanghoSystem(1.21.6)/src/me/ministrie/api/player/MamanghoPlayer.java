package me.ministrie.api.player;

import org.bukkit.entity.Player;

import me.ministrie.api.data.player.PlayerData;
import me.ministrie.emoticon.Emoticon;
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.user.User;

public interface MamanghoPlayer{

	public Player getPlayer();
	
	public User getLuckpermsUser();
	
	public String getPrefix();
	
	public String getNickname();
	
	public String getCustomNickname();
	
	public Component getDisplaynameWithPrefix();
	
	public String getPlainDisplaynameWithPrefix();
	
	public void setCustomNickname(String nickname);
	
	public void say(String messages);
	
	public void printIcon(Emoticon emoticon);
	
	public void printPairIcon(Emoticon first, Emoticon second);
	
	public PlayerData getData();
	
	public void updateNameVisually();
}
