package me.ministrie.handlers.data.player;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ministrie.api.data.player.PlayerData;
import me.ministrie.emoticon.EmoticonBookmark;

public class PlayerDataHandler implements PlayerData{

	public enum DataEnum{
		
		CUSTOM_NICKNAME(String.class, "data.nickname", (data) -> {
			return data.getString("data.nickname");
		}, (config, value) -> {
			config.set("data.nickname", value);
		}),
		
		EMOTICON_BOOKMARK(EmoticonBookmark.class, "data.emoticon-bookmark", (data) -> {
			return (EmoticonBookmark) data.get("data.emoticon-bookmark");
		}, (config, value) -> {
			EmoticonBookmark bookmark = (EmoticonBookmark) value;
			config.set("data.emoticon-bookmark", bookmark.isEmpty() ? null : bookmark);
		}),
		
		SHARE_LOCATION(Boolean.class, "data.share-location", (data) -> {
			return data.getBoolean("data.share-location", false);
		}, (config, value) -> {
			config.set("data.share-location", value);
		});
		
		private Function<YamlConfiguration, Object> dataProvide;
		private BiConsumer<YamlConfiguration, Object> save;
		private String sectionPath;
		private Class<?> matcher;
		
		DataEnum(Class<?> matcher, String sectionPath, Function<YamlConfiguration, Object> dataProvide,
				BiConsumer<YamlConfiguration, Object> save){
			this.matcher = matcher;
			this.sectionPath = sectionPath;
			this.dataProvide = dataProvide;
			this.save = save;
		}
		
		public Class<?> getMatchClass(){
			return matcher;
		}
		
		public String getSectionPath(){
			return this.sectionPath;
		}
		
		public boolean match(Object o){
			return o.getClass().isAssignableFrom(matcher);
		}
		
		public Object provide(YamlConfiguration config){
			return this.dataProvide.apply(config);
		}
		
		public void save(YamlConfiguration config, Object object){
			this.save.accept(config, object);
		}
	}
	
	private static final String DATA_SECTION_PATH = "./plugins/MamanghoSystem/userdata/%s.yml";
	
	private File dataFile;
	private YamlConfiguration dataSection;
	
	private ConcurrentMap<DataEnum, Object> datas = new ConcurrentHashMap<>();
	
	public PlayerDataHandler(Player player){
		this.dataFile = new File(DATA_SECTION_PATH.formatted(player.getUniqueId().toString()));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getData(DataEnum type){
		Object value = datas.get(type);
		return (T) value;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getData(DataEnum type, T def){
		Object value = datas.get(type);
		if(value == null) return def;
		return (T) value;
	}
	
	@Override
	public boolean setData(DataEnum type, Object object){
		if(object != null && !type.match(object)) throw new RuntimeException("DataEnum '" + type.name() + "' not match value. must like " + type.getMatchClass().getSimpleName() + ".class type value.");
		type.save(dataSection, object);
		try{
			this.dataSection.save(dataFile);
			if(object != null){
				this.datas.put(type, object);
			}else{
				this.datas.remove(type);
			}
			return true;
		}catch (IOException e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void load(){
		this.dataSection = YamlConfiguration.loadConfiguration(dataFile);
		for(DataEnum type : DataEnum.values()){
			Object value = type.provide(dataSection);
			if(value != null) this.datas.put(type, value);
		}
	}
}
