package com.example;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class GoogleExcelUtil {
	
	private static final Log LOG = LogFactory.getLog(GoogleExcelUtil.class);
	
	public static String getExcelData(String spreadsheetId, String sheetName, String apiKey) {
		// https://developers.google.com/sheets/api/samples/reading
		String gSheetUrl = "https://sheets.googleapis.com/v4/spreadsheets/"+spreadsheetId+"/values/"+sheetName+"?key="+apiKey; //June%202020
		
		String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {

            HttpGet request = new HttpGet(gSheetUrl);

            CloseableHttpResponse response = httpClient.execute(request);

            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity); 
                }

            } catch (IOException e) {
            	LOG.error(e.getMessage());
			} finally {
                try {
					response.close();
				} catch (IOException e) {
					LOG.error(e.getMessage());
				}
            }
        } catch(IOException e) {
        	LOG.error(e.getMessage());
        } finally {
            try {
				httpClient.close();
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
        }
        
        return result;
	}

}
