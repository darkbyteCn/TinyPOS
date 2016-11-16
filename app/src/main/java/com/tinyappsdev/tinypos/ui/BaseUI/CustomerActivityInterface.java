package com.tinyappsdev.tinypos.ui.BaseUI;

import com.tinyappsdev.tinypos.data.Customer;
import com.tinyappsdev.tinypos.ui.BaseUI.ActivityInterface;

public interface CustomerActivityInterface extends ActivityInterface {
    String getSearchQuery();
    Customer getCustomer();
    void selectCustomer(long customerId);
    void saveCustomer(Customer customer);
    void setResult(Customer customer);
    boolean isResultNeeded();
}
