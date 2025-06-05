package me.ministrie.skins;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.configuration.file.YamlConfiguration;

import me.ministrie.skins.impl.BowSkinImpl;
import me.ministrie.skins.impl.WeaponSkinImpl;

public enum SkinType{

	MELEE_WEAPON(WeaponSkinImpl.class),
	BOW(BowSkinImpl.class);
	
	private Class<? extends ModelSkin> constructor;
	
	SkinType(Class<? extends ModelSkin> constructor){
		this.constructor = constructor;
	}
	
	public static SkinType fromFile(YamlConfiguration config){
		return SkinType.valueOf(config.getString("skin.type").toUpperCase());
	}
	
	public <T> ModelSkin adaptSkin(ModelSkin skin){
		return constructor.cast(skin);
	}
	
	public ModelSkin generate(YamlConfiguration config){
		try {
			return this.constructor.getConstructor(YamlConfiguration.class).newInstance(config);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e){
			e.printStackTrace();
		}
		return null;
	}
}
