package com.tinyappsdev.tinypos.ui.BaseUI;


public interface LoginActivityInterface extends ActivityInterface {
    void loginServer(String address, String password);
    void loginCustomer(int code);
}
