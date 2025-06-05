package me.ministrie.skins;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum WeaponType{

	SWORD(new Material[]{Material.WOODEN_SWORD, Material.DIAMOND_SWORD, Material.GOLDEN_SWORD, Material.IRON_SWORD,
			Material.NETHERITE_SWORD, Material.STONE_SWORD}),
	AXE(new Material[]{Material.WOODEN_AXE, Material.DIAMOND_AXE, Material.GOLDEN_AXE, Material.IRON_AXE,
			Material.NETHERITE_AXE, Material.STONE_AXE}),
	MACE(new Material[]{Material.MACE}),

	PICKAXE(new Material[]{Material.WOODEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.GOLDEN_PICKAXE, Material.IRON_PICKAXE,
			Material.NETHERITE_PICKAXE, Material.STONE_PICKAXE}),
	
	SHOVEL(new Material[]{Material.WOODEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.GOLDEN_SHOVEL, Material.IRON_SHOVEL,
			Material.NETHERITE_SHOVEL, Material.STONE_SHOVEL}),
	
	HOE(new Material[]{Material.WOODEN_HOE, Material.DIAMOND_HOE, Material.GOLDEN_HOE, Material.IRON_HOE,
			Material.NETHERITE_HOE, Material.STONE_HOE}),
	
	FISHING_ROD(new Material[]{Material.FISHING_ROD});
	
	private Material[] compatibles;
	
	WeaponType(Material[] compatibles){
		this.compatibles = compatibles;
	}
	
	public boolean match(ItemStack item){
		for(Material c : this.compatibles){
			if(item.getType().equals(c)) return true;
		}
		return false;
	}
}
