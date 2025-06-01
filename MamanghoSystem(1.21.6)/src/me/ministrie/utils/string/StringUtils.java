package me.ministrie.utils.string;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class StringUtils{

	private static final Pattern REGEX_PATTERN = Pattern.compile("\\{([0-9]+)\\}");
	
	public static String doubleToCell(double value, String format){
		double result = value;
		DecimalFormat form = new DecimalFormat(format);
		return form.format(result);
	}
	
	public static String stripColor(String str){
		if(str == null || str.isEmpty()) return "";
		return str.replaceAll("§([a-z0-9])", "");
	}
	
	public static String[] splitString(String string, String split){
		if(string == null || string.isEmpty()) return new String[]{};
		return string.split(split);
	}
	
	public static List<String> splitStringList(String string, String split){
		if(string == null || string.isEmpty()) return new ArrayList<>();
		return Arrays.asList(string.split(split));
	}
	
	public static List<String> deserializeString(String format, boolean transColor){
		List<String> result = new ArrayList<>();
		if(format == null || format.isEmpty()) return result;
		String[] sp = format.split("\\|");
		for(String str : sp){
			result.add(transColor ? ChatColor.translateAlternateColorCodes('&', str) : str);
		}
		return result;
	}
	
	public static List<String> translateColorCodes(List<String> formats){
		List<String> result = new ArrayList<>();
		if(formats == null || formats.isEmpty()) return result;
		for(String format : formats){
			result.add(ChatColor.translateAlternateColorCodes('&', format));
		}
		return result;
	}
	
	public static String regexString(String format, Object... o){
		if(format != null){
			String text = ChatColor.translateAlternateColorCodes('&', format);
			Matcher matcher = REGEX_PATTERN.matcher(text);
			while(matcher.find()){
				String m = matcher.group(1);
				int array = Integer.parseInt(m);
	         	if(o.length > array){
					String replacement = "{" + m + "}";
					text = text.replace(replacement, o[array] == null ? "" : o[array].toString());
	         	}else{
	         		break;
	         	}
			}
			return text;
		}
		return "";
	}
	
	public static String regexString(String format, String... o){
		if(format != null){
			String text = ChatColor.translateAlternateColorCodes('&', format);
			Matcher matcher = REGEX_PATTERN.matcher(text);
			while(matcher.find()){
				String m = matcher.group(1);
				int array = Integer.parseInt(m);
				if(o.length > array){
					String replacement = "{" + m + "}";
					text = text.replace(replacement, o[array] == null ? "" : o[array]);
				}else{	
					break;
				}
			}
			return text;
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> regexList(List<String> formats, Object... o){
		List<String> list = new ArrayList<String>(formats);
		int arr = 0;
		List<ReplaceList> rplist = new ArrayList<>();
        
		for(String s: list){
			String str = ChatColor.translateAlternateColorCodes('&', s);
			Matcher matcher = REGEX_PATTERN.matcher(s);
			while(matcher.find()){
				String m = matcher.group(1);
				int array = Integer.parseInt(m);
				if(o.length > array){
					String replacement = "{" + m + "}";
					if(!(o[array] instanceof List)){
						str = str.replace(replacement, o[array] == null ? "" : o[array].toString());
					}else{
						rplist.add(new ReplaceList(replacement, (List<String>) o[array]));
					}      	
				}else{	
					break;
				}
			}
			list.set(arr++, str);
		}

		if(!rplist.isEmpty()){
			for(ReplaceList rp : rplist){
				int index = ReplaceList.searchSymbolIndex(list, rp.symbol());
				if(index != -1){
					if(rp.value().isEmpty()){
						list.remove(index);
					}else{
						list.remove(index);
						list.addAll(index, rp.value());
					}
				}
			}
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> regexList(String list_formats, Object... o){
		
		String[] split = list_formats.split("\\|");
		List<String> list = StringUtils.translateColorCodes(Arrays.asList(split));
        
		int arr = 0;
		List<ReplaceList> rplist = new ArrayList<>();
        
		for(String s: list){
			String str = s;
			Matcher matcher = REGEX_PATTERN.matcher(s);

			while(matcher.find()){
				String m = matcher.group(1);
				int array = Integer.parseInt(m);
				if(o.length > array){
					String replacement = "{" + m + "}";
					if(!(o[array] instanceof List)){
						str = str.replace(replacement, o[array] == null ? "" : o[array].toString());
					}else{
						rplist.add(new ReplaceList(replacement, (List<String>) o[array]));
					}      	
				}else{	
					break;
				}
			}
			list.set(arr++, str);
		}

		if(!rplist.isEmpty()){
			for(ReplaceList rp : rplist){
				int index = ReplaceList.searchSymbolIndex(list, rp.symbol());
				if(index != -1){
					if(rp.value().isEmpty()){
						list.remove(index);
					}else{
						list.remove(index);
						list.addAll(index, rp.value());
					}
				}
			}
		}
		return list;
	}
	
	public static final class ReplaceList{
		
		private String symbol;
		private List<String> value;
		
		public ReplaceList(String symbol, List<String> value){
			this.symbol = symbol;
			this.value = value;
		}
		
		public String symbol(){
			return symbol;
		}
		
		public List<String> value(){
			return value;
		}
		
		public static int searchSymbolIndex(List<String> list, String symbol){
			if(list == null || list.isEmpty()) return -1;
			
			int index = 0;
			for(String comp : list){
				if(comp.equals(symbol)) return index;
				index++;
			}
			return -1;
		}
	}
}