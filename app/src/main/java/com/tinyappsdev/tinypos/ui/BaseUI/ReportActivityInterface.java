package com.tinyappsdev.tinypos.ui.BaseUI;


import com.tinyappsdev.tinypos.helper.TinyMap;

public interface ReportActivityInterface extends ActivityInterface {
    void setReportDate(String date);
    TinyMap getReportData();
}
