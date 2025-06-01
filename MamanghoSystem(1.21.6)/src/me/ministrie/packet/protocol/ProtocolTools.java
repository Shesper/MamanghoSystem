package me.ministrie.packet.protocol;

import java.util.Collections;
import java.util.UUID;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.main.MamanghoSystem;

public class ProtocolTools{

	public static void setFakeAboveName(MamanghoPlayer user, String fakename){
		Player player = user.getPlayer();
		UUID uuid = player.getUniqueId();
		WrappedGameProfile fakeProfile = new WrappedGameProfile(uuid, fakename);
		GameProfile nmsProfile = ((CraftPlayer) player).getProfile();
		Collection<Property> textures = nmsProfile.getProperties().get("textures");
	    textures.forEach(property -> {
	    	fakeProfile.getProperties().put("textures", WrappedSignedProperty.fromHandle(property));
	    });
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketContainer removePacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
		PlayerInfoData infoData = new PlayerInfoData(fakeProfile, 0, NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromText(fakename));
		removePacket.getUUIDLists().write(0, Lists.newArrayList(uuid));
		
		PacketContainer addPacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
		addPacket.getPlayerInfoActions().write(0, Sets.newHashSet(PlayerInfoAction.ADD_PLAYER));
		addPacket.getPlayerInfoDataLists().write(1, Collections.singletonList(infoData));
		
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
			GameProfile nmsProfile = ((CraftPlayer) other).getProfile();
			WrappedGameProfile fakeProfile = new WrappedGameProfile(other.getUniqueId(), displayname);
			Collection<Property> textures = nmsProfile.getProperties().get("textures");
		    textures.forEach(property -> {
		    	fakeProfile.getProperties().put("textures", WrappedSignedProperty.fromHandle(property));
		    });
			PacketContainer removePacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
			PlayerInfoData infoData = new PlayerInfoData(fakeProfile, 0, NativeGameMode.fromBukkit(other.getGameMode()), WrappedChatComponent.fromText(displayname));
			removePacket.getUUIDLists().write(0, Lists.newArrayList(other.getUniqueId()));
			
			PacketContainer addPacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
			addPacket.getPlayerInfoActions().write(0, Sets.newHashSet(PlayerInfoAction.ADD_PLAYER));
			addPacket.getPlayerInfoDataLists().write(1, Collections.singletonList(infoData));
			
			protocolManager.sendServerPacket(listener, removePacket);
			listener.hidePlayer(MamanghoSystem.getInstance(), other);
			protocolManager.sendServerPacket(listener, addPacket);
			listener.showPlayer(MamanghoSystem.getInstance(), other);
		}
	}
}
