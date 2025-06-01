package me.ministrie.emoticon;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import me.ministrie.api.data.player.PlayerData;
import me.ministrie.handlers.data.player.PlayerDataHandler.DataEnum;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.managers.EmoticonManager;
import me.ministrie.utils.MathUtil;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

@SerializableAs("EmoticonBookmark")
public class EmoticonBookmark implements ConfigurationSerializable{

	private static final Comparator<Entry<String, Long>> SORTED = (a, b) -> {
		return a.getValue().compareTo(b.getValue());
	};
	
	private Map<String, Long> bookmarks = new HashMap<>();
	
	public EmoticonBookmark(){}
	
	public EmoticonBookmark(Map<String, Long> bookmarks){
		this.bookmarks = bookmarks;
	}
	
	public List<Emoticon> getBookmarkedList(){
		List<Emoticon> list = new ArrayList<>();
		EmoticonManager mgr = MamanghoSystem.getEmoticonManager();
		this.bookmarks.entrySet().stream().sorted(SORTED).forEach(e -> {
			Emoticon emoji = mgr.getEmoticon(e.getKey());
			if(emoji != null) list.add(emoji);
		});
		return list;
	}
	
	public boolean isEmpty(){
		return this.bookmarks.isEmpty();
	}
	
	public boolean isBookmarked(Emoticon emoticon){
		return this.bookmarks.containsKey(emoticon.getID());
	}
	
	public void bookmark(Emoticon emoticon, boolean mark, PlayerData data){
		if(!mark){
			this.bookmarks.remove(emoticon.getID());
		}else{
			this.bookmarks.put(emoticon.getID(), System.currentTimeMillis());
		}
		this.save(data);
	}
	
	public void save(PlayerData data){
		data.setData(DataEnum.EMOTICON_BOOKMARK, this);
	}

	@Override
	public Map<String, Object> serialize(){
		Map<String, Object> map = new HashMap<>();
		this.bookmarks.forEach((k, v) -> map.put(k, v));
		return map;
	}
	
	public static EmoticonBookmark deserialize(Map<String, Object> map){
		Map<String, Long> m = new HashMap<>();
		map.forEach((k, v) -> {
			Long value = MathUtil.adaptLong(v);
			if(value != null) m.put(k, value);
		});
		return new EmoticonBookmark(m);
	}
	
	public static EmoticonBookmark emptyBookmark(){
		return new EmoticonBookmark();
	}
}
