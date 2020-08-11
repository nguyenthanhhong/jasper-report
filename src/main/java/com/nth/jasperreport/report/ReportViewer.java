package com.nth.jasperreport.report;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
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

/**
 * @author Hong Nguyen
 */
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
        if (report_name.endsWith(".jasper")) {
            report = (JasperReport) JRLoader.loadObject(stream);
        } else {
            report = JasperCompileManager.compileReport(stream);
        }

        JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);
        final OutputStream outStream = report_type == ReportType.HTML ? null : response.getOutputStream();
        Exporter exporter;

        switch (report_type) {
            case PRINT:
                JasperPrintManager.printReport(jasperPrint, false);
                break;
            case XLS:
                response.setHeader("Content-Disposition", "attachment;filename=\"" + jasperPrint.getName() + ".xls\"");
                exporter = new JRXlsExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));

                SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
                configuration.setOnePagePerSheet(true);
                configuration.setDetectCellType(true);
                configuration.setCollapseRowSpan(false);
                exporter.setConfiguration(configuration);

                exporter.exportReport();
                break;
            case XLSX:
                response.setHeader("Content-Disposition", "attachment;filename=\"" + jasperPrint.getName() + ".xlsx\"");
                exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));

                SimpleXlsxReportConfiguration xlsxConfiguration = new SimpleXlsxReportConfiguration();
                xlsxConfiguration.setOnePagePerSheet(true);
                xlsxConfiguration.setDetectCellType(true);
                xlsxConfiguration.setCollapseRowSpan(false);
                exporter.setConfiguration(xlsxConfiguration);

                exporter.exportReport();
                break;
            case DOC:
                response.setHeader("Content-Disposition", "attachment;filename=\"" + jasperPrint.getName() + ".doc\"");
                exporter = new JRRtfExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleWriterExporterOutput(outStream));
                exporter.setConfiguration(new SimpleRtfExporterConfiguration());
                exporter.exportReport();
                break;
            case DOCX:
                response.setHeader("Content-Disposition", "attachment;filename=\"" + jasperPrint.getName() + ".docx\"");
                exporter = new JRDocxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));

                exporter.exportReport();
                break;
            case CSV:
                exporter = new JRCsvExporter();

                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleWriterExporterOutput(outStream));

                exporter.exportReport();
                break;
            case HTML:
                response.setContentType("text/html");
                exporter = new HtmlExporter(DefaultJasperReportsContext.getInstance());
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleHtmlExporterOutput(response.getWriter()));
                exporter.exportReport();
                break;
            case XML:
                JasperExportManager.exportReportToXmlStream(jasperPrint, outStream);
                outStream.flush();
                outStream.close();
                break;
            default:
                response.setContentType("application/pdf");
                JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
                outStream.flush();
                outStream.close();
                break;
        }
    }
}
