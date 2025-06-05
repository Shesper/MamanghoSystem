package me.ministrie.skins;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SkinSound{

	private String sound;
	private SoundCategory category;
	private float volume;
	private float pitch;
	
	public SkinSound(String sound, SoundCategory category, float volume, float pitch){
		this.sound = sound;
		this.category = category;
		this.volume = volume;
		this.pitch = pitch;
	}
	
	public SkinSound(YamlConfiguration config, String key){
		this.sound = config.getString(key + ".sound");
		this.category = SoundCategory.valueOf(config.getString(key + ".category", "MASTER").toUpperCase());
		this.volume = Double.valueOf(config.getDouble(key + ".volume", 1.0)).floatValue();
		this.pitch = Double.valueOf(config.getDouble(key + ".pitch", 1.0)).floatValue();
	}
	
	public String getSound(){
		return sound;
	}
	
	public SoundCategory getCategory(){
		return category;
	}
	
	public float getVolume(){
		return volume;
	}
	
	public float getPitch(){
		return pitch;
	}
	
	public void playSound(Player target){
		if(sound == null || sound.isEmpty()) return;
		target.playSound(target, sound, category, volume, pitch);
	}
	
	public void playSound(Location location){
		if(sound == null || sound.isEmpty()) return;
		location.getWorld().playSound(location, sound, category, volume, pitch);
	}
}
