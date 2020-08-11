package com.nth.jasperreport.controller;

import com.nth.jasperreport.report.ReportType;
import com.nth.jasperreport.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hong Nguyen
 */
@Controller
@RequestMapping("/report")
public class ReportController {
    @Autowired
    ReportService reportService;

    @GetMapping("/{type}")
    public String printReport(Model model, @PathVariable String type) throws Exception {
        try {
            ReportType enumType = ReportType.valueOf(type.toUpperCase());
            model.addAllAttributes(reportService.printReport(enumType));
            return "ReportViewer";
        } catch (IllegalArgumentException ex) {
            return "";
        }
    }
}
