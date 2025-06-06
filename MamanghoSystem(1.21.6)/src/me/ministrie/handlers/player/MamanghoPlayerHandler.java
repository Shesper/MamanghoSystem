package me.ministrie.handlers.player;

import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.commons.text.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import me.ministrie.api.data.player.PlayerData;
import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.BiomeInformation.BiomeSummary;
import me.ministrie.configs.MessageSetting;
import me.ministrie.configs.ServerSetting;
import me.ministrie.emoticon.Emoticon;
import me.ministrie.enchantments.CustomEnchantment;
import me.ministrie.enchantments.EnchantmentFinder;
import me.ministrie.functions.Callback;
import me.ministrie.gui.proccess.ProcessScreen;
import me.ministrie.handlers.data.player.PlayerDataHandler;
import me.ministrie.handlers.data.player.PlayerDataHandler.DataEnum;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.managers.PlayerCooldownManager;
import me.ministrie.packet.protocol.ProtocolTools;
import me.ministrie.utils.Pair;
import me.ministrie.utils.Trio;
import me.ministrie.utils.component.ComponentUtil;
import me.ministrie.utils.string.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;

public class MamanghoPlayerHandler implements MamanghoPlayer{
	
	private static final String DISPALY_EMOJI_FORMAT = ":%s:";
	private static final String DISPALY_PAIR_EMOJI_FORMAT = ":%s: :%s:";
	private static final String DISPLAY_FORMAT = "[{\"text\": \"%s\", \"color\": \"%s\"},{\"text\": \"%s\", \"color\": \"#ffffff\", \"font\": \"prefix\"}]";
	private static final String DISPLAY_PLAIN_FORMAT = "%s%s";
	
	private Player handler;
	private User user;
	private PlayerData data;
	private ProcessScreen processing;
	private PlayerCooldownManager cooldowns;
	private boolean empty;
	private BukkitTask equipmentTask;
	
	public MamanghoPlayerHandler(Player handler, boolean emptyConstruct){
		this.handler = handler;
		this.empty = true;
	}
	
	public MamanghoPlayerHandler(Player handler){
		this.handler = handler;
		this.user = LuckPermsProvider.get().getUserManager().getUser(handler.getUniqueId());
		this.data = new PlayerDataHandler(handler);
		this.cooldowns = new PlayerCooldownManager(this);
	}
	
	@Override
	public Player getPlayer(){
		return handler;
	}

	@Override
	public User getLuckpermsUser(){
		return user;
	}

	@Override
	public PlayerData getData(){
		return this.data;
	}
	
	@Override
	public String getCustomNickname(){
		return data.getData(DataEnum.CUSTOM_NICKNAME);
	}
	
	@Override
	public String getNickname(){
		return this.getCustomNickname() != null ? this.getCustomNickname() : this.handler.getName();
	}

	@Override
	public void setCustomNickname(String nickname){
		data.setData(DataEnum.CUSTOM_NICKNAME, nickname);	
		this.updateNameVisually();
	}
	
	@Override
	public Component getDisplaynameWithPrefix(String hexcolor){
		return ComponentUtil.parseComponent(DISPLAY_FORMAT.formatted(this.getNickname(), hexcolor, this.getPrefix()));
	}
	
	@Override
	public String getPlainDisplaynameWithPrefix(){
		return DISPLAY_PLAIN_FORMAT.formatted(this.getNickname(), StringEscapeUtils.unescapeJava(this.getPrefix().replace("B", "F")));
	}
	
	@Override
	public String getPrefix(){
		Group group = LuckPermsProvider.get().getGroupManager().getGroup(user.getPrimaryGroup());
		if(group != null) return group.getCachedData().getMetaData().getPrefix();
		return "";
	}
	
	@Override
	public void say(String messages){
		int maxlength = ServerSetting.CHAT_MESSAGE_MAX_LENGTH.getValue();
		if(messages.length() > maxlength){
			messages = messages.substring(0, maxlength);
		}
		final String finalMessages = messages;
		Component msg = MessageSetting.CHAT_MESSAGE_FORMAT.getComponent(this.getNickname(), this.getPrefix(), messages);
		
		MamanghoSystem.getTaskChainFactory().newChain().syncFirst(() -> {
			return Bukkit.getOnlinePlayers();
		}).asyncLast((users) -> {
			users.forEach(user -> user.sendMessage(this.mixinChat(user, msg)));
			Bukkit.getLogger().log(Level.INFO, MessageSetting.CHAT_MESSAGE_CONSOLE_FORMAT.getValue(this.getNickname(), finalMessages));
		}).execute();
	}
	
