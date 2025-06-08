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
import me.ministrie.handlers.data.SaveTaskManager;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.skins.BowSkin;
import me.ministrie.skins.WeaponSkin;

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
		}),
		
		HIDE_ARMOR_LAYER(Boolean.class, "data.hide-armor-layer", (data) -> {
			return data.getBoolean("data.hide-armor-layer", false);
		}, (config, value) -> {
			config.set("data.hide-armor-layer", value);
		}),
		
		SWORD_SKIN(WeaponSkin.class, "data.skins.sword-skin", (data) -> {
			return MamanghoSystem.getSkinManager().getSkin(data.getString("data.skins.sword-skin"));
		}, (config, value) -> {
			WeaponSkin skin = (WeaponSkin) value;
			config.set("data.skins.sword-skin", skin != null ? skin.getUID() : null);
		}),

		AXE_SKIN(WeaponSkin.class, "data.skins.axe-skin", (data) -> {
			return MamanghoSystem.getSkinManager().getSkin(data.getString("data.skins.axe-skin"));
		}, (config, value) -> {
			WeaponSkin skin = (WeaponSkin) value;
			config.set("data.skins.axe-skin", skin != null ? skin.getUID() : null);
		}),

		MACE_SKIN(WeaponSkin.class, "data.skins.mace-skin", (data) -> {
			return MamanghoSystem.getSkinManager().getSkin(data.getString("data.skins.mace-skin"));
		}, (config, value) -> {
			WeaponSkin skin = (WeaponSkin) value;
			config.set("data.skins.mace-skin", skin != null ? skin.getUID() : null);
		}),

		PICKAXE_SKIN(WeaponSkin.class, "data.skins.pickaxe-skin", (data) -> {
			return MamanghoSystem.getSkinManager().getSkin(data.getString("data.skins.pickaxe-skin"));
		}, (config, value) -> {
			WeaponSkin skin = (WeaponSkin) value;
			config.set("data.skins.pickaxe-skin", skin != null ? skin.getUID() : null);
		}),

		SHOVEL_SKIN(WeaponSkin.class, "data.skins.shovel-skin", (data) -> {
			return MamanghoSystem.getSkinManager().getSkin(data.getString("data.skins.shovel-skin"));
		}, (config, value) -> {
			WeaponSkin skin = (WeaponSkin) value;
			config.set("data.skins.shovel-skin", skin != null ? skin.getUID() : null);
		}),

		HOE_SKIN(WeaponSkin.class, "data.skins.hoe-skin", (data) -> {
			return MamanghoSystem.getSkinManager().getSkin(data.getString("data.skins.hoe-skin"));
		}, (config, value) -> {
			WeaponSkin skin = (WeaponSkin) value;
			config.set("data.skins.hoe-skin", skin != null ? skin.getUID() : null);
		}),

		FISHING_ROD_SKIN(WeaponSkin.class, "data.skins.fishing-rod-skin", (data) -> {
			return MamanghoSystem.getSkinManager().getSkin(data.getString("data.skins.fishing-rod-skin"));
		}, (config, value) -> {
			WeaponSkin skin = (WeaponSkin) value;
			config.set("data.skins.fishing-rod-skin", skin != null ? skin.getUID() : null);
		}),

		BOW_SKIN(BowSkin.class, "data.skins.bow-skin", (data) -> {
			return MamanghoSystem.getSkinManager().getSkin(data.getString("data.skins.bow-skin"));
		}, (config, value) -> {
			BowSkin skin = (BowSkin) value;
			config.set("data.skins.bow-skin", skin != null ? skin.getUID() : null);
		}),

		CROSSBOW_SKIN(BowSkin.class, "data.skins.crossbow-skin", (data) -> {
			return MamanghoSystem.getSkinManager().getSkin(data.getString("data.skins.crossbow-skin"));
		}, (config, value) -> {
			BowSkin skin = (BowSkin) value;
			config.set("data.skins.crossbow-skin", skin != null ? skin.getUID() : null);
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
			return matcher.isAssignableFrom(o.getClass());
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
	private int id;
	private SaveTaskManager saveTask;
	private SkinModule skinModule;
	
	private ConcurrentMap<DataEnum, Object> datas = new ConcurrentHashMap<>();
	
	public PlayerDataHandler(Player player){
		this.id = player.getEntityId();
		this.dataFile = new File(DATA_SECTION_PATH.formatted(player.getUniqueId().toString()));
		this.saveTask = new SaveTaskManager(this);
		this.skinModule = new SkinModule(this);
	}
	
	@Override
	public int getId(){
		return id;
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
		if(object != null){
			this.datas.put(type, object);
		}else{
			this.datas.remove(type);
		}
		this.saveTask.submit(() -> {
			try{
				this.dataSection.save(dataFile);
			}catch (IOException e){
				e.printStackTrace();
			}
		});
		return true;
	}

	@Override
	public void load(){
		this.dataSection = YamlConfiguration.loadConfiguration(dataFile);
		for(DataEnum type : DataEnum.values()){
			Object value = type.provide(dataSection);
			if(value != null) this.datas.put(type, value);
		}
	}

	@Override
	public void saveShutdown(){
		this.saveTask.saveShutdown();
	}

	@Override
	public void saveGracefully(){
		this.saveTask.saveGracefully();
	}

	@Override
	public SkinModule getSkinModule(){
		return skinModule;
	}
}
