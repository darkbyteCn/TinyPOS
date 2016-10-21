package com.tinyappsdev.tinypos.ui;


import android.database.DataSetObserver;

import com.tinyappsdev.tinypos.data.Ticket;

import java.util.Map;

public interface OrderActivityInterface {
    Ticket getTicket();
    Map<Long, Integer> getFoodMap();
    void registerObserverForFood(DataSetObserver observer);
    void unregisterObserverForFood(DataSetObserver observer);
    void notifyChangedForFood();
    void registerObserverForTicket(DataSetObserver observer);
    void unregisterObserverForTicket(DataSetObserver observer);
    void notifyChangedForTicket();
    void openFoodDetailWnd(long foodId, int index);
}
