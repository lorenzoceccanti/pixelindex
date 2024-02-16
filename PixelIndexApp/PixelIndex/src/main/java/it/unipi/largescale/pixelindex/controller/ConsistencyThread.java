package it.unipi.largescale.pixelindex.controller;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsistencyThread extends Thread{
    private boolean running = true;
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    @Override
    public void run(){
        try{
            while(running || !taskQueue.isEmpty()){
                Runnable task = taskQueue.take();
                task.run();
                int DELAY = 2500;
                Thread.sleep(DELAY);
            }
        }catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
            System.out.println("The consistency thread has been interrupted");
        }
    }

    public void addTask(Runnable task) {
        taskQueue.add(task);
    }
    public void stopThread() {
        this.running = false;
    }
}
