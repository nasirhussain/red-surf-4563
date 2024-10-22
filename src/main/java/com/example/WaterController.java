package com.example;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SecurityCharges.MonthlyChargesDetail;

@RestController
public class WaterController {

	private static final String SPREADSHEET_ID = "1hswm7ybOWx7hsbYjjAQdxcX4gtUHUoYx7YVaEeqL5TQ";
	private static final String SPREADSHEET_ID_HORTICULTURE = "1hswm7ybOWx7hsbYjjAQdxcX4gtUHUoYx7YVaEeqL5TQ";
	private static final String API_KEY = "AIzaSyC042LdhI9fuj02qhJSk3TJks90R3MlM6E";
	private static final DateTimeFormatter DATE_FORMATTER_UG = DateTimeFormatter.ofPattern("dd-MMM-yy");
	private static final String SHEET_SELECTION_UG = "June2020!A2:ES60";
	private static final String SHEET_SELECTION_HORTICULTURE = "Sheet1!A2:F125";
	private static final DateTimeFormatter DATE_FORMATTER_OVERHEAD = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static final String SHEET_SELECTION_OVERHEAD = "June2020!A81:M100";
	private static final String SHEET_SELECTION_SECURITY = "Security2020!A2:K65";
	private static final List<String> UNPAID_PLOTS_UG = Arrays.asList("1932", "1697");
	private static final List<String> UNPAID_PLOTS_OVERHEAD = new ArrayList<String>();
	private static final DateFormat MONTH_DF = new SimpleDateFormat("MMMMM yyyy");
	//private static final long MEGABYTE = 1024L * 1024L;
	private static final Log LOG = LogFactory.getLog(WaterController.class);
	
	/*private static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }*/
	
