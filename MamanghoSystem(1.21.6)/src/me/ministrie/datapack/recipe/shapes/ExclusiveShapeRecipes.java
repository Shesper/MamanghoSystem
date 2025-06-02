package me.ministrie.datapack.recipe.shapes;

import java.util.function.Function;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public enum ExclusiveShapeRecipes{

	EXPERIMENTAL("minecraft:experimental", inventory -> true, (item) -> item);
	
	private static final Map<String, ExclusiveShapeRecipes> caches;
	
	static{
		caches = new HashMap<>();
		for(ExclusiveShapeRecipes e : ExclusiveShapeRecipes.values()){
			caches.put(e.getKey(), e);
		}
	}
	
	private String key;
	private Function<CraftingInventory, Boolean> condition;
	private Function<ItemStack, ItemStack> export;
	
	ExclusiveShapeRecipes(String key, Function<CraftingInventory, Boolean> condition, Function<ItemStack, ItemStack> export){
		this.key = key;
		this.condition = condition;
		this.export = export;
	}
	
	public String getKey(){
		return key;
	}
	
	public boolean checkCondition(CraftingInventory inventory){
		return this.condition.apply(inventory);
	}
	
	public ItemStack export(ItemStack result){
		return this.export.apply(result);
	}
	
	public static ExclusiveShapeRecipes fromExclusive(ShapedRecipe recipe){
		return caches.get(recipe.getKey().toString());
	}
}
