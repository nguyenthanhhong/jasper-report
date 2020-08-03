package com.nth.jasperreport.controller;

import com.nth.jasperreport.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/report")
public class ReportController {
    @Autowired
    ReportService reportService;

    @GetMapping("/html")
    public String printHtml(Model model, @org.springframework.web.bind.annotation.RequestParam String param) throws Exception {
        model.addAllAttributes(reportService.printHtml());
        return "ReportViewer";
    }
    @GetMapping("/xml")
    public String printXml(Model model, @org.springframework.web.bind.annotation.RequestParam String param) throws Exception {
        model.addAllAttributes(reportService.printXml());
        return "ReportViewer";
    }
    @GetMapping("/pdf")
    public String printPdf(Model model, @org.springframework.web.bind.annotation.RequestParam String param) throws Exception {
        model.addAllAttributes(reportService.printPdf());
        return "ReportViewer";
    }
}
