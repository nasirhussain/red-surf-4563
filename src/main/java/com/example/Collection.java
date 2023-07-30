package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Collection {
	
	private static final String SPREADSHEET_ID_COLLECTION = "1k7cblwNdfg76lRUkppJr8gau4S0_atNC-7NIMO9MFkg";
	private static final String API_KEY = "AIzaSyBjk92K05nqxNmGpH0pQ3_fL5Gnaa1SNbA";
	private static final String SHEET_SELECTION = "2022!A3:B80";
	private static final String SHEET_ACCESS_SERVICE_KEY = "89275c374a8313244c9f8bbfdecb5be5ae4eebdf";
	
	private static final String SPREADSHEET_ID_UG = "1hswm7ybOWx7hsbYjjAQdxcX4gtUHUoYx7YVaEeqL5TQ";
	private static final String SHEET_SELECTION_UG = "June2020!BC22:BH60";

	public static void main(String[] args) {
		String fileName = "/Users/nasirhussain/valamis/ListOmaxeP2.txt";
		String plotslist = "/Users/nasirhussain/valamis/plots.txt";
		String paidlist = "/Users/nasirhussain/valamis/paid.txt";
		List<String> plotList = new ArrayList<>();
		List<String> paidList = new ArrayList<>();
		List<String> processedPlotList = new ArrayList<>();
		List<Integer> overheadPlotList = new ArrayList<>();
		List<String> list = new ArrayList<>();
		Map<Integer, String> map = new HashMap<>();
		
		try (Stream<String> stream = Files.lines(Paths.get(plotslist))) {
			plotList = stream.collect(Collectors.toList());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		final List<String> finalPlotList = Collections.unmodifiableList(plotList);
		
		//read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			list = stream.collect(Collectors.toList());

			for (int i = 0; i < list.size(); i++) {
				String string = list.get(i);
				int idx = string.lastIndexOf('\t');
				//System.out.println(string +" : " + idx);
				String number = string.substring(idx, string.length()).trim();
				String name = string.substring(0, idx).trim();
				map.put(Integer.parseInt(number), name);
			}
			/*for (int i = 0; i < plotList.size(); i++) {
				if(!map.containsKey(Integer.parseInt(plotList.get(i)))) {
					map.put(Integer.parseInt(plotList.get(i)), "");
				}
			}
			
			ArrayList<Integer> sortedKeys = new ArrayList<>(map.keySet());

			Collections.sort(sortedKeys);

			// Display the TreeMap which is naturally sorted
			for (int x : sortedKeys) {
				if(plotList.contains(String.valueOf(x))) {
					processedPlotList.add(String.valueOf(x));
					System.out.println("<tr><td>"+x + "</td><td>" + map.get(x) + "</td></tr>");
				} else {
					overheadPlotList.add(x);
				}
			}*/
			
			/*plotList.removeAll(processedPlotList);
			for (String plot : plotList) {
				System.out.println("<tr><td>"+plot + "</td><td></td></tr>");
			}*/
			
			System.out.println("========hello========");
			for (Integer plotNo : overheadPlotList) {
				System.out.println("<tr><td>"+plotNo + "</td><td>" + map.get(plotNo) + "</td></tr>");
			}

			//String result = GoogleExcelUtil.getExcelData(SPREADSHEET_ID_UG, SHEET_SELECTION_UG, API_KEY);
			//System.out.println(result);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try (Stream<String> stream = Files.lines(Paths.get(paidlist))) {
			paidList = stream.collect(Collectors.toList());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("========hello1========");
		for (String x : finalPlotList) {
			if(!paidList.contains(x)) {
				System.out.println(x + " pending.");
			} else {
				//System.out.println(x + " paid.");
			}
		}
		System.out.println("========hello1========");

	}

}
