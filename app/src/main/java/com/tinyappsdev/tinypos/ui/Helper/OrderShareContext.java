package com.tinyappsdev.tinypos.ui.Helper;

import android.database.DataSetObservable;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyappsdev.tinypos.data.DineTable;
import com.tinyappsdev.tinypos.data.Food;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketFood;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrderShareContext {
    public DataSetObservable ticketLoadedObservable = new DataSetObservable();
    public DataSetObservable foodObservable = new DataSetObservable();
    public Map<Long, Integer> foodCountById = new HashMap<Long, Integer>();
    public Ticket ticket;
    public DineTable dineTable;

    public static OrderShareContext fromJson(String json) {
        OrderShareContext shareCtx = new OrderShareContext();
        try {
            shareCtx.ticket = (new ObjectMapper()).readValue(json, Ticket.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<TicketFood> foodItems = shareCtx.ticket.getFoodItems();
        for(TicketFood foodItem: foodItems) {
            long id = foodItem.getId();
            int qty = foodItem.getQuantity();
            Integer curQty = shareCtx.foodCountById.get(id);
            shareCtx.foodCountById.put(id, curQty == null ? qty : curQty + qty);
        }

        return shareCtx;
    }

    public String toJson() {
        String js = null;
        try {
            js = (new ObjectMapper()).writeValueAsString(this.ticket);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Log.i("PKT", ">>>>>" + js);
        return js;
    }

}
