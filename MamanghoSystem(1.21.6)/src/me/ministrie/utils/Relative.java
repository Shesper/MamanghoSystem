package me.ministrie.utils;

import org.bukkit.Location;
import org.bukkit.Material;

public class Relative{
	
	private double x;
	private double y;
	private double z;
	private Material match;
	
	public Relative(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Relative(double x, double y, double z, Material mat){
		this.x = x;
		this.y = y;
		this.z = z;
		this.match = mat;
	}
	
	public double getRelativeX(){
		return x;
	}
	
	public double getRelativeY(){
		return y;
	}
	
	public double getRelativeZ(){
		return z;
	}
	
	public Material getMatchType(){
		return match;
	}
	
	public Location getRelativeLocation(Location loc){
		return new Location(loc.getWorld(), loc.getX()+x, loc.getY()+y, loc.getZ()+z);
	}
	
	public static Relative of(double x, double y, double z){
		return new Relative(x, y, z);
	}
	
	public static Relative of(double x, double y, double z, Material match){
		return new Relative(x, y, z, match);
	}
	
	@Override
	public String toString(){
		return "Relative{" + x + ", " + y + ", " + z + "}";
	}
}