	@Override
	public void printIcon(Emoticon emoticon){
		Component msg = MessageSetting.CHAT_EMOTICON_FORMAT.getComponent(this.getNickname(), this.getPrefix(), emoticon.getValue(), emoticon.getFont());
		MamanghoSystem.getTaskChainFactory().newChain().syncFirst(() -> {
			return Bukkit.getOnlinePlayers();
		}).asyncLast((users) -> {
			users.forEach(user -> {
				user.sendMessage(this.mixinChat(user, msg));
				for(int i = 0; i < emoticon.getGab(); i++){
					user.sendMessage(Component.space());
				}
			});
			Bukkit.getLogger().log(Level.INFO, MessageSetting.CHAT_MESSAGE_CONSOLE_FORMAT.getValue(this.getNickname(), DISPALY_EMOJI_FORMAT.formatted(emoticon.getID())));
		}).execute();
	}
	
	@Override
	public void printIcon(Emoticon emoticon, boolean big){
		Component msg = MessageSetting.CHAT_EMOTICON_FORMAT.getComponent(this.getNickname(), this.getPrefix(), emoticon.getValue(), emoticon.getFont(big));
		MamanghoSystem.getTaskChainFactory().newChain().syncFirst(() -> {
			return Bukkit.getOnlinePlayers();
		}).asyncLast((users) -> {
			users.forEach(user -> {
				user.sendMessage(this.mixinChat(user, msg));
				for(int i = 0; i < (big ? emoticon.getGab()*1.5 : emoticon.getGab()); i++){
					user.sendMessage(Component.space());
				}
			});
			Bukkit.getLogger().log(Level.INFO, MessageSetting.CHAT_MESSAGE_CONSOLE_FORMAT.getValue(this.getNickname(), DISPALY_EMOJI_FORMAT.formatted(emoticon.getID())));
		}).execute();
	}
	
	@Override
	public void printPairIcon(Emoticon first, Emoticon second){
		Component msg = MessageSetting.CHAT_PAIR_EMOTICON_FORMAT.getComponent(this.getNickname(), this.getPrefix(), first.getValue(), first.getFont(), second.getValue(), second.getFont());
		MamanghoSystem.getTaskChainFactory().newChain().syncFirst(() -> {
			return Bukkit.getOnlinePlayers();
		}).asyncLast((users) -> {
			users.forEach(user -> {
				user.sendMessage(this.mixinChat(user, msg));
				for(int i = 0; i < first.getGab(); i++){
					user.sendMessage(Component.space());
				}
			});
			Bukkit.getLogger().log(Level.INFO, MessageSetting.CHAT_MESSAGE_CONSOLE_FORMAT.getValue(this.getNickname(), DISPALY_PAIR_EMOJI_FORMAT.formatted(first.getID(), second.getID())));
		}).execute();
	}
	
	public void load(Callback<MamanghoPlayer> callback){
		Bukkit.getAsyncScheduler().runNow(MamanghoSystem.getInstance(), (task) -> {
			if(task.isCancelled()) return;
			if(!MamanghoSystem.getInstance().isEnabled()) return;
			/* 데이터 로드 시작 */
			try{
				long time = System.currentTimeMillis();
				this.data.load();
				if(this.getCustomNickname() != null){
					this.handler.displayName(ComponentUtil.parseComponent(this.getCustomNickname()));
				}
				Bukkit.getLogger().log(Level.INFO, "[MamanghoSystem] player '" + handler.getName() + "' data loaded. " + (System.currentTimeMillis()-time) + "ms.");
				callback.done(this, null);	
			}catch(Exception e){
				callback.done(this, e);	
			}
		});
	}

	protected Component mixinChat(Player viewer, Component base){
		Pair<HoverEvent<Component>, ClickEvent> hoverFor = this.getHoverFor(viewer);
		return hoverFor.getValue() != null ? base.hoverEvent(hoverFor.getKey()).clickEvent(hoverFor.getValue()) :
			base.hoverEvent(hoverFor.getKey());
	}
	
