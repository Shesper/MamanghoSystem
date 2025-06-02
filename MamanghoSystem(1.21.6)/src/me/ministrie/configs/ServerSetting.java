package me.ministrie.configs;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

public enum ServerSetting{
	
	CHAT_MESSAGE_MAX_LENGTH("config.chat.message-max-length", 64),
	NICKNAME_CHANGE_MAX_LENGTH("config.chat.nickname-max-length", 15),
	
	BIG_EMOTICON_COOLDOWN("config.player.cooldowns.big-emoticon-cooldown", 10000),
	
	RESOURCEPACK_URL("config.system.resourcepack-url", ""),
	RESOURCEPACK_UUID("config.system.resourcepack-uuid", UUID.randomUUID().toString()),
	RESOURCEPACK_SHA1_HASH("config.system.resourcepack-sha1-hash", "");

	public static final String DIR = "./plugins/MamanghoSystem/config.yml";
	public static YamlConfiguration config = null;
	public static Map<ServerSetting, Object> cache_values = new HashMap<>();
	
	private String path;
	private Object def_value;
	
	ServerSetting(String path, Object def){
		this.path = path;
		this.def_value = def;
	}
	
	public String getPath(){
		return path;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getDefaultValue(){
		return (T) def_value;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(){
		return (T) cache_values.get(this);
	}
	
	public UUID fromUUID(){
		return UUID.fromString(this.getValue());
	}
	
	public static void load(){
		File file = new File(DIR);
		boolean save = false;
		config = YamlConfiguration.loadConfiguration(file);
		for(ServerSetting setting : ServerSetting.values()){
			if(!config.isSet(setting.getPath())){
				config.set(setting.getPath(), setting.getDefaultValue());
				cache_values.put(setting, setting.getDefaultValue());
				save = true;
			}else{
				cache_values.put(setting, config.get(setting.getPath()));
			}
		}
		if(save){
			try{
				config.save(file);
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}