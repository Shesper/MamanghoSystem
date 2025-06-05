package me.ministrie.handlers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent.EquipmentChange;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.MessageSetting;
import me.ministrie.configs.ServerSetting;
import me.ministrie.datapack.recipe.shapes.ExclusiveShapeRecipes;
import me.ministrie.datapack.recipe.smithings.ExclusiveSmithingRecipes;
import me.ministrie.enchantments.types.EternalBindingCurseEnchant;
import me.ministrie.functions.Callback;
import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.TaskHolder;
import me.ministrie.gui.proccess.ProcessScreen;
import me.ministrie.gui.types.emoticon.EmoticonGui;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.managers.DamageTickController;
import me.ministrie.packet.protocol.ProtocolTools;
import me.ministrie.thread.ThreadUtils;
import me.ministrie.utils.component.ComponentUtil;
import net.kyori.adventure.text.format.TextColor;

public class PlayerListenerHandler implements Listener{

	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		event.joinMessage(null);
		Player p = event.getPlayer();
		MamanghoPlayer.getEmpty(p).initializeEquipment();
		MamanghoSystem.getPlayerManager().loadAndCache(p, new Callback<MamanghoPlayer>(){
			@Override
			public void done(MamanghoPlayer player, Throwable error){
				if(error != null){
					error.printStackTrace();
					ThreadUtils.runThreadSafe((v) -> {
						p.kick(MessageSetting.DATA_LOAD_FAILED.getComponent());
					});
					return;
				}
				MamanghoSystem.getPlayerManager().cachePlayer(player);
				MessageSetting.SYSTEM_JOIN_MESSAGES.sendMessages(p);
				Bukkit.broadcast(ComponentUtil.translatiableFrom("multiplayer.player.joined", player.getDisplaynameWithPrefix("#faf219")).color(TextColor.fromHexString("#faf219")));
				
				String url = ServerSetting.RESOURCEPACK_URL.getValue();
				if(url != null && !url.isEmpty()){
					if(!p.isOp()){
						p.setResourcePack(ServerSetting.RESOURCEPACK_UUID.fromUUID(), url, ServerSetting.RESOURCEPACK_SHA1_HASH.<String>getValue(), MessageSetting.SYSTEM_RESOURCEPACK_PROMPT.getComponent(), true);
					}
				}
				ThreadUtils.runThreadSafe((v) -> {
					player.updateNameVisually();
					ProtocolTools.updateOtherFakeAboveName(player);
				});
			}
		});
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		event.quitMessage(null);
		MamanghoPlayer player = MamanghoSystem.getPlayerManager().removePlayer(event.getPlayer());
		if(player != null){
			Bukkit.broadcast(ComponentUtil.translatiableFrom("multiplayer.player.left", player.getDisplaynameWithPrefix("#faf219")).color(TextColor.fromHexString("#faf219")));
		}
		Player online = event.getPlayer();
		InventoryView view = online.getOpenInventory();
		Inventory inv = view.getTopInventory();
		InventoryHolder holder = inv.getHolder();
		if(holder != null && holder instanceof TaskHolder task){
			task.closeTask();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEntityDeath(EntityDeathEvent event){
		if(event.getEntityType().equals(EntityType.PLAYER)){
			Player player = (Player) event.getEntity();
			Enchantment curse = Enchantment.getByKey(EternalBindingCurseEnchant.key);
			Map<EquipmentSlot, ItemStack> curseItems = Maps.newHashMap();
			EntityEquipment equipments = player.getEquipment();
			for(ItemStack armor : equipments.getArmorContents()){
				if(armor == null || armor.isEmpty()) continue;
				if(armor.containsEnchantment(curse)){
					curseItems.put(armor.getType().getEquipmentSlot(), armor.clone());
					equipments.setItem(armor.getType().getEquipmentSlot(), null);
					event.getDrops().remove(armor);
				}
			}
			if(!curseItems.isEmpty()){
				ThreadUtils.runThreadSafe((e) -> {
					curseItems.forEach((k, v) ->  equipments.setItem(k, v));
				}, 1);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		event.deathMessage(ComponentUtil.extractTranslatable(event.deathMessage()));
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onChat(AsyncChatEvent event){
		if(event.isCancelled()) return;
		event.setCancelled(true);
		Player player = event.getPlayer();
		MamanghoPlayer user = MamanghoSystem.getPlayerManager().getPlayer(player);
		if(user == null) return;
		String messages = ComponentUtil.getComponentPlainText(event.message());
		if(user.getProcessScreen() != null){
			if(messages.equalsIgnoreCase("cancel") || messages.equalsIgnoreCase("취소") || messages.equalsIgnoreCase("cnlth")){
				user.getProcessScreen().cancelProcess();
				return;
			}
			if(user.getProcessScreen().writeValue(user.getProcessScreen().getProcess(), messages)) return;
		}
		user.say(messages);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onClickInventory(InventoryClickEvent event){
		Inventory inv = event.getInventory();
		InventoryHolder holder = inv.getHolder();
		if(holder != null && holder instanceof ScreenHolder){
			ScreenHolder screenHolder = (ScreenHolder) holder;
			Screen screen = screenHolder.getScreen();
			if(screen.isCancelledDefault()) event.setCancelled(true);
			screen.click(event);
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onOpenInventory(InventoryOpenEvent event){
		Inventory inv = event.getInventory();
		InventoryHolder holder = inv.getHolder();
		if(holder != null){
			if(holder instanceof ScreenHolder sh){
				sh.openTask();
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onCloseInventory(InventoryCloseEvent event){
		Inventory inv = event.getInventory();
		InventoryHolder holder = inv.getHolder();
		if(holder != null && holder instanceof ScreenHolder screenHolder){
			screenHolder.closeTask();
			if(screenHolder.getScreen() instanceof ProcessScreen){
				MamanghoPlayer player = MamanghoPlayer.getPlayer(event.getPlayer());
				if(player != null) player.setProcessScreen(null);
			}
			if(screenHolder.getScreen().getPreviousScreen() != null && screenHolder.possiblePreviousScreen()){
				Bukkit.getScheduler().runTask(MamanghoSystem.getInstance(), () -> {
					screenHolder.getScreen().getPreviousScreen().open();
				});
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onInteract(PlayerSwapHandItemsEvent event){
		Player p = event.getPlayer();
		if(p.isSneaking()){
			MamanghoPlayer player = MamanghoSystem.getPlayerManager().getPlayer(event.getPlayer());
			if(player != null){
				event.setCancelled(true);
				EmoticonGui g = new EmoticonGui(player);
				g.open();
			}
		}
	}
	
	@EventHandler
	public void prepareCrafing(PrepareItemCraftEvent event){
		CraftingInventory crafting = event.getInventory();
		Recipe recipe = crafting.getRecipe();
		if(recipe != null && recipe instanceof ShapedRecipe shape){
			ExclusiveShapeRecipes exclusive = ExclusiveShapeRecipes.fromExclusive(shape);
			if(exclusive != null){
				if(!exclusive.checkCondition(crafting)){
					crafting.setResult(null);
					return;
				}
				crafting.setResult(exclusive.export(crafting.getResult()));
				return;
			}
			for(ItemStack material : crafting.getMatrix()){
				if(material == null || material.isEmpty()) continue;
				ItemMeta meta = material.getItemMeta();
				CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
				if(!cmd.getFloats().isEmpty()){
					crafting.setResult(null); /* 허용되지 않은 커스텀 모델이 존재하는 아이템은 조합할 수 없음. */
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void prepareSmithing(PrepareSmithingEvent event){
		Recipe recipe = event.getInventory().getRecipe();
		if(recipe != null && recipe instanceof SmithingRecipe smithingRecipe){
			ExclusiveSmithingRecipes exclusive = ExclusiveSmithingRecipes.fromExclusive(smithingRecipe);
			if(exclusive != null){
				if(!exclusive.checkCondition(event.getInventory())){
					event.setResult(null);
					return;
				}
				event.setResult(exclusive.export(event.getResult()));
			}
		}
	}
	
	@EventHandler
	public void onAdvancementDone(PlayerAdvancementDoneEvent event){
		event.message(ComponentUtil.extractTranslatable(event.message()));
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onDamageEvent(EntityDamageEvent event){
		if(event.isCancelled()) return;
		Entity entity = event.getEntity();
		if(event instanceof EntityDamageByEntityEvent entityEvent){
			Entity damager = entityEvent.getDamager();
			if(damager instanceof Player user){
				MamanghoPlayer attacker = MamanghoPlayer.getPlayer(user);
				if(attacker != null){
					if(entity instanceof LivingEntity living){
						event.setDamage(attacker.getIncreaseDamage(living, event.getDamage()));
					}
				}
			}
		}
		if(entity instanceof Player user){
			MamanghoPlayer victim = MamanghoPlayer.getPlayer(user);
			if(victim != null){
				event.setDamage(victim.getReduceDamage(event.getDamage()));
				victim.onTrigger(event);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onDamageEntityEvent(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		Player attacker = null;
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();
		if(entity instanceof LivingEntity living){
			boolean projectile = false;
			if(damager instanceof Projectile){
				projectile = true;
				Projectile proj = (Projectile) damager;
				if(proj.getShooter() instanceof Player player){
					attacker = player;
				}else{
					damager = proj.getShooter() != null ? (Entity) proj.getShooter() : damager;
				}
			}else if(damager instanceof Player player){
				attacker = player;
			}
			ThreadUtils.runThreadSafe((v) -> living.setNoDamageTicks(0), 1);
			DamageTickController controller = MamanghoSystem.getDamageTickController();
			if(attacker == null && entity instanceof Player player && damager instanceof Mob){
				if(controller.isVictimPlayerTick(player)){
					event.setCancelled(true);
					return;
				}
				controller.damageTickForMonster(player, ServerSetting.DAMAGE_TICK.<Integer>getValue());
			}
			if(attacker != null && !projectile){
				if(controller.isDamageTicking(attacker, living)){
					event.setCancelled(true);
					return;
				}
				controller.damageTicking(attacker, living, ServerSetting.DAMAGE_TICK.<Integer>getValue());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerBreakItem(PlayerItemBreakEvent event){
		MamanghoPlayer player = MamanghoPlayer.getPlayer(event.getPlayer());
		if(player != null){
			player.brokenEquipment(event.getBrokenItem());
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onChangeEquipment(EntityEquipmentChangedEvent event){
		if(event.getEntityType().equals(EntityType.PLAYER)){
			Player changer = (Player) event.getEntity();
			MamanghoPlayer player = MamanghoPlayer.getOrEmpty(changer);
			Map<EquipmentSlot, Boolean> checks = Maps.newHashMap();
			List<EquipmentSlot> modifiedSlots = Lists.newArrayList();
			for(Entry<EquipmentSlot, EquipmentChange> e : event.getEquipmentChanges().entrySet()){
				EquipmentSlot k = e.getKey();
				EquipmentChange v = e.getValue();
				ItemStack before = v.oldItem();
				ItemStack after = v.newItem();
				if(before != null && after != null){
					boolean same = before.getType().equals(after.getType()) && before.getEnchantments().hashCode() == after.getEnchantments().hashCode();
					if(same){
						ItemMeta bmeta = before.getItemMeta();
						ItemMeta ameta = after.getItemMeta();
						if(bmeta instanceof Damageable bd && ameta instanceof Damageable ad){
							if(bd.getDamage() != ad.getDamage()){
								return;
							}
						}
					}
				}
				modifiedSlots.add(k);
				checks.put(k, true);
			}
			if(checks.containsValue(true)){
				player.initializeEquipment(modifiedSlots);
			}
			checks.clear();
			checks = null;
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onFoodChange(FoodLevelChangeEvent event){
		if(event.isCancelled()) return;
		MamanghoPlayer player = MamanghoPlayer.getPlayer(event.getEntity());
		if(player != null){
			player.onTrigger(event);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onRegainHealth(EntityRegainHealthEvent event){
		if(event.isCancelled()) return;
		MamanghoPlayer player = MamanghoPlayer.getPlayer(event.getEntity().getUniqueId());
		if(player != null){
			player.onTrigger(event);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event){
		if(event.isCancelled()) return;
		MamanghoPlayer player = MamanghoPlayer.getPlayer(event.getPlayer());
		if(player != null){
			player.onTrigger(event);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onExpChange(PlayerExpChangeEvent event){
		MamanghoPlayer player = MamanghoPlayer.getPlayer(event.getPlayer());
		if(player != null){
			player.onTrigger(event);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onShootBow(EntityShootBowEvent event){
		if(event.isCancelled()) return;
		MamanghoPlayer player = MamanghoPlayer.getPlayer(event.getEntity().getUniqueId());
		if(player != null){
			player.onTrigger(event);
		}
	}
}
