package com.tinyappsdev.tinypos.ui.BaseUI;


import android.database.DataSetObserver;

import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.data.TicketFoodAttr;

import java.util.List;
import java.util.Map;

public interface OrderActivityInterface extends ActivityInterface {
    Ticket getTicket();
    Map<Long, Integer> getFoodMap();
    void openFoodDetailWnd(long foodId, int index);
    void closeFoodDetailWnd();
    void addFood(TicketFood ticketFood);
    void changeFood(int index, List<TicketFoodAttr> ticketFoodAttr, int quantity, double price);
    void removeFood(int index);
    void clearAllFood();
    void setNumGuest(int number);
    void openCustomerPicker();
    void setNotes(String notes);
    void setDineTable(long id, String name);
    void deleteTicket();
    void openPaymentWnd();
    void closePaymentWnd();
    void setPayment(int index, int type, double amount);
    void removePayment(int index);
}
