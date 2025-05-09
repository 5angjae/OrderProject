package com.example;

import com.google.gson.Gson;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.client.util.StringContentProvider;

import java.util.Arrays;
import java.util.List;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        HttpClient client = new HttpClient();
        client.start();

        List<Order> orders = Arrays.asList(
            createOrder("O-1001", "apple", 5, 1.2),
            createOrder("O-1002", "banana", 3, 0.8),
            createOrder("O-1003", "apple", 2, 1.5),
            createOrder("O-1004", "orange", 4, 1.0)
        );

        Gson gson = new Gson();
        String json = gson.toJson(orders);

        ContentResponse response = client.newRequest("http://localhost:8080/orders")
                .method(HttpMethod.POST)
                .header(HttpHeader.CONTENT_TYPE, "application/json")
                .content(new StringContentProvider(json))
                .send();

        System.out.println("응답: " + response.getContentAsString());
        client.stop();
    }

    private static Order createOrder(String id, String product, int qty, double price) {
        Order o = new Order();
        o.orderId = id;
        o.product = product;
        o.quantity = qty;
        o.price = price;
        return o;
    }
}