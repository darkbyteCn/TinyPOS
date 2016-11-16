package com.tinyappsdev.tinypos.ui.BaseUI;


import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.data.TicketFoodAttr;

import java.util.List;
import java.util.Map;

public interface TicketActivityInterface extends ActivityInterface {
    Ticket getTicket();
    void selectTicket(long ticketId);
    String getSearchQuery();
}
