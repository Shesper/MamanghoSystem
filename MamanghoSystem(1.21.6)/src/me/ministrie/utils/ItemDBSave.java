package me.ministrie.utils;

import java.util.UUID;
import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ItemDBSave{

	public static final String DIR_PATH = "./plugins/MamanghoSystem/itemdb";
	
	public static void saveItem(ItemStack item){
		File file = new File(DIR_PATH + "/" + UUID.randomUUID() + ".yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("item", item);
		try {
			config.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ItemStack loadItem(String name){
		File file = new File(DIR_PATH + "/" + name + ".yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		return config.getItemStack("item", null);
	}
}
