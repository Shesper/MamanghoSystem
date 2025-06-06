package me.ministrie.datapack.recipe.smithings;

import java.util.function.Function;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

public enum ExclusiveSmithingRecipes{

	NETHERITE_ELYTRA_CHESTPLATE("elytrachestplate:netherite", (inventory) -> {
		ItemStack template = inventory.getInputTemplate();
		ItemMeta meta = template.getItemMeta();
		CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
		return cmd.getFloats().contains(1.0f);
	}, (item) -> item);
	
	private static final Map<String, ExclusiveSmithingRecipes> caches;
	
	static{
		caches = new HashMap<>();
		for(ExclusiveSmithingRecipes e : ExclusiveSmithingRecipes.values()){
			caches.put(e.getKey(), e);
		}
	}
	
	private String key;
	private Function<SmithingInventory, Boolean> condition;
	private Function<ItemStack, ItemStack> export;
	
	ExclusiveSmithingRecipes(String key, Function<SmithingInventory, Boolean> condition, Function<ItemStack, ItemStack> export){
		this.key = key;
		this.condition = condition;
		this.export = export;
	}
	
	public String getKey(){
		return key;
	}
	
	public boolean checkCondition(SmithingInventory inventory){
		return this.condition.apply(inventory);
	}
	
	public ItemStack export(ItemStack result){
		return this.export.apply(result);
	}
	
	public static ExclusiveSmithingRecipes fromExclusive(SmithingRecipe recipe){
		return caches.get(recipe.getKey().toString());
	}
}
