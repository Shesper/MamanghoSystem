package me.ministrie.configs;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ministrie.utils.component.ComponentUtil;
import me.ministrie.utils.string.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.md_5.bungee.api.ChatColor;

public enum MessageSetting{
	
	DATA_LOAD_FAILED("messages.data.failed-loaded", "&c데이터 로드에 실패하였습니다."),
	CHAT_MESSAGE_FORMAT("messages.chat.message-format", "[{\"text\": \"<\", \"color\": \"#ffffff\"},{\"text\": \"{0}\", \"color\": \"#ffffff\"},{\"text\": \"{1}\", \"font\": \"prefix\", \"color\": \"#ffffff\"},{\"text\": \">\", \"color\": \"#ffffff\"},{\"text\": \" {2}\", \"color\": \"#ffffff\"}]"),
	CHAT_EMOTICON_FORMAT("messages.chat.emoticon-format", "[{\"text\": \"<\", \"color\": \"#ffffff\"},{\"text\": \"{0}\", \"color\": \"#ffffff\"},{\"text\": \"{1}\", \"font\": \"prefix\", \"color\": \"#ffffff\"},{\"text\": \"> \", \"color\": \"#ffffff\"},{\"text\": \"{2}\", \"font\": \"{3}\", \"color\": \"#ffffff\"}]"),
	CHAT_PAIR_EMOTICON_FORMAT("messages.chat.pair-emoticon-format", "[{\"text\": \"<\", \"color\": \"#ffffff\"},{\"text\": \"{0}\", \"color\": \"#ffffff\"},{\"text\": \"{1}\", \"font\": \"prefix\", \"color\": \"#ffffff\"},{\"text\": \"> \", \"color\": \"#ffffff\"},{\"text\": \"{2}\", \"font\": \"{3}\", \"color\": \"#ffffff\"},{\"text\": \" \", \"font\": \"space\"},{\"text\": \"{4}\", \"font\": \"{5}\", \"color\": \"#ffffff\"}]"),
	CHAT_MESSAGE_CONSOLE_FORMAT("messages.chat.messages-console-format", "[Chatting] <{0}> {1}"),
	CHAT_TABLIST_FORMAT("messages.chat.tablist.format", "[{\"text\": \"{0}\", \"color\": \"#ffffff\"},{\"text\": \"{1}\", \"font\": \"prefix\", \"color\": \"#ffffff\"},{\"text\": \" {2}\", \"color\": \"{3}\"}]"),
	CHAT_TABLIST_NORMAL_PING_COLOR("messages.chat.tablist.normal-ping-color", "#f7ef05"),
	CHAT_TABLIST_WARNING_PING_COLOR("messages.chat.tablist.warning-ping-color", "#b30909"),
	
	SYSTEM_JOIN_MESSAGES("system.messages.join-messages", "[{\"text\": \"총갤 마망호 서버에 입장하였습니다.\", \"color\": \"white\"},{\"text\": \"\n\"},{\"text\": \"마망호에 관한 이용 준수 사항 및, 추가된 컨텐츠에 대해선 \"},{\"text\": \"여기\", \"color\": \"#89eb34\", \"bold\": \"true\", \"italic\": \"true\", \"hoverEvent\": {\"action\": \"show_text\", \"contents\": \"클릭 시 총갤 마망호 노션 페이지로 이동합니다.\"}, \"clickEvent\": {\"action\": \"open_url\", \"value\": \"https://www.notion.so/2055bcfc69f280a0a8a1e8323d6c629f?source=copy_link\"}},{\"text\": \"를 클릭하여 노션을 확인해주세요.\", \"color\": \"white\"},{\"text\": \"\n\"},{\"text\": \"주기적인 마망호 업데이트 내용도 담겨있습니다.\", \"color\": \"white\"}]"),
	SYSTEM_NAMECHANGE_COMMAND_GUIDE("system.messages.namechange.command-guide", "/namechange [변경할 닉네임]"),
	SYSTEM_NAMECHANGE_OVER_LENGTH("system.messages.namechange.name-over-length", "닉네임은 최대 {0}자 까지 설정 가능합니다."),
	SYSTEM_NAMECHANGE_CHANGED("system.messages.namechange.changed", "닉네임이 변경되었습니다. 변경된 닉네임: {0}"),
	
	SYSTEM_SHARE_LOCATION_ENABLE("system.messages.share-location.enable", "위치 공유가 활성화 되었습니다."),
	SYSTEM_SHARE_LOCATION_DISABLE("system.messages.share-location.disable", "위치 공유가 비활성화 되었습니다."),
	