	protected Pair<HoverEvent<Component>, ClickEvent> getHoverFor(Player viewer){
		Component hoverComponent = ComponentUtil.translatiableFrom("chat.hover.targetlocation", this.getDisplaynameWithPrefix("#ffffff"));
		boolean share = this.getData().<Boolean>getData(DataEnum.SHARE_LOCATION);
		ClickEvent clickevent = null;
		if(!viewer.isOp() && !share){
			hoverComponent = hoverComponent.append(Component.newline());
			hoverComponent = hoverComponent.append(ComponentUtil.translatiableFrom("chat.hover.targetlocation-blocked"));
		}else{
			Trio<String, String, String> worldtranslates = this.getWorldTranslateKey(viewer.getWorld());
			Component worldname = ComponentUtil.translatiableFrom(worldtranslates.getFirst());
			Component worldbanner = ComponentUtil.parseComponent(worldtranslates.getSecond());
			
			hoverComponent = hoverComponent.append(Component.newline());
			hoverComponent = hoverComponent.append(ComponentUtil.translatiableFrom("chat.hover.world", worldbanner, worldname));

			Location location = viewer.getLocation();
			Biome biome = viewer.getWorld().getBiome(location);
			BiomeSummary biomeSummary = MamanghoSystem.getBiomeInformation().getBiomeSummary(biome);
			Component biomebanner = ComponentUtil.parseComponent(biomeSummary.getBanner());
			Component biomename = ComponentUtil.translatiableFrom(biomeSummary.getTranslateKey());
			hoverComponent = hoverComponent.append(Component.newline());
			hoverComponent = hoverComponent.append(ComponentUtil.translatiableFrom("chat.hover.biome", biomebanner, biomename));
			String X = StringUtils.doubleToCell(location.getX(), "#.#");
			String Y = StringUtils.doubleToCell(location.getY(), "#.#");
			String Z = StringUtils.doubleToCell(location.getZ(), "#.#");
			hoverComponent = hoverComponent.append(Component.newline());
			hoverComponent = hoverComponent.append(ComponentUtil.translatiableFrom("chat.hover.location", ComponentUtil.parseComponent(X),
					ComponentUtil.parseComponent(Y), ComponentUtil.parseComponent(Z)));
			hoverComponent = hoverComponent.append(Component.newline()).append(Component.newline());
			if(viewer.isOp()){
				if(!share){
					hoverComponent = hoverComponent.append(ComponentUtil.translatiableFrom("chat.hover.denyclick"));
				}else{
					hoverComponent = hoverComponent.append(ComponentUtil.translatiableFrom("chat.hover.click"));
				}
			}else{
				hoverComponent = hoverComponent.append(ComponentUtil.translatiableFrom("chat.hover.click"));
			}
			clickevent = ClickEvent.suggestCommand(MessageSetting.FORMAT_PRINT_LOCATION.getValue(this.getNickname(), worldtranslates.getThird(), X, Y, Z));
		}
		return Pair.of(HoverEvent.showText(hoverComponent), clickevent);
	}
	
	protected Trio<String, String, String> getWorldTranslateKey(World world){
		if(world.getEnvironment().equals(Environment.NORMAL)){
			return Trio.of(MessageSetting.FORMAT_OVERWORLD_TRANSLATE_KEY.getValue(), MessageSetting.FORMAT_OVERWORLD_BANNER.getValue(), "오버월드");
		}else if(world.getEnvironment().equals(Environment.NETHER)){
			return Trio.of(MessageSetting.FORMAT_NETHER_TRANSLATE_KEY.getValue(), MessageSetting.FORMAT_NETHER_BANNER.getValue(), "네더월드");
		}else if(world.getEnvironment().equals(Environment.THE_END)){
			return Trio.of(MessageSetting.FORMAT_THE_END_TRANSLATE_KEY.getValue(), MessageSetting.FORMAT_THE_END_BANNER.getValue(), "엔더월드");
		}
		return Trio.of("", "", "");
	}

	@Override
	public void updateNameVisually(){
		String display = this.getPlainDisplaynameWithPrefix();
		if(display.length() > 16) display = display.substring(0, 16);
		ProtocolTools.setFakeAboveName(this, display);
	}

