package me.ministrie.utils.component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.main.MamanghoSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ComponentUtil{

	private static final String PATTERN_FORMAT = "\"text\":\"%s\"";
	private static final Pattern EXTRACT_PATTERN = Pattern.compile("\"text\"\\s*:\\s*\"([^\"]+)\"");
	
	public static Component translatiableFrom(String key, Component... params){
		return Component.translatable(key, params);
	}
	
	public static Component parseComponent(String text){
		if(text == null) return Component.space();
		try{
			return GsonComponentSerializer.gson().deserialize(text);
		}catch (Exception e){
			return Component.text(text).decoration(TextDecoration.ITALIC, State.FALSE);
		}
	}
	
	public static List<Component> parseComponents(List<String> texts){
		List<Component> comps = new ArrayList<>();
		if(texts == null || texts.isEmpty()) return comps;
		for(String text : texts){
			if(text == null){
				comps.add(Component.space());
				continue;
			}
			try{
				comps.add(GsonComponentSerializer.gson().deserialize(text));
			}catch (Exception e){
				comps.add(Component.text(text).decoration(TextDecoration.ITALIC, State.FALSE));
			}
		}
		return comps;
	}

	public static String getComponentPlainText(Component comp){
		return PlainTextComponentSerializer.plainText().serialize(comp);
	}
	
	public static String componentToGson(Component comp){
		return GsonComponentSerializer.gson().serialize(comp);
	}
	
	public static Component gsonToComponent(String json){
		return GsonComponentSerializer.gson().deserialize(json);
	}
	
	public static List<Component> gsonToComponents(List<String> jsons){
		List<Component> result = new ArrayList<>();
		if(jsons == null || jsons.isEmpty()) return result;
		for(String json : jsons){
			result.add(GsonComponentSerializer.gson().deserialize(json));
		}
		return result;
	}
	
	public static List<String> componentToGsons(List<Component> comps){
		List<String> result = new ArrayList<>();
		if(comps == null || comps.isEmpty()) return result;
		for(Component comp : comps){
			result.add(GsonComponentSerializer.gson().serialize(comp));
		}
		return result;
	}
	
	public static Component extractTranslatable(Component component){
		String json = componentToGson(component);
		Matcher matcher = EXTRACT_PATTERN.matcher(json);
		while(matcher.find()){
			String group = matcher.group(1);
			Player user = Bukkit.getPlayer(group);
			MamanghoPlayer player = null;
			if(user != null && (player = MamanghoSystem.getPlayerManager().getPlayer(user)) != null){
				json = json.replace(PATTERN_FORMAT.formatted(group), PATTERN_FORMAT.formatted(player.getNickname()+player.getPrefix().replace("B", "F")));
			}
		}
		return ComponentUtil.parseComponent(json);
	}
}
