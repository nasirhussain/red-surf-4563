package com.example;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class VillaWaterDates {
	
	private String plotOrVillaNo;
	private List<LocalDate> waterSupplyDateList;
	private LocalDate nextDate;
	private LocalDate lastSupplyDate = null;
	
	public String getPlotOrVillaNo() {
		return plotOrVillaNo;
	}
	public void setPlotOrVillaNo(String plotOrVillaNo) {
		this.plotOrVillaNo = plotOrVillaNo;
	}
	public List<LocalDate> getWaterSupplyDateList() {
		return waterSupplyDateList;
	}
	public void setWaterSupplyDateList(List<LocalDate> waterSupplyDateList) {
		this.waterSupplyDateList = waterSupplyDateList;
	}
	public LocalDate getNextDate() {
		return nextDate;
	}
	public void setNextDate(LocalDate nextDate) {
		this.nextDate = nextDate;
	}
	public LocalDate getLastSupplyDate() {
		return lastSupplyDate;
	}
	public void setLastSupplyDate(LocalDate lastSupplyDate) {
		this.lastSupplyDate = lastSupplyDate;
	}
	
	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
	}
	
}