	@GetMapping("/water-overhead")
	public String waterOverhead(@RequestParam(value = "dateInput", defaultValue = "0") String dateInput) {
		LocalDate date =  LocalDate.now();
		LOG.info("java " + System.getProperty("java.version"));
		if(dateInput.length() >= 6) {
			date = getDate(dateInput, DATE_FORMATTER_OVERHEAD);
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
		StringBuilder builder = new StringBuilder();
		startHTML(builder);
		builder.append("<b>"+formatter.format(date)+" : Overhead Water Supply</b>");
		
		String result = GoogleExcelUtil.getExcelData(SPREADSHEET_ID, SHEET_SELECTION_OVERHEAD, API_KEY);
		List<String> villaList = createOverheadList(result, date.getMonth());
		for (String villaNo : villaList) {
			if(!UNPAID_PLOTS_OVERHEAD.contains(villaNo)) {
				builder.append("<br />");
				builder.append(villaNo);
			}
		}
		
		endHTML(builder);
		return builder.toString();
	}
	
	@PostMapping("/horticulture-add")
	public String horticulture(@RequestParam(value = "dateInput", defaultValue = "0") String dateInput) {
		LocalDate date =  LocalDate.now();
		if(dateInput.length() >= 6) {
			date = getDate(dateInput, DATE_FORMATTER_OVERHEAD);
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
		StringBuilder builder = new StringBuilder();
		startHTML(builder);
		// TODO add / update data in excel sheet SPREADSHEET_ID_HORTICULTURE
		builder.append("<b>"+formatter.format(date)+" : Horticulture staff record added.</b>");
		
		endHTML(builder);
		return builder.toString();
	}
	
	@GetMapping("/security")
	public String security() {
		LocalDate date =  LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
		StringBuilder builder = new StringBuilder();
		startHTML(builder);
		builder.append("<b>"+formatter.format(date)+" : Security contribution</b><br />");

		String result = GoogleExcelUtil.getExcelData(SPREADSHEET_ID, SHEET_SELECTION_SECURITY, API_KEY);
		//LOG.info("result " + result);
		List<SecurityCharges> villaList = createSecurityList(result, date.getMonth());
		for (SecurityCharges villa : villaList) {
			builder.append("<br />");
			if(villa.getMonthlyCharges().get(0).getStatus().equals("pending")) {
				builder.append("<span style=\"color:red\">");
			}
			builder.append(villa.getVillaNo() + " : " + villa.getName() + " : Rs " + villa.getMonthlyCharges().get(0).getAmount() + " : " + villa.getMonthlyCharges().get(0).getStatus());
			if(villa.getMonthlyCharges().get(0).getStatus().equals("pending")) {
				builder.append("</span>");
			}
		}

		endHTML(builder);
		return builder.toString();
	}
	
	@GetMapping("/security-due")
	public String securityDue() {
		LocalDate date =  LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
		StringBuilder builder = new StringBuilder();
		startHTML(builder);
		builder.append("<b>"+formatter.format(date)+" : Security contribution dues</b><br />");

		String result = GoogleExcelUtil.getExcelData(SPREADSHEET_ID, SHEET_SELECTION_SECURITY, API_KEY);
		//LOG.info("result " + result);
		List<SecurityCharges> villaList = createSecurityList(result, date.getMonth());
		for (SecurityCharges villa : villaList) {
			if(villa.getMonthlyCharges().get(0).getStatus().equals("pending")) {
				builder.append("<br />");
				builder.append("<span style=\"color:red\">");
				builder.append(villa.getVillaNo() + " : " + villa.getName() + " : Rs " + villa.getMonthlyCharges().get(0).getAmount() + " : " + villa.getMonthlyCharges().get(0).getStatus());
				builder.append("</span>");
			}
			
		}

		endHTML(builder);
		return builder.toString();
	}

	@GetMapping("/water")
	public String water(@RequestParam(value = "dateInput", defaultValue = "next") String dateInput) {
		LocalDate date =  LocalDate.now().plusDays(1);
		LocalDate tomorrow =  LocalDate.now().plusDays(1);
		boolean todayRecord = false;
		boolean overdueRecords = false;
		boolean tomorrowRecord = false;
		if(dateInput.length() >= 8) {
			date = getDate(dateInput, DATE_FORMATTER_UG);
			LocalDate start =  LocalDate.now().plusDays(1);
			LocalDate end =  LocalDate.now().plusDays(4);
			if(!(date.isAfter(start) && date.isBefore(end))) {
				date =  LocalDate.now().plusDays(1);
			}
		} else if(dateInput.equals("1")) {
			todayRecord = true;
			date = LocalDate.now();
		}
		// check time for 11:00 PM IST today and show overdue records in tomorrow listing
		LocalDateTime now = LocalDateTime.now();
		LocalTime startTime = LocalTime.of(17,30);
		LocalTime endTime = LocalTime.of(23,59,59);
		LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), startTime);
		LocalDateTime endDateTime = LocalDateTime.of(LocalDate.now(), endTime);
		if((dateInput == "" || date.equals(tomorrow)) && now.isAfter(startDateTime) && now.isBefore(endDateTime)) {
			tomorrowRecord = true;
		}
		// 20 June : UG Water Supply
		StringBuilder builder = new StringBuilder();
		StringBuilder overdueBuilder = new StringBuilder();
		startHTML(builder);
		builder.append("<b>"+DATE_FORMATTER_UG.format(date)+" : UG Water Supply</b>");

		// return it as a String
		String result = GoogleExcelUtil.getExcelData(SPREADSHEET_ID, SHEET_SELECTION_UG, API_KEY);
		List<VillaWaterDates> waterSupplyDateList = createList(result);
		for (VillaWaterDates villaWaterDates : waterSupplyDateList) {
			if(date.equals(villaWaterDates.getNextDate()) && !UNPAID_PLOTS_UG.contains(villaWaterDates.getPlotOrVillaNo())) {
				builder.append("<br />");
				builder.append(villaWaterDates.getPlotOrVillaNo());
			}
			// create overdue records
			boolean isTomorrowOverdue = (tomorrowRecord && villaWaterDates.getLastSupplyDate() == null && LocalDate.now().equals(villaWaterDates.getNextDate()))
					|| (villaWaterDates.getNextDate() != null && LocalDate.now().isAfter(villaWaterDates.getNextDate()));
			boolean isTodayOverdue = todayRecord && villaWaterDates.getNextDate() != null && date.isAfter(villaWaterDates.getNextDate());

			if((isTodayOverdue || isTomorrowOverdue) && !UNPAID_PLOTS_UG.contains(villaWaterDates.getPlotOrVillaNo())) {
				overdueBuilder.append("<br />");
				LocalDate lastSupplyDate = getLastSupplyDate(villaWaterDates, villaWaterDates.getPlotOrVillaNo());
				if(lastSupplyDate != null) {
					overdueRecords = true;
					// <span style="color:red">No overdue records found!</span>
					overdueBuilder.append("<span style=\"color:red\">"+villaWaterDates.getPlotOrVillaNo() + " : last supply on " + DATE_FORMATTER_UG.format(lastSupplyDate)+"</span>");
				}
			} 
		}

		if(overdueRecords) {
			builder.append(overdueBuilder.toString());
		}
		endHTML(builder);
		return builder.toString();
	}
	
