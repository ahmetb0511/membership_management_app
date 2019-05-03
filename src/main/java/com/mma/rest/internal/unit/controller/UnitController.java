package com.mma.rest.internal.unit.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mma.common.datatable.domain.DataTableRequest;
import com.mma.common.datatable.domain.DataTableResponse;
import com.mma.common.enums.UnitEnums;
import com.mma.domain.Unit;
import com.mma.menu.MenuController;
import com.mma.rest.internal.unit.resource.UnitConverter;
import com.mma.rest.internal.unit.resource.UnitResource;
import com.mma.rest.internal.unit.service.UnitService;

@MenuController(value = "/units", item = com.mma.menu.Item.Units)
public class UnitController {

	@Autowired
	private UnitService unitService;

	@Autowired
	private UnitConverter unitConverter;

	
	@GetMapping
	public String showPage() {
		return "units/units";
	}
	
	@PostMapping("/data")
	public @ResponseBody DataTableResponse<UnitResource> getUnits(@RequestBody DataTableRequest request) {
		return unitConverter.convertResponse(unitService.getUnits(request));
	}
	
	@DeleteMapping("/{unitIds}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUnits(@PathVariable Integer[] unitIds) {
		for (Integer unitId: unitIds) {
			unitService.deleteUnit(unitId);
		}
	}
	
	@PutMapping("/{unitIds}/disable")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void disableUnits(@PathVariable Integer[] unitIds) {
		changeStatus(unitIds, false);
	}
	
	@PutMapping("/{unitIds}/enable")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void enableUnits(@PathVariable Integer[] unitIds) {
		changeStatus(unitIds, true);
	}
	
	protected void changeStatus(Integer[] unitIds, boolean enabled) {
		for (Integer unitId: unitIds) {
			unitService.updateStatus(unitId, enabled);
		}
	}
	
	//
	
	@GetMapping("/newUnit")
	public String newUnit( Model model) {
		UnitResource unit = new UnitResource();
		unit.setEnabled(true);
		
		return showPage(unit, model);
	}

	protected String showPage(UnitResource unit, Model model) {
		model.addAttribute("unitForm", unit);
		
		return "units/unit-edit";
	}

	
	@PostMapping("/newUnit")
	public String createUnit(@ModelAttribute("unitForm") @Validated UnitResource resource, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showPage(resource, model);
		}
	
		return saveUnit(0, resource);
	}

	@GetMapping("/{unitId}")
	public String editUnit(@PathVariable int unitId, Model model) {
		Unit unit = unitService.getUnit(unitId);
		
		UnitResource res = unitConverter.convert(unit);
		

		return showPage(res, model);
	}

	

	@PostMapping("/{unitId}")
	public String updateUnit(@PathVariable int unitId, @ModelAttribute("unitForm") @Validated UnitResource resource, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showPage(resource, model);
		}

		return saveUnit(unitId, resource);
	}

	protected String saveUnit(int unitId, UnitResource resource) {
		Unit unit = populateUnit(unitId, resource);
		
		if (unitId == 0) {
			unitService.createUnit(unit);
		} else {
			unitService.updateUnit(unit);
		}

		return "redirect:/units";
	}

	protected Unit populateUnit(int unitId, UnitResource resource) {
		Unit unit = null;
		if (unitId == 0) {
			unit = new Unit();
			unit.setTimeAdded(new Date());
		} else {
			unit = unitService.getUnit(unitId);
		}

		unit.setName(resource.getName());
		unit.setSupportEmail(resource.getSupportEmail());
		unit.setStatus(resource.isEnabled() ? UnitEnums.Status.ACTIVE : UnitEnums.Status.BLOCKED);
		
		return unit;
	}
}
