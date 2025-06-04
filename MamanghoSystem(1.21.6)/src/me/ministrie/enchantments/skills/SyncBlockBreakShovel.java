package me.ministrie.enchantments.skills;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import me.ministrie.main.MamanghoSystem;
import me.ministrie.utils.Relative;

public class SyncBlockBreakShovel extends BukkitRunnable{

	public static final ImmutableList<Relative> offsets;
	
	static{
		List<Relative> r = Lists.newArrayList();
		for(int x = -1; x <= 1; x++){
			for(int y = -1; y <= 1; y++){
				for(int z = -1; z <= 1; z++){
					if(x == 0 && y == 0 && z == 0) continue;
					r.add(new Relative(x,y,z));
				}
			}
		}
		offsets = ImmutableList.copyOf(r);
	}
	
	private Player p;
	private Location center;
	private ItemStack item;
	
	public SyncBlockBreakShovel(Player p, Location center, ItemStack item){
		this.p = p;
		this.center = center;
		this.item = item;
	}
	
	public Player getPlayer(){
		return p;
	}
	
	public Location getCenter(){
		return center;
	}
	
	public ItemStack getItem(){
		return item;
	}
	
	public static boolean checkValid(Player p, Block b){
		World world = p.getWorld();
		boolean allows = world.getName().equalsIgnoreCase("world") || world.getName().equalsIgnoreCase("world_nether") || world.getName().equalsIgnoreCase("world_the_end") || world.getName().equalsIgnoreCase("world_heaven_sky");
		if(allows){
			if(b.getType().equals(Material.DIRT) || b.getType().equals(Material.GRASS_BLOCK) || b.getType().equals(Material.SAND) || b.getType().equals(Material.RED_SAND) || b.getType().equals(Material.GRAVEL) || b.getType().equals(Material.SOUL_SAND) ||
					b.getType().equals(Material.DIRT_PATH) || b.getType().equals(Material.COARSE_DIRT) || b.getType().equals(Material.SOUL_SOIL)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void run() {
		for(Relative rel : offsets){
			Location offset_loc = rel.getRelativeLocation(center);
			Block b = offset_loc.getBlock();
			if(b.getType().equals(Material.AIR)) continue;
			if(checkValid(p, b)){
				Location loc = b.getLocation().add(0.5D, 0.5D, 0.5D);
				b.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, loc, 35, 0.5D/3D, 0.5D/3D, 0.5D/3D, 0.12D, b.getBlockData());
				b.getWorld().playSound(loc, b.getBlockData().getSoundGroup().getBreakSound(), 0.7f, 1.0f);
				b.breakNaturally(item);
			}
		}
	}
	
	public static SyncBlockBreakShovel run(Player p, Location center, ItemStack hand){
		SyncBlockBreakShovel task = new SyncBlockBreakShovel(p, center, hand);
		task.runTask(MamanghoSystem.getInstance());
		return task;
	}
}
