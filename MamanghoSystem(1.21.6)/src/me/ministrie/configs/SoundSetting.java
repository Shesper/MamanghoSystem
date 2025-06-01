package me.ministrie.configs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ministrie.api.player.MamanghoPlayer;

public enum SoundSetting{

	DEFAULT_GUI_PAGE_CLICK("sounds.default.gui.page-click", "ui.button.click"),
	DEFAULT_GUI_EMOTICON_BOOKMARK("sounds.default.gui.emoticon-bookmark", "ui.button.click"),
	DEFAULT_GUI_HOOK_EMOTICON_PAIR_FIRST("sounds.default.gui.emoticon-hook-pair-first", "entity.chicken.egg");

	private String path;
	private String def;
	private SoundCategory category = SoundCategory.MASTER;
	private float volume = 1.0f;
	private float pitch = 1.0f;
	
	SoundSetting(String path, String def){
		this.path = path;
		this.def = def;
	}
	
	public String getPath(){
		return path;
	}
	
	public String getDefaultValue(){
		return def;
	}

	public void playSound(MamanghoPlayer player){
		if(player == null) return;
		Player online = player.getPlayer();
		try{
			@SuppressWarnings("removal")
			Sound sound = Sound.valueOf(this.getValue());
			online.playSound(online.getLocation(), sound, this.getSoundCategory(), this.getVolume(), this.getPitch());
		}catch(Exception e){
			String sound = this.getValue();
			if(sound == null || sound.isEmpty()) return;
			online.playSound(online.getLocation(), sound, this.getSoundCategory(), this.getVolume(), this.getPitch());
		}
	}
	
	public void playSound(Player online){
		if(online == null) return;
		try{
			@SuppressWarnings("removal")
			Sound sound = Sound.valueOf(this.getValue());
			online.playSound(online.getLocation(), sound, this.getSoundCategory(), this.getVolume(), this.getPitch());
		}catch(Exception e){
			String sound = this.getValue();
			if(sound == null || sound.isEmpty()) return;
			online.playSound(online.getLocation(), sound, this.getSoundCategory(), this.getVolume(), this.getPitch());
		}
	}
	
	public static final String FILENAME = "./plugins/MamanghoSystem/sounds.yml";
	public static YamlConfiguration config = null;
	public static Map<SoundSetting, String> cache_values = new HashMap<>();
	public static Map<SoundSetting, SoundCategory> category_caches = new HashMap<>();
	public static Map<SoundSetting, Float> volume_caches = new HashMap<>();
	public static Map<SoundSetting, Float> pitch_caches = new HashMap<>();
	
	public String getValue(){
		return cache_values.getOrDefault(this, this.getDefaultValue());
	}
	
	public SoundCategory getSoundCategory(){
		return category_caches.getOrDefault(this, this.category);
	}
	
	public float getVolume(){
		return volume_caches.getOrDefault(this, this.volume);
	}
	
	public float getPitch(){
		return pitch_caches.getOrDefault(this, this.pitch);
	}
	
	public static void load(){
		cache_values.clear();
		category_caches.clear();
		volume_caches.clear();
		pitch_caches.clear();
		File file = new File(FILENAME);
		boolean save = false;
		config = YamlConfiguration.loadConfiguration(file);
		for(SoundSetting setting : SoundSetting.values()){
			String soundPath = setting.getPath() + ".sound";
			String categoryPath = setting.getPath() + ".channel";
			String volumePath = setting.getPath() + ".volume";
			String pitchPath = setting.getPath() + ".pitch";
			
			if(!config.isSet(soundPath)){
				config.set(soundPath, setting.getDefaultValue());
				cache_values.put(setting, setting.getDefaultValue());
				save = true;
			}else{
				cache_values.put(setting, config.getString(soundPath));
			}
			
			if(!config.isSet(categoryPath)){
				config.set(categoryPath, setting.category.name());
				category_caches.put(setting, setting.category);
				save = true;
			}else{
				category_caches.put(setting, fromString(config.getString(categoryPath)));
			}
			
			if(!config.isSet(volumePath)){
				config.set(volumePath, setting.volume);
				volume_caches.put(setting, setting.volume);
				save = true;
			}else{
				volume_caches.put(setting, (float)config.getDouble(volumePath));
			}
			
			if(!config.isSet(pitchPath)){
				config.set(pitchPath, setting.pitch);
				pitch_caches.put(setting, setting.pitch);
				save = true;
			}else{
				pitch_caches.put(setting, (float)config.getDouble(pitchPath));
			}
		}
		if(save){
			try{
				config.save(file);
			}catch (IOException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static SoundCategory fromString(String format){
		if(format == null || format.isEmpty()) return SoundCategory.MASTER;
		try{
			return SoundCategory.valueOf(format.toUpperCase());
		}catch(Exception e){
			return SoundCategory.MASTER;
		}
	}
}
