package com.tinyappsdev.tinypos.ui.BaseUI;

import java.util.Map;

/**
 * Created by pk on 11/11/2016.
 */

public interface KitchenActivityInterface extends ActivityInterface {
    void fulfillFood(Map<Long, Map<Integer, Integer>> items);
}
