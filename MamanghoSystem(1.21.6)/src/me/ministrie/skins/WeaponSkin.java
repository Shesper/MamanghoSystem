package me.ministrie.skins;

import org.bukkit.inventory.ItemStack;

public interface WeaponSkin extends ModelSkin{

	public SkinSound getHitSound();
	
	public boolean compatible(ItemStack item, WeaponType type);
}
