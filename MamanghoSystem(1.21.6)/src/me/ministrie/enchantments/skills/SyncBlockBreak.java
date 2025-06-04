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

public class SyncBlockBreak extends BukkitRunnable{

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
	
	public SyncBlockBreak(Player p, Location center, ItemStack item){
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
	
	public static boolean checkContain(Material m){
		if(m.equals(Material.COBBLESTONE) || m.equals(Material.COBBLED_DEEPSLATE) || m.equals(Material.STONE) || m.equals(Material.ANDESITE) || m.equals(Material.DIORITE) || m.equals(Material.GRANITE) || 
				m.equals(Material.SANDSTONE) || m.equals(Material.DEEPSLATE) || m.equals(Material.TUFF) || m.equals(Material.DRIPSTONE_BLOCK) ||
				m.equals(Material.CALCITE) || m.equals(Material.BLACKSTONE) || m.equals(Material.NETHERRACK) || m.equals(Material.BASALT) || 
				m.equals(Material.MAGMA_BLOCK) || m.equals(Material.END_STONE) || m.equals(Material.SMOOTH_BASALT)
				|| m.equals(Material.TERRACOTTA) || m.name().contains("_TERRACOTTA")){
			return true;
		}
		return false;
	}
	
	public static boolean checkValid(Player p, Block b){
		World world = p.getWorld();
		boolean allows = world.getName().equalsIgnoreCase("world") || world.getName().equalsIgnoreCase("world_nether") || world.getName().equalsIgnoreCase("world_the_end") || world.getName().equalsIgnoreCase("world_heaven_sky");
		if(allows){
			if(b.getType().equals(Material.STONE) || b.getType().equals(Material.ANDESITE) || b.getType().equals(Material.DIORITE) || b.getType().equals(Material.GRANITE) || 
					b.getType().equals(Material.SANDSTONE) || b.getType().equals(Material.DEEPSLATE) || b.getType().equals(Material.TUFF) || b.getType().equals(Material.DRIPSTONE_BLOCK) ||
					b.getType().equals(Material.CALCITE) || b.getType().equals(Material.BLACKSTONE) || b.getType().equals(Material.NETHERRACK) || b.getType().equals(Material.BASALT) || 
					b.getType().equals(Material.MAGMA_BLOCK) || b.getType().equals(Material.END_STONE) || b.getType().equals(Material.SMOOTH_BASALT)
					|| b.getType().equals(Material.TERRACOTTA) || b.getType().name().contains("_TERRACOTTA")){
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
				/* 뜨거운 걸음 관련 인챈트가 추가될 경우 여기에 추가 작업을 시행해야함 */
				Location loc = b.getLocation().add(0.5D, 0.5D, 0.5D);
				b.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, loc, 35, 0.5D/3D, 0.5D/3D, 0.5D/3D, 0.12D, b.getBlockData());
				b.getWorld().playSound(loc, b.getBlockData().getSoundGroup().getBreakSound(), 0.7f, 1.0f);
				b.breakNaturally(item);
			}
		}
	}
	
	public static SyncBlockBreak run(Player p, Location center, ItemStack hand){
		SyncBlockBreak task = new SyncBlockBreak(p, center, hand);
		task.runTask(MamanghoSystem.getInstance());
		return task;
	}
}
