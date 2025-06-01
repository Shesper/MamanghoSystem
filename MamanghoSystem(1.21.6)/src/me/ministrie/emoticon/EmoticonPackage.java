package me.ministrie.emoticon;

import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import me.ministrie.configs.IconSetting;
import me.ministrie.utils.InstantIcon;
import me.ministrie.utils.string.StringUtils;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.configuration.MemorySection;

public class EmoticonPackage{
	
	private int priority;
	private int id;
	private String name;
	private List<String> descriptions;
	private InstantIcon icon;
	private String iconBanner;
	private List<Emoticon> emoticons;
	
	public EmoticonPackage(YamlConfiguration config){
		this.priority = config.getInt("emoticon.priority", 0);
		this.id = config.getInt("emoticon.id", 0);
		this.name = ChatColor.translateAlternateColorCodes('&', config.getString("emoticon.name", ""));
		this.descriptions = StringUtils.translateColorCodes(config.getStringList("emoticon.descriptions"));
		this.icon = InstantIcon.of(config.getString("emoticon.icon"));
		this.iconBanner = config.getString("emoticon.banner", "");
		this.emoticons = new ArrayList<>();
		ConfigurationSection section = config.getConfigurationSection("emoticon.emoji");
		if(section != null){
			for(Entry<String, Object> e : section.getValues(false).entrySet()){
				MemorySection memory = (MemorySection) e.getValue();
				Emoticon emoji = new Emoticon(this, memory, e.getKey());
				this.emoticons.add(emoji);
			}
			Bukkit.getLogger().log(Level.INFO, "[Emoticon] ID: " + this.getID() + ", Emoji Size: " + this.emoticons.size() + " has been loaded.");
		}
	}
	
	public int getID(){
		return id;
	}
	
	public int getPriority(){
		return priority;
	}
	
	public String getName(){
		return name;
	}
	
	public List<String> getDescriptions(){
		return descriptions;
	}
	
	public InstantIcon getIcon(){
		return icon;
	}
	
	public String getBanner(){
		return iconBanner;
	}
	
	public List<Emoticon> getEmojis(){
		return emoticons;
	}
	
	public ItemStack getUIIcon(){
		return IconSetting.EMOTICON_ICON.getIconInfo().toOverrideIcon(icon, this.getName(), this.getBanner(), this.getDescriptions());
	}
}
