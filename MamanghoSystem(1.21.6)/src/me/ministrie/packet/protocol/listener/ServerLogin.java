package me.ministrie.packet.protocol.listener;

import java.security.SecureRandom;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import me.ministrie.main.MamanghoSystem;
import me.ministrie.utils.MathUtil;

/**
 * 특정 유저들이 월드 시드 값을 통해 어떤 구조물이든 찾을 수 있는 것에 대한
 * 방어책입니다. 
 * 아 지잉짜... 핵쓰지 말라구용...
 * 
 * @author 최태성
 */
public class ServerLogin extends PacketAdapter{

	private static final SecureRandom RANDOM = new SecureRandom();
	
	public ServerLogin(MamanghoSystem plugin){
		super(plugin, PacketType.Play.Server.LOGIN);
	}
	
	@Override
	public void onPacketSending(PacketEvent event){
		PacketContainer packet = event.getPacket();
		StructureModifier<InternalStructure> modifier = packet.getStructures();
		for(InternalStructure structure : modifier.getValues()){
			if(structure.getHandle().getClass().getSimpleName().equalsIgnoreCase("CommonPlayerSpawnInfo")){
				structure.getLongs().write(0, MathUtil.randomizeHashedSeed(RANDOM.nextLong(1000000)));
			}
		}
		event.setPacket(packet);
	}
}
