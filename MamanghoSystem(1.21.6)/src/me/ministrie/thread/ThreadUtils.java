package me.ministrie.thread;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import me.ministrie.main.MamanghoSystem;

public class ThreadUtils{
	
	public static void runThreadSafe(Consumer<Boolean> task){
		if(Bukkit.isPrimaryThread()){
			task.accept(true);
		}else{
			Bukkit.getScheduler().runTask(MamanghoSystem.getInstance(), () -> {
				task.accept(false);
			});
		}
	}

	public static void runThreadSafe(Consumer<Boolean> task, int later){
		if(Bukkit.isPrimaryThread()){
			if(later <= 0){
				task.accept(true);
			}else{
				Bukkit.getScheduler().runTaskLater(MamanghoSystem.getInstance(), () -> {
					task.accept(true);
				}, later);
			}
		}else{
			if(later <= 0){
				Bukkit.getScheduler().runTask(MamanghoSystem.getInstance(), () -> {
					task.accept(false);
				});
			}else{
				Bukkit.getScheduler().runTaskLater(MamanghoSystem.getInstance(), () -> {
					task.accept(false);
				}, later);
			}
		}
	}
	
	public static void runAsyncThread(Runnable runnable){
		if(!Bukkit.isPrimaryThread()){
			runnable.run();
		}else{
			Bukkit.getAsyncScheduler().runNow(MamanghoSystem.getInstance(), (task) -> {
				runnable.run();
			});
		}
	}
	
	public static void runAsyncThread(Runnable runnable, int later, TimeUnit unit){
		if(!Bukkit.isPrimaryThread()){
			Bukkit.getAsyncScheduler().runDelayed(MamanghoSystem.getInstance(), (task) -> {
				runnable.run();
			}, later, unit);
		}else{
			Bukkit.getAsyncScheduler().runDelayed(MamanghoSystem.getInstance(), (task) -> {
				runnable.run();
			}, later, unit);
		}
	}
}
