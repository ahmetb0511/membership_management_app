package com.mma.rest.internal.reports.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mma.common.datatable.domain.DataTableRequest;
import com.mma.common.datatable.domain.DataTableResponse;
import com.mma.menu.Item;
import com.mma.menu.MenuController;
import com.mma.rest.internal.fees.resource.FeeConverter;
import com.mma.rest.internal.fees.resource.FeeResource;
import com.mma.rest.internal.reports.service.ReportService;
import com.mma.userdetails.CurrentUser;
import com.mma.userdetails.UserPrincipal;

@MenuController(value = "/reports", item = Item.Reports)
public class ReportController {

	@Autowired
	private ReportService reportService;
	
	@Autowired
	private FeeConverter feeConverter;
	
	@GetMapping
	public String showPage() {
		return "reports/reports";
	}

	@PostMapping("/data")
	public @ResponseBody DataTableResponse<FeeResource> getReports(@RequestBody DataTableRequest request, @CurrentUser UserPrincipal principal) {		
		return feeConverter.convertResponse(reportService.getReports(request, principal));
	}
	
}