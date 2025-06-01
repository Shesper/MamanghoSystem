package me.ministrie.utils;

import org.bukkit.Material;

public class InstantIcon{

	private Material type;
	private int modelData;
	
	public InstantIcon(Material type, int modelData){
		this.type = type;
		this.modelData = modelData;
	}
	
	public Material getType(){
		return type;
	}
	
	public int getModelData(){
		return modelData;
	}
	
	public static InstantIcon of(String format){
		if(format == null || format.isEmpty()) return new InstantIcon(Material.BARRIER, -1);
		String[] split = format.split(":");
		if(split.length == 2){
			try{
				return new InstantIcon(Material.getMaterial(split[0]), Integer.parseInt(split[1]));
			}catch(Exception e){
				return new InstantIcon(Material.BARRIER, -1);
			}
		}
		return new InstantIcon(Material.BARRIER, -1);
	}
}
