package me.ministrie.main;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChainFactory;
import me.ministrie.commands.AdminCommand;
import me.ministrie.commands.EmoticonCommand;
import me.ministrie.commands.HelpCommand;
import me.ministrie.commands.ImageMapCommand;
import me.ministrie.commands.NicknameChangeCommand;
import me.ministrie.commands.ShareLocationCommand;
import me.ministrie.commands.WhisperCommand;
import me.ministrie.configs.BiomeInformation;
import me.ministrie.configs.IconSetting;
import me.ministrie.configs.MessageSetting;
import me.ministrie.configs.ServerSetting;
import me.ministrie.configs.SoundSetting;
import me.ministrie.emoticon.EmoticonBookmark;
import me.ministrie.handlers.LuckpermsListenerHandler;
import me.ministrie.handlers.PlayerListenerHandler;
import me.ministrie.managers.DamageTickController;
import me.ministrie.managers.EmoticonManager;
import me.ministrie.managers.PlayerManager;
import me.ministrie.packet.protocol.listener.ServerLogin;
import me.ministrie.packet.protocol.listener.ServerRespawn;
import me.ministrie.schedulers.UserTabListUpdater;

public class MamanghoSystem extends JavaPlugin{

	private static MamanghoSystem instance;
	
	private static PlayerManager playermanager;
	private static EmoticonManager emojiManager;
	private static BiomeInformation biomeSummary;
	private static TaskChainFactory TASK_CHAIN;
	private static DamageTickController tickController;
	private static UserTabListUpdater tabUpdater;
	
	@Override
	public void onEnable(){
		instance = this;
		this.bukkitCommandLoad();
		TASK_CHAIN = BukkitTaskChainFactory.create(this);
		ConfigurationSerialization.registerClass(EmoticonBookmark.class);
		Bukkit.getPluginManager().registerEvents(new PlayerListenerHandler(), this);
		ServerSetting.load();
		MessageSetting.load();
		IconSetting.load();
		SoundSetting.load();
		playermanager = new PlayerManager(this);
		tabUpdater = new UserTabListUpdater();
		emojiManager = new EmoticonManager();
		biomeSummary = new BiomeInformation();
		tickController = new DamageTickController();
		LuckpermsListenerHandler.start();
		ProtocolLibrary.getProtocolManager().addPacketListener(new ServerLogin(this));
		ProtocolLibrary.getProtocolManager().addPacketListener(new ServerRespawn(this));
	}
	
	@Override
	public void onDisable(){
		tabUpdater.stop();
		for(Player online : Bukkit.getOnlinePlayers()){
			playermanager.removePlayerShutdown(online);
		}
	}
	
	public static MamanghoSystem getInstance(){
		return instance;
	}
	
	public static PlayerManager getPlayerManager(){
		return playermanager;
	}
	
	public static UserTabListUpdater getTabListUpdater(){
		return tabUpdater;
	}
	
	public static EmoticonManager getEmoticonManager(){
		return emojiManager;
	}
	
	public static BiomeInformation getBiomeInformation(){
		return biomeSummary;
	}
	
	public static DamageTickController getDamageTickController(){
		return tickController;
	}
	
	public static TaskChainFactory getTaskChainFactory(){
		return TASK_CHAIN;
	}
	
	private void bukkitCommandLoad(){
		this.getCommand("system").setExecutor(new AdminCommand());
		this.getCommand("con").setExecutor(new EmoticonCommand());
		this.getCommand("namechange").setExecutor(new NicknameChangeCommand());
		this.getCommand("sharelocation").setExecutor(new ShareLocationCommand());
		this.getCommand("help").setExecutor(new HelpCommand());
		this.getCommand("image").setExecutor(new ImageMapCommand());
		WhisperCommand whipsercmd = new WhisperCommand();
		this.getCommand("tell").setExecutor(whipsercmd);
		this.getCommand("tell").setTabCompleter(whipsercmd);
	}
}
