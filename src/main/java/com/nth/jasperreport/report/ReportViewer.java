package com.nth.jasperreport.report;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("ReportViewer")
public class ReportViewer extends AbstractView {
    @Value("${REPORT_PATH}")
    String reportPath;

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        String report_name = String.valueOf(model.get(ReportConstants.REPORT_NAME));
        ReportType report_type = (ReportType) model.get(ReportConstants.REPORT_TYPE);
        List<Map> data = (List<Map>) model.get(ReportConstants.REPORT_DATA);
        Map params = (HashMap) model.get(ReportConstants.REPORT_PARAMETERS);

        JRDataSource dataSource = new JRBeanCollectionDataSource(data);

        File file = ResourceUtils.getFile("classpath:" + reportPath + report_name);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found");
        }

        InputStream stream = new FileInputStream(file);
        JasperReport report;
        String exportName = "";
        if (report_name.endsWith(".jasper")) {
            report = (JasperReport) JRLoader.loadObject(stream);
            exportName = report_name.replaceAll(".jasper","");
        } else {
            report = JasperCompileManager.compileReport(stream);
            exportName = report_name.replaceAll(".jrxml","");
        }

        JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);



        switch (report_type) {
            case HTML:
                response.setContentType("text/html");
                HtmlExporter exporter = new HtmlExporter(DefaultJasperReportsContext.getInstance());
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleHtmlExporterOutput(response.getWriter()));
                exporter.exportReport();
                break;
            case XLS:
                response.setHeader("Content-Disposition", "attachment;filename=\"" + new String(exportName.getBytes("utf-8"),"ISO-8859-1") + ".xls\"");
                OutputStream xlsStream = response.getOutputStream();
                JRXlsExporter xlsExporter = new JRXlsExporter();
                xlsExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsStream));

                SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
                configuration.setOnePagePerSheet(true);
                configuration.setDetectCellType(true);
                configuration.setCollapseRowSpan(false);
                xlsExporter.setConfiguration(configuration);

                xlsExporter.exportReport();
                break;
            case XLSX:
                response.setHeader("Content-Disposition", "attachment;filename=\"" + new String(exportName.getBytes("utf-8"),"ISO-8859-1") + ".xlsx\"");
                OutputStream xlsxStream = response.getOutputStream();
                JRXlsxExporter xlsxExporter = new JRXlsxExporter();
                xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsxStream));

                SimpleXlsxReportConfiguration xlsxConfiguration = new SimpleXlsxReportConfiguration();
                xlsxConfiguration.setOnePagePerSheet(true);
                xlsxConfiguration.setDetectCellType(true);
                xlsxConfiguration.setCollapseRowSpan(false);
                xlsxExporter.setConfiguration(xlsxConfiguration);

                xlsxExporter.exportReport();
                break;
            case CSV:
                OutputStream csvStream = response.getOutputStream();
                JRCsvExporter csvExporter = new JRCsvExporter();

                csvExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                csvExporter.setExporterOutput(new SimpleWriterExporterOutput(csvStream));

                csvExporter.exportReport();
                break;
            case PRINT:
                JasperPrintManager.printReport(jasperPrint, false);
                break;
            case XML:
                OutputStream xmlStream = response.getOutputStream();
                JasperExportManager.exportReportToXmlStream(jasperPrint, xmlStream);
                xmlStream.flush();
                xmlStream.close();
                break;
            default:
                response.setContentType("application/pdf");
                final OutputStream outStream = response.getOutputStream();

                JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
                outStream.flush();
                outStream.close();
                break;
        }
    }
}
