package me.ministrie.packet.protocol;

import java.util.Collections;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.view.AnvilView;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.authlib.properties.Property;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.main.MamanghoSystem;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;

public class ProtocolTools{

	public static void updateAnvilInventory(PrepareAnvilEvent event){
		if(event.getView().getPlayer() instanceof Player viewer){
			Bukkit.getScheduler().scheduleSyncDelayedTask(MamanghoSystem.getInstance(), () -> {
				if(viewer.isOnline()){
					AnvilView view = event.getView();
					int cost = view.getRepairCost();
					if(cost >= 40) cost = 39;
					ServerPlayer sp = ((CraftPlayer) viewer).getHandle();
					if(sp.containerMenu instanceof AnvilMenu anvilMenu){
						ClientboundContainerSetDataPacket packet = new ClientboundContainerSetDataPacket(anvilMenu.containerId, 0, cost);
						sendPacket(viewer, packet);
					}
				}
			}, 1);
		}
	}
	
	public static void setFakeAboveName(MamanghoPlayer user, String fakename){
		Player player = user.getPlayer();
		UUID uuid = player.getUniqueId();
		WrappedGameProfile fakeProfile = new WrappedGameProfile(uuid, fakename);
		Multimap<String, WrappedSignedProperty> properties = HashMultimap.create();
		player.getPlayerProfile().getProperties().forEach(prop -> {
			properties.put(prop.getName(), WrappedSignedProperty.fromHandle(new Property(prop.getName(), prop.getValue(), prop.getSignature())));
		});
		fakeProfile.getProperties().putAll(properties);
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketContainer removePacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
		PlayerInfoData infoData = new PlayerInfoData(fakeProfile, 0, NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromText(fakename));
		removePacket.getUUIDLists().write(0, Lists.newArrayList(uuid));
		
		PacketContainer addPacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
		addPacket.getPlayerInfoActions().write(0, Sets.newHashSet(PlayerInfoAction.ADD_PLAYER));
		addPacket.getPlayerInfoDataLists().write(0, Collections.singletonList(infoData));
		
		for(Player other : Bukkit.getOnlinePlayers()){
			if(user.getPlayer().getUniqueId().equals(other.getUniqueId())) continue;
			protocolManager.sendServerPacket(other, removePacket);
			other.hidePlayer(MamanghoSystem.getInstance(), player);
			protocolManager.sendServerPacket(other, addPacket);
			other.showPlayer(MamanghoSystem.getInstance(), player);
		}
	}
	
	public static void updateOtherFakeAboveName(MamanghoPlayer user){
		Player listener = user.getPlayer();
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		for(Player other : Bukkit.getOnlinePlayers()){
			if(user.getPlayer().getUniqueId().equals(other.getUniqueId())) continue;
			MamanghoPlayer otherPlayer = MamanghoSystem.getPlayerManager().getPlayer(other);
			if(otherPlayer == null) continue;
			String displayname = otherPlayer.getPlainDisplaynameWithPrefix();
			WrappedGameProfile fakeProfile = new WrappedGameProfile(other.getUniqueId(), displayname);
			Multimap<String, WrappedSignedProperty> properties = HashMultimap.create();
			other.getPlayerProfile().getProperties().forEach(prop -> {
				properties.put(prop.getName(), WrappedSignedProperty.fromHandle(new Property(prop.getName(), prop.getValue(), prop.getSignature())));
			});
			fakeProfile.getProperties().putAll(properties);
			PacketContainer removePacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
			PlayerInfoData infoData = new PlayerInfoData(fakeProfile, 0, NativeGameMode.fromBukkit(other.getGameMode()), WrappedChatComponent.fromText(displayname));
			removePacket.getUUIDLists().write(0, Lists.newArrayList(other.getUniqueId()));
			
			PacketContainer addPacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
			addPacket.getPlayerInfoActions().write(0, Sets.newHashSet(PlayerInfoAction.ADD_PLAYER));
			addPacket.getPlayerInfoDataLists().write(0, Collections.singletonList(infoData));
			
			protocolManager.sendServerPacket(listener, removePacket);
			listener.hidePlayer(MamanghoSystem.getInstance(), other);
			protocolManager.sendServerPacket(listener, addPacket);
			listener.showPlayer(MamanghoSystem.getInstance(), other);
		}
	}
	
	public static void sendPacket(Player player, Packet<?> packet){
		CraftPlayer p = (CraftPlayer) player;
		p.getHandle().connection.send(packet);
	}
}
