package me.ministrie.managers;

import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import me.ministrie.emoticon.EmoticonPackage;
import me.ministrie.emoticon.Emoticon;

public class EmoticonManager{

	private static final Comparator<EmoticonPackage> SORTED = (a, b) -> {
		return Integer.valueOf(a.getPriority()).compareTo(b.getPriority());
	};
	private static final String FILEPATH = "./plugins/MamanghoSystem/emoticons";
	
	private Map<String, Emoticon> caches_icons = new HashMap<>();
	private Map<Integer, EmoticonPackage> caches = new HashMap<>();
	
	public EmoticonManager(){
		this.load();
	}
	
	public List<EmoticonPackage> getEmoticons(){
		return new ArrayList<>(this.caches.entrySet().stream().map(e -> e.getValue()).sorted(SORTED).toList());
	}
	
	public Emoticon getEmoticon(String id){
		return this.caches_icons.get(id);
	}
	
	public void load(){
		this.caches_icons.clear();
		this.caches.clear();
		File directory = new File(FILEPATH);
		if(directory.exists()){
			if(directory.isDirectory()){
				this.readDirectory(directory);
			}
		}
		Bukkit.getLogger().log(Level.INFO, "[MamanghoSystem] 총 " + this.caches.size() + "개의 이모티콘 정보가 로드되었습니다.");
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
			EmoticonPackage emoticon = new EmoticonPackage(config);
			if(this.caches.containsKey(emoticon.getID())){
				Bukkit.getLogger().log(Level.WARNING, "[MamanghoSystem] 중복된 이모티콘 id가 존재합니다. 파일명: " + file.getName());
				return;
			}
			this.caches.put(emoticon.getID(), emoticon);
			emoticon.getEmojis().forEach(emoji -> caches_icons.put(emoji.getID(), emoji));
		}
	}
}