	SYSTEM_RESOURCEPACK_PROMPT("system.messages.resourcepack-prompt", "[{\"text\": \"총갤 마망호에 입장하기 위해선 서버에서 제공하는 리소스팩 다운로드가 필수적입니다.\", \"color\": \"#ffffff\"},{\"text\": \"\n\"},{\"text\": \"수락을 눌러 리소스팩 다운로드를 진행해주세요.\", \"color\": \"#ffffff\"},{\"text\": \"\n\"},{\"text\": \"리소스팩 다운로드를 거부할 경우 서버에 접속할 수 없습니다.\", \"color\": \"#e60b00\"}]"),
	
	FORMAT_OVERWORLD_TRANSLATE_KEY("format.worlds.overworld-translate-key", "chat.hover.worlds.overworld"),
	FORMAT_NETHER_TRANSLATE_KEY("format.worlds.nether-translate-key", "chat.hover.worlds.nether"),
	FORMAT_THE_END_TRANSLATE_KEY("format.worlds.the-end-translate-key", "chat.hover.worlds.the-end"),
	FORMAT_OVERWORLD_BANNER("format.worlds.banner.overworld", "[{\"text\": \"\uB200\", \"font\": \"ui\", \"color\": \"#ffffff\"}]"),
	FORMAT_NETHER_BANNER("format.worlds.banner.nether", "[{\"text\": \"\uB201\", \"font\": \"ui\", \"color\": \"#ffffff\"}]"),
	FORMAT_THE_END_BANNER("format.worlds.banner.the-end", "[{\"text\": \"\uB202\", \"font\": \"ui\", \"color\": \"#ffffff\"}]"),
	FORMAT_PRINT_LOCATION("froamt.print-location", "{0}님의 위치 - 월드: {1}, 위치: X: {2}, Y: {3}, Z: {4}");
	
	private String path;
	private String def;
	
	MessageSetting(String path, String def){
		this.path = path;
		this.def = def;
	}
	
	public String getPath(){
		return path;
	}
	
	public String getDefaultValue(){
		return def;
	}
	
	public static final String FILENAME = "plugins/MamanghoSystem/messages.yml";
	public static YamlConfiguration config = null;
	public static Map<MessageSetting, String> cache_values = new HashMap<>();
	
	public void sendMessages(Player online, Object... o){
		if(online != null){
			String msg = this.getValue(o);
			if(!msg.isEmpty()) online.sendMessage(ComponentUtil.parseComponent(msg));
		}
	}
	
	public void sendListMessages(Player online, Object... o){
		List<String> list = StringUtils.regexList(cache_values.getOrDefault(this, this.getDefaultValue()), o);
		if(online != null){
			list.forEach(message -> {
				if(!message.isEmpty()) online.sendMessage(ComponentUtil.parseComponent(message));
			});
		}
	}
	
	public void sendTitle(World world, Object... o){
		if(world == null) return;
		Title title = Title.title(Component.space(), ComponentUtil.parseComponent(this.getValue(o)), Times.times(Duration.ofMillis(0), Duration.ofSeconds(1), Duration.ofMillis(300)));
		for(Player online : world.getPlayers()){
			online.showTitle(title);
		}
	}
	
	public Component getComponent(Object... o){
		String str = cache_values.getOrDefault(this, this.getDefaultValue());
		if(str != null){
			String format = ChatColor.translateAlternateColorCodes('&', str);
			return ComponentUtil.parseComponent(StringUtils.regexString(format, o));
		}
		return Component.empty();
	}
	
	public String getValue(Object... o){
		String str = cache_values.getOrDefault(this, this.getDefaultValue());
		if(str != null){
			String format = ChatColor.translateAlternateColorCodes('&', str);
			return StringUtils.regexString(format, o);
		}
		return "";
	}
	
	public static void load(){
		cache_values.clear();
		boolean save = false;
		File file = new File(FILENAME);
		config = YamlConfiguration.loadConfiguration(file);
		for(MessageSetting setting : MessageSetting.values()){
			if(!config.isSet(setting.getPath())){
				config.set(setting.getPath(), setting.getDefaultValue());
				cache_values.put(setting, setting.getDefaultValue());
				save = true;
			}else{
				cache_values.put(setting, config.getString(setting.getPath()));
			}
		}
		if(save){
			try{
				config.save(file);
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}