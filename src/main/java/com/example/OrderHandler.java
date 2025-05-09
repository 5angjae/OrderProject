package com.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.util.*;

public class OrderHandler extends HttpServlet {
    private final Gson gson = new Gson();
    private final List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
    private final OrderBackupService backupService;

    public OrderHandler(OrderBackupService backupService) {
        this.backupService = backupService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder body = new StringBuilder();
        String line;
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }

        List<Order> received = gson.fromJson(body.toString(), new TypeToken<List<Order>>(){}.getType());
        orders.addAll(received);
        for (Order o : received) {
            backupService.enqueue(o);
        }

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("orderCount", orders.size());

        // 제품별 통계
        Map<String, Map<String, Object>> stats = new TreeMap<String, Map<String, Object>>();
        Map<String, List<Order>> grouped = new HashMap<String, List<Order>>();

        for (Order o : orders) {
            if (!grouped.containsKey(o.product)) {
                grouped.put(o.product, new ArrayList<Order>());
            }
            grouped.get(o.product).add(o);
        }

        for (Map.Entry<String, List<Order>> entry : grouped.entrySet()) {
            String product = entry.getKey();
            List<Order> list = entry.getValue();

            int totalQty = 0;
            double totalPrice = 0;
            for (Order o : list) {
                totalQty += o.quantity;
                totalPrice += o.price;
            }

            double avg = totalPrice / list.size();
            Map<String, Object> val = new HashMap<String, Object>();
            val.put("quantity", totalQty);
            val.put("avgPrice", Math.round(avg * 100.0) / 100.0);
            stats.put(product, val);
        }

        result.put("products", stats);

        resp.setContentType("application/json");
        resp.getWriter().println(gson.toJson(result));
    }
}