	@GetMapping("/water-report")
	public String waterReport(@RequestParam(value = "dateInput", defaultValue = "next") String dateInput) {
		LocalDate date = LocalDate.now().minusDays(1);
		boolean isFutureDate = false;
		if(dateInput.length() >= 8) {
			date = getDate(dateInput, DATE_FORMATTER_UG);
			if(!date.isBefore(LocalDate.now())) {
				isFutureDate = true;
			}
		} 
		// 20 June : UG Water Supply
		StringBuilder builder = new StringBuilder();
		startHTML(builder);
		builder.append("<b>"+DATE_FORMATTER_UG.format(date)+" : UG Water Supply Report</b><span style='display:none'>"+LocalDateTime.now()+"</span>");
		boolean records = false;

		String result = GoogleExcelUtil.getExcelData(SPREADSHEET_ID, SHEET_SELECTION_UG, API_KEY);
		List<VillaWaterDates> waterSupplyDateList = createExelDateList(result);
		for (VillaWaterDates villaWaterDates : waterSupplyDateList) {
			for (LocalDate localDate : villaWaterDates.getWaterSupplyDateList()) {
				if(date.equals(localDate)) {
					builder.append("<br />");
					builder.append(villaWaterDates.getPlotOrVillaNo());
					records = true;
				}
			}
		}

		if(!records) {
			builder.append("<br />");
			builder.append("No record found!");
		}
		endHTML(builder);
		return builder.toString();
	}
	
	@GetMapping("/overdue")
	public String overdue() {
		LocalDate date =  LocalDate.now();

		StringBuilder builder = new StringBuilder();
		startHTML(builder);
		builder.append("<b>"+DATE_FORMATTER_UG.format(date)+" : UG Water Supply Overdue</b>");
		boolean overdueRecords = false;

		String result = GoogleExcelUtil.getExcelData(SPREADSHEET_ID, SHEET_SELECTION_UG, API_KEY);
		List<VillaWaterDates> waterSupplyDateList = createList(result);
		for (VillaWaterDates villaWaterDates : waterSupplyDateList) {
			if(villaWaterDates.getNextDate() != null && date.isAfter(villaWaterDates.getNextDate()) && !UNPAID_PLOTS_UG.contains(villaWaterDates.getPlotOrVillaNo())) {
				builder.append("<br />");
				LocalDate lastSupplyDate = getLastSupplyDate(villaWaterDates, villaWaterDates.getPlotOrVillaNo());
				if(lastSupplyDate != null) {
					overdueRecords = true;
					builder.append(villaWaterDates.getPlotOrVillaNo() + " : last supply on " + DATE_FORMATTER_UG.format(lastSupplyDate));
				}
			}
		}

		if(!overdueRecords) {
			builder.append("<br />");
			builder.append("No overdue records found!");
		}
		endHTML(builder);
		return builder.toString();
	}
	
	@GetMapping("/dates")
	public String dates(@RequestParam(value = "plot", defaultValue = "1440") String plot) {

		// 1440 : UG Water Supply
		StringBuilder builder = new StringBuilder();
		startHTML(builder);
		StringBuilder builder2 = new StringBuilder();
		List<LocalDate> supplyDateList = new ArrayList<>();
		boolean recordFound = false;

		String result = GoogleExcelUtil.getExcelData(SPREADSHEET_ID, SHEET_SELECTION_UG, API_KEY);
		List<VillaWaterDates> waterSupplyDateList = createList(result);
		for (VillaWaterDates villaWaterDates : waterSupplyDateList) {
			if(plot.equals(villaWaterDates.getPlotOrVillaNo())) {
				for (LocalDate supplyDate : villaWaterDates.getWaterSupplyDateList()) {
					recordFound = true;
					supplyDateList.add(supplyDate);
				}
				if(UNPAID_PLOTS_UG.contains(villaWaterDates.getPlotOrVillaNo())) {
					builder2.append("<b>"+StringEscapeUtils.escapeHtml4(plot)+" : Water Supply Blocked</b><br />");
					builder2.append("<br /><br />");
				} else {
					builder2.append("<b>"+StringEscapeUtils.escapeHtml4(plot)+" : Next Water Refill</b><br />");
					builder2.append(DATE_FORMATTER_UG.format(villaWaterDates.getNextDate()));
					builder2.append("<br /><br />");
				}
			}
		}

		if(!recordFound) {
			builder.append("<br />");
			builder.append("<b>No record found!</b>");
		} else {
			builder.append(builder2.toString());
			builder.append("<b>"+StringEscapeUtils.escapeHtml4(plot)+" : UG Water Supply Report</b>");
			Collections.reverse(supplyDateList);
			for (LocalDate supplyDate : supplyDateList) {
				builder.append("<br />");
				builder.append(DATE_FORMATTER_UG.format(supplyDate));
			}

		}
		endHTML(builder);
		return builder.toString();

	}
	
