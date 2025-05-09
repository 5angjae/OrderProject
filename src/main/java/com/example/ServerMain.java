package com.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        OrderBackupService backupService = new OrderBackupService();
        backupService.setDaemon(true);
        backupService.start();

        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new OrderHandler(backupService)), "/orders");
        server.setHandler(handler);

        server.start();
        System.out.println("서버 실행됨: http://localhost:8080/orders");
        server.join();
    }
}