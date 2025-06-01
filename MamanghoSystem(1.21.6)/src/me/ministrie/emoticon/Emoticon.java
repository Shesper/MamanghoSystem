package me.ministrie.emoticon;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import me.ministrie.configs.IconSetting;
import me.ministrie.utils.InstantIcon;
import me.ministrie.utils.string.StringUtils;
import net.md_5.bungee.api.ChatColor;

public class Emoticon{

	private static final String DEFAULT_NAME_FORMAT = "[{\"text\": \"%s\", \"color\": \"white\", \"italic\": \"false\"}]";
	
	private EmoticonPackage parent;
	private String name;
	private String key;
	private List<String> descriptions;
	private InstantIcon icon;
	private String iconBanner;
	private String font;
	private String value;
	private int gab;
	
	public Emoticon(EmoticonPackage parent, MemorySection memory, String key){
		this.parent = parent;
		this.icon = InstantIcon.of(memory.getString("icon"));
		this.iconBanner = memory.getString("banner", "");
		this.key = key;
		this.name = ChatColor.translateAlternateColorCodes('&', memory.getString("name", DEFAULT_NAME_FORMAT.formatted(key)));
		this.descriptions = StringUtils.translateColorCodes(memory.getStringList("descriptions"));
		this.font =  memory.getString("font", "default");
		this.value = memory.getString("value", "");
		this.gab = memory.getInt("gab", 6);
	}
	
	public EmoticonPackage getParent(){
		return parent;
	}
	
	public String getName(){
		return name;
	}
	
	public String getKey(){
		return key;
	}
	
	public List<String> getDescriptions(){
		return descriptions;
	}
	
	public String getFont(){
		return font;
	}
	
	public String getValue(){
		return value;
	}
	
	public InstantIcon getIcon(){
		return icon;
	}
	
	public String getBanner(){
		return iconBanner;
	}
	
	public int getGab(){
		return gab;
	}
	
	public String getID(){
		return this.parent.getID() + "-" + this.getKey();
	}
	
	public ItemStack getUIIcon(boolean bookmarked, Emoticon hooked){
		if(hooked != null){
			if(bookmarked){
				return IconSetting.EMOTICON_DETAIL_ICON_BOOKMARKED_HOOKED.getIconInfo().toOverrideIcon(icon, this.getName(), this.getBanner(), this.getDescriptions(), hooked.getBanner());
			}else{
				return IconSetting.EMOTICON_DETAIL_ICON_UNBOOKMARK_HOOKED.getIconInfo().toOverrideIcon(icon, this.getName(), this.getBanner(), this.getDescriptions(), hooked.getBanner());
			}
		}
		if(bookmarked){
			return IconSetting.EMOTICON_DETAIL_ICON_BOOKMARKED.getIconInfo().toOverrideIcon(icon, this.getName(), this.getBanner(), this.getDescriptions());
		}else{
			return IconSetting.EMOTICON_DETAIL_ICON_UNBOOKMARK.getIconInfo().toOverrideIcon(icon, this.getName(), this.getBanner(), this.getDescriptions());
		}
	}
}
