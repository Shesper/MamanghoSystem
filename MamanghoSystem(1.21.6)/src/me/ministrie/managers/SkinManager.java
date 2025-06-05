package me.ministrie.managers;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import me.ministrie.skins.ModelSkin;
import me.ministrie.skins.WeaponSkin;
import me.ministrie.skins.BowSkin;
import me.ministrie.skins.SkinType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SkinManager{

	private static final String FILEPATH = "./plugins/MamanghoSystem/skins";
	
	private Map<String, ModelSkin> caches = Maps.newHashMap();
	private ImmutableList<WeaponSkin> melee_weapons = ImmutableList.of();
	private ImmutableList<BowSkin> bow_weapons = ImmutableList.of();
	private ImmutableList<BowSkin> crossbow_weapons = ImmutableList.of();
	
	private static List<WeaponSkin> melee = Lists.newArrayList();
	private static List<BowSkin> bow = Lists.newArrayList();
	private static List<BowSkin> crossbow = Lists.newArrayList();
	
	public SkinManager(){
		this.load();
	}
	
	public ModelSkin getSkin(String uid){
		return this.caches.get(uid);
	}
	
	public List<WeaponSkin> getMeleeWeaponSkins(){
		return this.melee_weapons;
	}
	
	public List<BowSkin> getBowSkins(){
		return this.bow_weapons;
	}
	
	public List<BowSkin> getCrossbowSkins(){
		return this.crossbow_weapons;
	}
	
	public void load(){
		this.caches.clear();
		melee.clear();
		bow.clear();
		crossbow.clear();
		this.melee_weapons = ImmutableList.of();
		this.bow_weapons = ImmutableList.of();
		this.crossbow_weapons = ImmutableList.of();
		File directory = new File(FILEPATH);
		if(directory.exists()){
			if(directory.isDirectory()){
				this.readDirectory(directory);
			}
		}
		this.melee_weapons = ImmutableList.copyOf(melee);
		this.bow_weapons = ImmutableList.copyOf(bow);
		this.crossbow_weapons = ImmutableList.copyOf(crossbow);
		Bukkit.getLogger().log(Level.INFO, "[MamanghoSystem] 총 " + this.caches.size() + "개의 스킨 정보가 로드되었습니다.");
	}
	
	protected void readDirectory(File directory){
		if(directory.isDirectory()){
			File[] listfile = directory.listFiles();
			for(File file : listfile){
				if(file.isDirectory()){
					this.readDirectory(file);
				}else{
					this.readFile(file);
				}
			}
		}
	}
	
	protected void readFile(File file){
		if(file.isFile()){
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			SkinType type = SkinType.fromFile(config);
			ModelSkin skin = type.generate(config);
			if(this.caches.containsKey(skin.getUID())){
				Bukkit.getLogger().log(Level.WARNING, "[MamanghoSystem] 중복된 스킨 UID가 존재합니다. 파일명: " + file.getName());
				return;
			}
			this.caches.put(skin.getUID(), skin);
			switch(skin.getItemTarget()){
				case BOW:
					bow.add((BowSkin) skin);
					break;
				case CROSSBOW:
					crossbow.add((BowSkin) skin);
					break;
				case MELEE_WEAPON:
					melee.add((WeaponSkin)skin);
					break;
				default:
					break;
			}
		}
	}
}