	@Override
	public ProcessScreen getProcessScreen(){
		return processing;
	}

	@Override
	public void setProcessScreen(ProcessScreen screen){
		this.processing = screen;
	}

	@Override
	public boolean isEmpty(){
		return empty;
	}

	@Override
	public PlayerCooldownManager getCooldownManager(){
		return cooldowns;
	}

	@Override
	public void brokenEquipment(ItemStack broken){
		EnchantmentFinder.findEnchantments(broken).forEach((k, v) -> {
			k.onUnequip(handler, broken.getType().getEquipmentSlot(), v);
		});
	}

	@Override
	public void initializeEquipment(){
		if(handler.isOnline()){
			PlayerInventory inv = handler.getInventory();
			EnchantmentFinder.getEnchantments().forEach((k, v) -> {
				v.onUnequip(handler);
			});
			if(equipmentTask != null) equipmentTask.cancel();
			equipmentTask = Bukkit.getScheduler().runTaskLater(MamanghoSystem.getInstance(), () -> {
				if(!handler.isOnline() || !handler.isValid() || handler.isDead()) return;
				ItemStack[] armors = inv.getArmorContents();
				for(ItemStack armor : armors){
					if(armor == null || armor.isEmpty()) continue;
					EnchantmentFinder.findEnchantments(armor).forEach((k, v) -> {
						k.onEquip(handler, armor.getType().getEquipmentSlot(), v);
					});
				}
				ItemStack hand = inv.getItemInMainHand();
				ItemStack offhand = inv.getItemInOffHand();
				if(hand != null && !hand.isEmpty() && hand.getType().getEquipmentSlot().equals(EquipmentSlot.HAND)){
					EnchantmentFinder.findEnchantments(hand).forEach((k, v) -> {
						k.onEquip(handler, hand.getType().getEquipmentSlot(), v);
					});
				}
				if(offhand != null && !offhand.isEmpty() && offhand.getType().getEquipmentSlot().equals(EquipmentSlot.OFF_HAND)){
					EnchantmentFinder.findEnchantments(offhand).forEach((k, v) -> {
						k.onEquip(handler, offhand.getType().getEquipmentSlot(), v);
					});
				}
				equipmentTask = null;
			}, 2);
		}
	}
	
	@Override
	public void initializeEquipment(List<EquipmentSlot> modifiedSlots){
		if(handler.isOnline()){
			PlayerInventory inv = handler.getInventory();
			EnchantmentFinder.fromCategories(modifiedSlots).forEach(e -> {
				e.onUnequip(handler);
			});
			if(equipmentTask != null) equipmentTask.cancel();
			equipmentTask = Bukkit.getScheduler().runTaskLater(MamanghoSystem.getInstance(), () -> {
				if(!handler.isOnline() || !handler.isValid() || handler.isDead()) return;
				ItemStack[] armors = inv.getArmorContents();
				for(ItemStack armor : armors){
					if(armor == null || armor.isEmpty()) continue;
					EnchantmentFinder.findEnchantments(armor).forEach((k, v) -> {
						k.onEquip(handler, armor.getType().getEquipmentSlot(), v);
					});
				}
				ItemStack hand = inv.getItemInMainHand();
				ItemStack offhand = inv.getItemInOffHand();
				if(hand != null && !hand.isEmpty() && hand.getType().getEquipmentSlot().equals(EquipmentSlot.HAND)){
					EnchantmentFinder.findEnchantments(hand).forEach((k, v) -> {
						k.onEquip(handler, hand.getType().getEquipmentSlot(), v);
					});
				}
				if(offhand != null && !offhand.isEmpty() && offhand.getType().getEquipmentSlot().equals(EquipmentSlot.OFF_HAND)){
					EnchantmentFinder.findEnchantments(offhand).forEach((k, v) -> {
						k.onEquip(handler, offhand.getType().getEquipmentSlot(), v);
					});
				}
				equipmentTask = null;
			}, 2);
		}
	}

