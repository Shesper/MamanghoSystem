package me.ministrie.api.player;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.ministrie.api.data.player.PlayerData;
import me.ministrie.emoticon.Emoticon;
import me.ministrie.gui.proccess.ProcessScreen;
import me.ministrie.handlers.player.MamanghoPlayerHandler;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.managers.PlayerCooldownManager;
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.user.User;

public interface MamanghoPlayer{

	public boolean isEmpty();
	
	public Player getPlayer();
	
	public User getLuckpermsUser();
	
	public String getPrefix();
	
	public String getNickname();
	
	public String getCustomNickname();
	
	public Component getDisplaynameWithPrefix(String hexcolor);
	
	public String getPlainDisplaynameWithPrefix();
	
	public void setCustomNickname(String nickname);
	
	public void say(String messages);
	
	public void printIcon(Emoticon emoticon);
	
	public void printIcon(Emoticon emoticon, boolean big);
	
	public void printPairIcon(Emoticon first, Emoticon second);
	
	public PlayerData getData();
	
	public void updateNameVisually();
	
	public ProcessScreen getProcessScreen();
	
	public void setProcessScreen(ProcessScreen screen);
	
	public PlayerCooldownManager getCooldownManager();

	public void initializeEquipment();

	public void initializeEquipment(List<EquipmentSlot> modifiedSlots);

	public void brokenEquipment(ItemStack broken);
	
	public void onTrigger(Object value);
	
	public double getIncreaseDamage(LivingEntity victim, double damage);
	
	public double getReduceDamage(double damage);
	
	public static MamanghoPlayer getOrEmpty(Player player){
		MamanghoPlayer get = MamanghoSystem.getPlayerManager().getPlayer(player);
		if(get != null) return get;
		return getEmpty(player);
	}
	
	public static MamanghoPlayer getPlayer(Player player){
		return MamanghoSystem.getPlayerManager().getPlayer(player);
	}
	
	public static MamanghoPlayer getPlayer(UUID uuid){
		return MamanghoSystem.getPlayerManager().getPlayer(uuid);
	}
	
	public static MamanghoPlayer getPlayer(HumanEntity player){
		return MamanghoSystem.getPlayerManager().getPlayer(player.getUniqueId());
	}
	
	public static MamanghoPlayer getEmpty(Player player){
		return new MamanghoPlayerHandler(player, true);
	}
}