	private List<VillaWaterDates> createList(String result) {
		 JSONObject obj = new JSONObject(result);
         JSONArray array = obj.getJSONArray("values");
         List<VillaWaterDates> waterSupplyDateList = new ArrayList<>();
         for(int i = 0; i < array.length(); i++) {
        	 VillaWaterDates villaWaterDates = new VillaWaterDates();
        	 List<LocalDate> dateList = new ArrayList<>();
        	 JSONArray arr2 = (JSONArray)array.get(i);
        	 for(int j = 0; j < arr2.length(); j++) {
        		 if(j == 0) {
        			 villaWaterDates.setPlotOrVillaNo(arr2.getString(j));
        		 } else {
        			 if(arr2.getString(j).trim().length() > 0) {
        				 LocalDate localDate = getDate(arr2.getString(j), DATE_FORMATTER_UG);
        				 dateList.add(localDate);
        			 }
        		 }
        		 if(j > 0 && j == arr2.length() - 1) {
        			 LocalDate excelDate = getDate(arr2.getString(j), DATE_FORMATTER_UG);
        			 LocalDate today = LocalDate.now();
        			 LocalDate tomorrow = LocalDate.now().plusDays(1);
        			 LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);
        			 // if today's, tomorrow's or next day entry is found in excel sheet
        			 if(excelDate.equals(today) || excelDate.equals(tomorrow) || excelDate.equals(dayAfterTomorrow)) {
        				 villaWaterDates.setNextDate(excelDate);
        				 if(excelDate.equals(today)) {
        					 villaWaterDates.setLastSupplyDate(excelDate);
        				 }
        			 } else {
        				 if(villaWaterDates.getPlotOrVillaNo().equals("1425")) {
        					 // need water every Sunday
        					 LocalDate date = excelDate.plusDays(2);
        					 while(date.getDayOfWeek() != DayOfWeek.SUNDAY) {
        						 date = date.plusDays(1);
        					 }
        					 villaWaterDates.setNextDate(date);
        				 } else if(villaWaterDates.getPlotOrVillaNo().equals("1440") || villaWaterDates.getPlotOrVillaNo().equals("1395") || villaWaterDates.getPlotOrVillaNo().equals("2455") || villaWaterDates.getPlotOrVillaNo().equals("1385A") || villaWaterDates.getPlotOrVillaNo().equals("2512")) {
        					// need water every 5th day
        					 villaWaterDates.setNextDate(excelDate.plusDays(4));
        				 } else if(villaWaterDates.getPlotOrVillaNo().equals("2503")) {
        					 villaWaterDates.setNextDate(excelDate.plusDays(3));
        				 } else {
        					 villaWaterDates.setNextDate(excelDate.plusDays(5));
        				 }
        			 }
        		 }
        	 }
        	 villaWaterDates.setWaterSupplyDateList(dateList);
        	 waterSupplyDateList.add(villaWaterDates);
         }
         
