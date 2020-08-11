package com.nth.jasperreport.service;

import com.nth.jasperreport.report.ReportConstants;
import com.nth.jasperreport.report.ReportType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author Hong Nguyen
 */
@Service
public class ReportService {
    private Map param = new HashMap();
    private List datas = new ArrayList();

    ReportService() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.covid19api.com/summary";
        Map response = restTemplate.getForObject(url, Map.class);
        Map tmpparam = (Map) response.get("Global");
        param.put("Confirmed", tmpparam.get("TotalConfirmed"));
        param.put("Recovered", tmpparam.get("TotalRecovered"));
        param.put("Deaths", tmpparam.get("TotalDeaths"));

        datas = (List) response.get("Countries");
    }

    public Map<String,?> printReport(ReportType type) {
        Map result = new HashMap();
        result.put(ReportConstants.REPORT_TYPE, type);
        result.put(ReportConstants.REPORT_NAME, "coronasumary.jasper");
        result.put(ReportConstants.REPORT_PARAMETERS, param);
        result.put(ReportConstants.REPORT_DATA, datas);
        return result;
    }
}
