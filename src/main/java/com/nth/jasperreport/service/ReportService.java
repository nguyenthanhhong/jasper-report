package com.nth.jasperreport.service;

import com.nth.jasperreport.report.ReportConstants;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {
    public Map<String,?> printHtml() {
        Map result = new HashMap();
        result.put(ReportConstants.REPORT_TYPE, ReportConstants.PDF);
        result.put(ReportConstants.REPORT_NAME, "LocationLabel.jasper");
        result.put(ReportConstants.REPORT_DATA, getReportData());
        return result;
    }

    public Map<String,?> printXml() {
        Map result = new HashMap();
        result.put(ReportConstants.REPORT_TYPE, ReportConstants.PDF);
        result.put(ReportConstants.REPORT_NAME, "LocationLabel.jasper");
        result.put(ReportConstants.REPORT_DATA, getReportData());
        return result;
    }

    public Map<String,?> printPdf() {
        Map result = new HashMap();
        result.put(ReportConstants.REPORT_TYPE, ReportConstants.PDF);
        result.put(ReportConstants.REPORT_NAME, "LocationLabel.jasper");
        result.put(ReportConstants.REPORT_DATA, getReportData());
        return result;
    }

    private Object getReportData() {
        return null;
    }
}
