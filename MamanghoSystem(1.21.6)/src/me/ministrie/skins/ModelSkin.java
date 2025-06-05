package me.ministrie.skins;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import me.ministrie.api.player.MamanghoPlayer;

public interface ModelSkin{

	public int getPriority();
	
	public String getUID();
	
	public NamespacedKey getModelKey();
	
	public SkinType getSkinType();
	
	public ItemTarget getItemTarget();
	
	public String getName();
	
	public List<String> getDescriptions();
	
	public ItemStack getIcon(MamanghoPlayer viewer);
	
	public boolean compatible(ItemStack item);
}
