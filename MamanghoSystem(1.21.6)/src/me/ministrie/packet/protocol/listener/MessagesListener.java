package me.ministrie.packet.protocol.listener;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import me.ministrie.utils.component.ComponentUtil;
import net.kyori.adventure.text.Component;

public class MessagesListener extends PacketAdapter{

	public MessagesListener(Plugin plugin){
		super(plugin, PacketType.Play.Server.SYSTEM_CHAT);
	}
	
	@Override
	public void onPacketSending(PacketEvent event){
		PacketContainer packet = event.getPacket();
		WrappedChatComponent component = packet.getChatComponents().read(0);
		String json = component.getJson();
		Component parsing = ComponentUtil.parseComponent(json);
		String plainText = ComponentUtil.getComponentPlainText(parsing);
		if(plainText.equalsIgnoreCase("null")){
			event.setCancelled(true);
		}
	}
}