	@Override
	public void onTrigger(Object value){
		PlayerInventory inv = handler.getInventory();
		if(!handler.isOnline() || !handler.isValid() || handler.isDead()) return;
		ItemStack[] armors = inv.getArmorContents();
		for(ItemStack armor : armors){
			if(armor == null || armor.isEmpty()) continue;
			EnchantmentFinder.findEnchantments(armor).forEach((k, v) -> {
				k.onTrigger(handler, armor.getType().getEquipmentSlot(), value, v);
			});
		}
		ItemStack hand = inv.getItemInMainHand();
		ItemStack offhand = inv.getItemInOffHand();
		if(hand != null && !hand.isEmpty() && hand.getType().getEquipmentSlot().equals(EquipmentSlot.HAND)){
			EnchantmentFinder.findEnchantments(hand).forEach((k, v) -> {
				k.onTrigger(handler, hand.getType().getEquipmentSlot(), value, v);
			});
		}
		if(offhand != null && !offhand.isEmpty() && offhand.getType().getEquipmentSlot().equals(EquipmentSlot.OFF_HAND)){
			EnchantmentFinder.findEnchantments(offhand).forEach((k, v) -> {
				k.onTrigger(handler, offhand.getType().getEquipmentSlot(), value, v);
			});
		}
	}

	@Override
	public double getIncreaseDamage(LivingEntity victim, double damage){
		if(!handler.isOnline() || !handler.isValid() || handler.isDead()) return damage;
		PlayerInventory inv = handler.getInventory();
		ItemStack[] armors = inv.getArmorContents();
		for(ItemStack armor : armors){
			if(armor == null || armor.isEmpty()) continue;
			for(Entry<CustomEnchantment, Integer> e : EnchantmentFinder.findEnchantments(armor).entrySet()){
				CustomEnchantment k = e.getKey();
				int v = e.getValue();
				damage = k.getIncreaseDamage(handler, victim, damage, v);
			}
		}
		ItemStack hand = inv.getItemInMainHand();
		ItemStack offhand = inv.getItemInOffHand();
		if(hand != null && !hand.isEmpty() && hand.getType().getEquipmentSlot().equals(EquipmentSlot.HAND)){
			for(Entry<CustomEnchantment, Integer> e : EnchantmentFinder.findEnchantments(hand).entrySet()){
				CustomEnchantment k = e.getKey();
				int v = e.getValue();
				damage = k.getIncreaseDamage(handler, victim, damage, v);
			}
		}
		if(offhand != null && !offhand.isEmpty() && offhand.getType().getEquipmentSlot().equals(EquipmentSlot.OFF_HAND)){
			for(Entry<CustomEnchantment, Integer> e : EnchantmentFinder.findEnchantments(offhand).entrySet()){
				CustomEnchantment k = e.getKey();
				int v = e.getValue();
				damage = k.getIncreaseDamage(handler, victim, damage, v);
			}
		}
		return damage;
	}

	@Override
	public double getReduceDamage(double damage){
		if(!handler.isOnline() || !handler.isValid() || handler.isDead()) return damage;
		PlayerInventory inv = handler.getInventory();
		ItemStack[] armors = inv.getArmorContents();
		for(ItemStack armor : armors){
			if(armor == null || armor.isEmpty()) continue;
			for(Entry<CustomEnchantment, Integer> e : EnchantmentFinder.findEnchantments(armor).entrySet()){
				CustomEnchantment k = e.getKey();
				int v = e.getValue();
				damage = k.getReduceDamage(handler, damage, v);
			}
		}
		ItemStack hand = inv.getItemInMainHand();
		ItemStack offhand = inv.getItemInOffHand();
		if(hand != null && !hand.isEmpty() && hand.getType().getEquipmentSlot().equals(EquipmentSlot.HAND)){
			for(Entry<CustomEnchantment, Integer> e : EnchantmentFinder.findEnchantments(hand).entrySet()){
				CustomEnchantment k = e.getKey();
				int v = e.getValue();
				damage = k.getReduceDamage(handler, damage, v);
			}
		}
		if(offhand != null && !offhand.isEmpty() && offhand.getType().getEquipmentSlot().equals(EquipmentSlot.OFF_HAND)){
			for(Entry<CustomEnchantment, Integer> e : EnchantmentFinder.findEnchantments(offhand).entrySet()){
				CustomEnchantment k = e.getKey();
				int v = e.getValue();
				damage = k.getReduceDamage(handler, damage, v);
			}
		}
		return damage;
	}
}
