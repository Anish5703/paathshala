package com.paathshala.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueSummaryResponse {

    private double totalRevenue;
    private double thisMonthRevenue;
    private double lastMonthRevenue;
    private double growthPercentage;
    private List<MonthlyRevenue> monthly;

}