package me.ministrie.utils;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtil{

	private static final SecureRandom random = new SecureRandom();
	
	public static int underZero(int value){
		if(value <= 0) return 0;
		return value;
	}
	
	public static double underZero(double value){
		if(value <= 0) return 0;
		return value;
	}
	
	public static long underZero(long value){
		if(value <= 0) return 0;
		return value;
	}
	
	public static double toPercentage(double value){
		if(value <= 0) return 0.0;
		return value*0.01;
	}
	
	public static double toUnsafePercentage(double value){
		return value*0.01;
	}
	
	public static int gab(int value, int overmax){
		if(value >= overmax) return overmax;
		return value;
	}
	
	public static float gab(float value, float overmax){
		if(value >= overmax) return overmax;
		return value;
	}
	
	public static double gab(double value, double overmax){
		if(value >= overmax) return overmax;
		return value;
	}
	
	public static long gab(long value, long overmax){
		if(value >= overmax) return overmax;
		return value;
	}
	
	public static float gab(float value, float overmax, float undermax){
		if(value > overmax) return overmax;
		if(value < undermax) return undermax;
		return value;
	}
	
	public static double gab(double value, double overmax, double undermax){
		if(value > overmax) return overmax;
		if(value < undermax) return undermax;
		return value;
	}
	
	public static long gab(long value, long overmax, long undermax){
		if(value > overmax) return overmax;
		if(value < undermax) return undermax;
		return value;
	}
	
	public static int gab(int value, int overmax, int undermax){
		if(value > overmax) return overmax;
		if(value < undermax) return undermax;
		return value;
	}
	
	public static long under(long value, long undermax){
		if(value < undermax) return undermax;
		return value;
	}
	
	public static int under(int value, int undermax){
		if(value < undermax) return undermax;
		return value;
	}
	
	public static double under(double value, double undermax){
		if(value < undermax) return undermax;
		return value;
	}
	
	public static double gab2(double value, double overmax, double under, double understatic){
		if(value > overmax) return overmax;
		if(value <= under) return understatic;
		return value;
	}
	
	public static boolean percent(double percentage){
		return percentage >= random.nextDouble();
	}
	
	public static Long adaptLong(Object object){
		if(object == null) return 0l;
		if(object instanceof Float){
			return Float.valueOf((float)object).longValue();
		}else if(object instanceof Double){
			return Double.valueOf((double)object).longValue();
		}else if(object instanceof Integer){
			return Integer.valueOf((int)object).longValue();
		}else if(object instanceof Long){
			return (long)object;
		}
		return null;
	}
	
	public static long randomizeHashedSeed(long hashedSeed){
		int length = Long.toString(hashedSeed).length();
		if(length > 18) length = 18; 
		long min = (long)Math.pow(10.0D, (length - 1));
		long max = (long)(Math.pow(10.0D, length) - 1.0D);
		return ThreadLocalRandom.current().nextLong(min, max + 1L);
	}
}
