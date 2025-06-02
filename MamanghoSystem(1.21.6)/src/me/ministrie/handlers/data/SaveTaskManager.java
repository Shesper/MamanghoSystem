package me.ministrie.handlers.data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import me.ministrie.api.data.player.PlayerData;

public class SaveTaskManager{
	
	private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
	private final Thread workerThread;
	private volatile boolean running = true;
	private volatile boolean gracefulShutdown = false;

	public SaveTaskManager(PlayerData handler){
		workerThread = new Thread(() -> {
			while(running || !taskQueue.isEmpty()){
				try{
					Runnable task = taskQueue.take();
					task.run();
				}catch (InterruptedException e){
					if(gracefulShutdown && taskQueue.isEmpty()){
						break;
					}
					Thread.currentThread().interrupt();
				}
			}
		}, "UserDataSaveThread-" + handler.getId());
		workerThread.start();
	}

	public void submit(Runnable task){
		if(!running) throw new IllegalStateException("SaveTaskManager is shutting down.");
		taskQueue.add(task);
	}

	public void saveShutdown(){
		running = false;
		workerThread.interrupt();
		try{
			workerThread.join();
		}catch (InterruptedException e){
			Thread.currentThread().interrupt();
		}
	}
	
	public void saveGracefully(){
		gracefulShutdown = true;
		running = false;
		workerThread.interrupt();
	}
}