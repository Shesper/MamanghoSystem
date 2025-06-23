package me.ministrie.schedulers;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.ministrie.configs.ServerSetting;
import me.ministrie.main.MamanghoSystem;

public class EntityAttributeModifier{

	private NamespacedKey HAPPY_GHAST_ATTRIBUTE_KEY = NamespacedKey.minecraft("happy_ghast_speed");
	private double speed;
	private BukkitTask task;
	
	public EntityAttributeModifier(){
		this.update();
	}
	
	public void update(){
		Bukkit.getServer().getWorlds().forEach(world -> {
			world.getLivingEntities().forEach(living -> {
				if(living.getType().equals(EntityType.HAPPY_GHAST)){
					AttributeInstance att = living.getAttribute(Attribute.FLYING_SPEED);
					att.removeModifier(HAPPY_GHAST_ATTRIBUTE_KEY);
				}
			});
		});
		this.speed = ServerSetting.HAPPY_GHAST_FLY_SPEED.getValue();
		this.start();
	}
	
	public void start(){
		this.stop();
		task = new BukkitRunnable(){
			@Override
			public void run(){
				Bukkit.getServer().getWorlds().forEach(world -> {
					world.getLivingEntities().forEach(living -> {
						if(living.getType().equals(EntityType.HAPPY_GHAST)){
							AttributeInstance att = living.getAttribute(Attribute.FLYING_SPEED);
							if(att.getModifier(HAPPY_GHAST_ATTRIBUTE_KEY) != null) return;
							att.addModifier(new AttributeModifier(HAPPY_GHAST_ATTRIBUTE_KEY, speed, Operation.ADD_NUMBER));
						}
					});
				});
			}
		}.runTaskTimer(MamanghoSystem.getInstance(), 1, 1);
	}
	
	public void stop(){
		if(task != null) task.cancel();
		task = null;
	}
}