		return waterSupplyDateList;
	}
	
	private List<SecurityCharges> createSecurityList(String result, Month month) {
		JSONObject obj = new JSONObject(result);
		JSONArray array = obj.getJSONArray("values");
		List<SecurityCharges> securityChargesList = new ArrayList<>();
		SecurityCharges securityCharges = null;
		Date today = new Date();
		List<MonthlyChargesDetail> monthlyChargesList = null;
		outer:
		for(int i = 0; i < array.length(); i++) {
			JSONArray arr2 = (JSONArray)array.get(i);
			MonthlyChargesDetail monthlyCharges = null;
			for(int j = 0; j < arr2.length(); j++) {
				String val = arr2.getString(j);
				//LOG.info(i + " : " + j + " : " + val);
				if(j == 0 && StringUtils.isNotBlank(val)) {
					securityCharges = new SecurityCharges();
					monthlyChargesList = new ArrayList<>();
					securityCharges.setVillaNo(val);
				} else if(j == 1 && StringUtils.isNotBlank(val)) { 
					securityCharges.setName(val);
				} else if(j == 2 && StringUtils.isNotBlank(val)) { 
					securityCharges.setMobile(val);
				} else if(j == 3) {
					if(MONTH_DF.format(today).equalsIgnoreCase(val)) {
						monthlyCharges = securityCharges.createMonthlyCharges();
						monthlyCharges.setMonth(val);
					} else {
						continue outer;
					}
				} else if(j == 4) { 
					monthlyCharges.setAmount(Integer.parseInt(val));
				} else if(j == 5) { 
					monthlyCharges.setStatus(val);
				} else if(j == 6) { 
					monthlyCharges.setDueDate(getDate(val, DATE_FORMATTER_UG));
				} else if(j == 7) { 
					monthlyCharges.setPaymentDate(getDate(val, DATE_FORMATTER_UG));
				} else if(j == 8) { 
					monthlyCharges.setPaymentMode(val);
				} else if(j == 9) { 
					monthlyCharges.setReceiptNo(val);
				} else if(j == 10) { 
					monthlyCharges.setTransactionId(val);
				} 
				
				if(j == arr2.length()-1) {
					monthlyChargesList.add(monthlyCharges);
					securityCharges.setMonthlyCharges(monthlyChargesList);
					if(securityChargesList.contains(securityCharges)) {
						securityChargesList.remove(securityCharges);
					}
					//LOG.info(securityChargesList.size());
					securityChargesList.add(securityCharges);
				}
			}
		}

		return securityChargesList;
	}
	
	private List<String> createOverheadList(String result, Month month) {
		JSONObject obj = new JSONObject(result);
		JSONArray array = obj.getJSONArray("values");
		List<String> villaList = new ArrayList<>();
		for(int i = 0; i < array.length(); i++) {
			JSONArray arr2 = (JSONArray)array.get(i);
			String villaNo = "";
			for(int j = 0; j < arr2.length(); j++) {
				if(j == 0) {
					villaNo = arr2.getString(j);
				} else {
					if(arr2.getString(j).trim().length() > 0) {
						LocalDate localDate = getDate("01-"+arr2.getString(j), DATE_FORMATTER_OVERHEAD);
						if(localDate.getMonth() == month) {
							villaList.add(villaNo);
							break;
						}
					}
				}
			}
		}

		return villaList;
	}
	
	private List<VillaWaterDates> createExelDateList(String result) {
		 JSONObject obj = new JSONObject(result);
        JSONArray array = obj.getJSONArray("values");
        List<VillaWaterDates> waterSupplyDateList = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
       	 VillaWaterDates villaWaterDates = new VillaWaterDates();
       	 List<LocalDate> dateList = new ArrayList<>();
       	 JSONArray arr2 = (JSONArray)array.get(i);
       	 for(int j = 0; j < arr2.length(); j++) {
       		 if(j == 0) {
       			 villaWaterDates.setPlotOrVillaNo(arr2.getString(j));
       		 } else {
       			 if(arr2.getString(j).trim().length() > 0) {
       				 LocalDate localDate = getDate(arr2.getString(j), DATE_FORMATTER_UG);
       				 dateList.add(localDate);
       			 }
       		 }
       	 }
       	 villaWaterDates.setWaterSupplyDateList(dateList);
       	 waterSupplyDateList.add(villaWaterDates);
        }
        
		return waterSupplyDateList;
	}
	
	private LocalDate getLastSupplyDate(VillaWaterDates villaWaterDates, String plot) {
		LocalDate lastSupplyDate = null;
		if(plot.equals(villaWaterDates.getPlotOrVillaNo())) {
    		for (LocalDate supplyDate : villaWaterDates.getWaterSupplyDateList()) {
    			lastSupplyDate = supplyDate;
			}
    	}
		return lastSupplyDate;
	}
	
	private LocalDate getDate(String date, DateTimeFormatter formatter) {
		if(date.indexOf("-") == 1) {
			date = "0"+date;
		}
		LocalDate localDate = LocalDate.parse(date, formatter);
		return localDate;
	}
	
	private void startHTML(StringBuilder builder) {
		builder.append("<html><head>");
        builder.append("<title>OCRWS PHASE II</title>");
        builder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        builder.append("</head><body>");
	}
	
	private void endHTML(StringBuilder builder) {
		builder.append("</body></html>");
	}
}