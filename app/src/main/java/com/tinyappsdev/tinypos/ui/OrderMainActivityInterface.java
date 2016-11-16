package com.tinyappsdev.tinypos.ui;


import android.database.DataSetObserver;

import com.tinyappsdev.tinypos.data.Ticket;

import java.util.Map;

public interface OrderMainActivityInterface {
    int getCurrentFragmentId();
    void registerObserverForFragmentChange(DataSetObserver observer);
    void unregisterObserverForFragmentChange(DataSetObserver observer);
}
