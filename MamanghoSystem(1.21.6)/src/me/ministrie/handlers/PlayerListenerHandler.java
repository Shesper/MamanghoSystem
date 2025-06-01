package me.ministrie.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingRecipe;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.MessageSetting;
import me.ministrie.configs.ServerSetting;
import me.ministrie.datapack.recipe.ExclusiveRecipes;
import me.ministrie.functions.Callback;
import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.TaskHolder;
import me.ministrie.gui.types.emoticon.EmoticonGui;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.packet.protocol.ProtocolTools;
import me.ministrie.thread.ThreadUtils;
import me.ministrie.utils.component.ComponentUtil;
import net.kyori.adventure.text.format.TextColor;

public class PlayerListenerHandler implements Listener{

	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		event.joinMessage(null);
		Player p = event.getPlayer();
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
				Bukkit.broadcast(ComponentUtil.translatiableFrom("multiplayer.player.joined", player.getDisplaynameWithPrefix()).color(TextColor.fromHexString("#faf219")));
				
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
			Bukkit.broadcast(ComponentUtil.translatiableFrom("multiplayer.player.left", player.getDisplaynameWithPrefix()).color(TextColor.fromHexString("#faf219")));
		}
		Player online = event.getPlayer();
		InventoryView view = online.getOpenInventory();
		Inventory inv = view.getTopInventory();
		InventoryHolder holder = inv.getHolder();
		if(holder != null && holder instanceof TaskHolder task){
			task.closeTask();
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		event.deathMessage(ComponentUtil.extractTranslatable(event.deathMessage()));
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onChat(AsyncChatEvent event){
		if(event.isCancelled()) return;
		event.setCancelled(true);
		Player player = event.getPlayer();
		MamanghoPlayer user = MamanghoSystem.getPlayerManager().getPlayer(player);
		if(user == null) return;
		user.say(ComponentUtil.getComponentPlainText(event.message()));
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
		if(holder != null && holder instanceof TaskHolder task){
			task.openTask();
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onCloseInventory(InventoryCloseEvent event){
		Inventory inv = event.getInventory();
		InventoryHolder holder = inv.getHolder();
		if(holder != null && holder instanceof ScreenHolder screenHolder){
			if(screenHolder instanceof TaskHolder task){
				task.closeTask();
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
	public void prepareSmithing(PrepareSmithingEvent event){
		Recipe recipe = event.getInventory().getRecipe();
		if(recipe != null && recipe instanceof SmithingRecipe smithingRecipe){
			ExclusiveRecipes exclusive = ExclusiveRecipes.fromExclusive(smithingRecipe);
			if(exclusive != null){
				if(!exclusive.checkCondition(event.getInventory())){
					event.setResult(null);
					return;
				}
				event.setResult(exclusive.export(event.getResult()));
			}
		}
	}
}
