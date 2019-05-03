package com.mma.rest.internal.fee_types.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mma.common.datatable.domain.DataTableRequest;
import com.mma.common.datatable.domain.DataTableResponse;
import com.mma.domain.FeeType;
import com.mma.domain.User;
import com.mma.menu.MenuController;
import com.mma.repository.FeeTypeRepository;
import com.mma.repository.UserRepository;
import com.mma.repository.UserRoleRepository;
import com.mma.rest.internal.fee_types.resource.FeeTypeConverter;
import com.mma.rest.internal.fee_types.resource.FeeTypeResource;
import com.mma.rest.internal.fee_types.service.FeeTypeService;
import com.mma.rest.internal.users.service.UserService;
import com.mma.userdetails.CurrentUser;
import com.mma.userdetails.UserPrincipal;

@MenuController(value = "/feeTypes", item = com.mma.menu.Item.FeeTypes)
public class FeeTypeController {

	@Autowired
	private FeeTypeService feeTypeService;

	@Autowired
	private FeeTypeConverter feeTypeConverter;
	
	@Autowired
	private FeeTypeRepository feeTypeRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository roleRepository;
	
	@GetMapping
	public String showPage(@CurrentUser UserPrincipal principal) {	
		return "fee-types/fee-types";
	}
	
	@PostMapping("/data")
	public @ResponseBody DataTableResponse<FeeTypeResource> getFeeTypes(@RequestBody DataTableRequest request, @CurrentUser UserPrincipal principal) {		
		return feeTypeConverter.convertResponse(feeTypeService.getFeeTypes(request, principal));
	}
	
	@GetMapping("/newFeeType")
	public String newFeeType(@CurrentUser UserPrincipal principal, Model model) {
		FeeTypeResource feeType = new FeeTypeResource();

		return showPage(principal, feeType, model);
	}

	protected String showPage(UserPrincipal principal, FeeTypeResource feeType, Model model) {
		model.addAttribute("feeTypeForm", feeType);
		model.addAttribute("unitList", userService.getUnits());

		return "fee-types/fee-type-edit";
	}

	@PostMapping("/newFeeType")
	public String createFeeType(@CurrentUser UserPrincipal principal, @ModelAttribute("feeTypeForm") @Validated FeeTypeResource resource, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showPage(principal, resource, model);
		}

		return saveFeeType(principal, 0, resource);
	}

	@GetMapping("/{feeTypeId}")
	public String editFeeType(@CurrentUser UserPrincipal principal, @PathVariable int feeTypeId, Model model) {
		FeeType feeType = feeTypeRepository.findById(feeTypeId);	
		FeeTypeResource res = feeTypeConverter.convert(feeType);

		return showPage(principal, res, model);
	}

	@PostMapping("/{feeTypeId}")
	public String updateFeeType(@CurrentUser UserPrincipal principal, @PathVariable int feeTypeId, @ModelAttribute("feeTypeForm") @Validated FeeTypeResource resource, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showPage(principal, resource, model);
		}

		return saveFeeType(principal, feeTypeId, resource);
	}

	protected String saveFeeType(UserPrincipal principal, int feeTypeId, FeeTypeResource resource) {
		FeeType feeType = populateFeeType(feeTypeId, resource);
		
		boolean principalModerator = isPrincipalModerator(principal);
		int unitId;
		if(principalModerator) {
			User u = userRepository.findById(principal.getId());
			unitId = u.getUnit().getId();
		} else {
			unitId = getUnitId(resource);
		}
		
		if (feeTypeId == 0) {
			feeTypeService.createFeeType(principal, feeType, unitId);
		} else {
			feeTypeService.updateFeeType(principal, feeType, unitId);
		}

		return "redirect:/feeTypes";
	}

	protected FeeType populateFeeType(int feeTypeId, FeeTypeResource resource) {
		FeeType feeType = null;
		if (feeTypeId == 0) {
			feeType = new FeeType();
			feeType.setTimeAdded(new Date());
		} else {
			feeType = feeTypeRepository.findById(feeTypeId);
		}

		feeType.setName(resource.getName());
		feeType.setDescription(resource.getDescription());
		feeType.setPrice(resource.getPrice());

		return feeType;
	}

	protected int getUnitId(FeeTypeResource resource) {
		return resource.getUnit() != null ? resource.getUnit().getId() : 0;
	}
	
	protected boolean isPrincipalModerator(UserPrincipal principal) {
		User u = userRepository.findById(principal.getId());
		return u.getRole().equals(roleRepository.findByName("MODERATOR"));
	}
}
