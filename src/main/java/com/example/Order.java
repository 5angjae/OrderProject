package com.example;

public class Order {
    public String orderId;
    public String product;
    public int quantity;
    public double price;

    public Order() {} // Gson requires default constructor
}