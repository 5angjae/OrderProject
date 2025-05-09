package com.example;

import com.google.gson.Gson;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OrderBackupService extends Thread {
    private final BlockingQueue<Order> queue = new LinkedBlockingQueue<>();
    private final File file = new File("orders_backup.txt");
    private final Gson gson = new Gson();

    public void enqueue(Order order) {
        queue.offer(order);
    }

    @Override
    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            while (true) {
                Order order = queue.take();
                synchronized (writer) {
                    writer.write(gson.toJson(order));
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}