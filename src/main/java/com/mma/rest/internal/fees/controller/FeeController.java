package com.mma.rest.internal.fees.controller;

import java.util.Date;
import java.util.List;

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
import com.mma.common.enums.UserEnums;
import com.mma.domain.Fee;
import com.mma.domain.FeeType;
import com.mma.domain.Unit;
import com.mma.domain.User;
import com.mma.domain.UserRole;
import com.mma.menu.Item;
import com.mma.menu.MenuController;
import com.mma.repository.FeeRepository;
import com.mma.repository.FeeTypeRepository;
import com.mma.repository.UnitRepository;
import com.mma.repository.UserRepository;
import com.mma.repository.UserRoleRepository;
import com.mma.rest.internal.fees.resource.FeeConverter;
import com.mma.rest.internal.fees.resource.FeeResource;
import com.mma.rest.internal.fees.resource.UserResource;
import com.mma.rest.internal.fees.service.FeeService;
import com.mma.userdetails.CurrentUser;
import com.mma.userdetails.UserPrincipal;

@MenuController(value = "/fees", item = Item.Fees)
public class FeeController {

	@Autowired
	private FeeService feeService;

	@Autowired
	private FeeConverter feeConverter;
	
	@Autowired
	private FeeRepository feeRepository;
	
	@Autowired
	private FeeTypeRepository feeTypeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository roleRepository;
	
	@Autowired
	private UnitRepository unitRepository;
	
	@GetMapping
	public String showPage(@CurrentUser UserPrincipal principal) {	
		return "fees/fees";
	}
	
	@PostMapping("/data")
	public @ResponseBody DataTableResponse<FeeResource> getFees(@RequestBody DataTableRequest request, @CurrentUser UserPrincipal principal) {		
		return feeConverter.convertResponse(feeService.getFees(request, principal));
	}
	
	@GetMapping("/newFee")
	public String newFee(@CurrentUser UserPrincipal principal, Model model) {
		FeeResource fee = new FeeResource();

		return showPage(principal, fee, model);
	}

	protected String showPage(UserPrincipal principal, FeeResource fee, Model model) {
		model.addAttribute("feeForm", fee);
		if(isPrincipalAdmin(principal)) {
			User u = userRepository.findById(principal.getId());
			model.addAttribute("unitList", u.getUnit());
			model.addAttribute("userList", userRepository.findByRoleAndStatus(getRole("MODERATOR"), UserEnums.Status.ENABLED));
			model.addAttribute("feeTypeList", feeTypeRepository.findByUnit(u.getUnit()));
		} else {
			User u = userRepository.findById(principal.getId());
			model.addAttribute("unitList", u.getUnit());
			model.addAttribute("userList", userRepository.findByUnitAndStatus(u.getUnit(), UserEnums.Status.ENABLED));
			model.addAttribute("feeTypeList", feeTypeRepository.findByUnit(u.getUnit()));
		}

		return "fees/fee-edit";
	}

	@PostMapping("/newFee")
	public String createFee(@CurrentUser UserPrincipal principal, @ModelAttribute("feeForm") @Validated FeeResource resource, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showPage(principal, resource, model);
		}

		return saveFee(principal, 0, resource);
	}

	@GetMapping("/{feeId}")
	public String editFee(@CurrentUser UserPrincipal principal, @PathVariable int feeId, Model model) {
		Fee fee = feeRepository.findById(feeId);	
		FeeResource res = feeConverter.convert(fee);

		return showPage(principal, res, model);
	}

	@PostMapping("/{feeId}")
	public String updateFee(@CurrentUser UserPrincipal principal, @PathVariable int feeId, @ModelAttribute("feeForm") @Validated FeeResource resource, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showPage(principal, resource, model);
		}

		return saveFee(principal, feeId, resource);
	}

	protected String saveFee(UserPrincipal principal, int feeId, FeeResource resource) {
		Fee fee = populateFee(feeId, resource);
		
		boolean principalModerator = isPrincipalModerator(principal);
		int unitId;
		if(principalModerator) {
			User u = userRepository.findById(principal.getId());
			unitId = u.getUnit().getId();
		} else {
			unitId = getUnitId(resource);
		}
		
		if (feeId == 0) {
			feeService.createFee(principal, fee, unitId, resource);
		} else {
			feeService.updateFee(principal, fee, unitId, resource);
		}

		return "redirect:/fees";
	}

	protected Fee populateFee(int feeId, FeeResource resource) {
		Fee fee = null;
		if (feeId == 0) {
			fee = new Fee();
			fee.setTimeAdded(new Date());
		} else {
			fee = feeRepository.findById(feeId);
		}

		return fee;
	}
	
	@GetMapping(value = "/getUsers/{unitId}")
	@ResponseBody
	private List<UserResource> getUsers(@PathVariable("unitId") Integer unitId) {
		return feeService.getUsersByUnit(unitId);
	}
	
	@GetMapping(value = "/getFeeTypes/{unitId}")
	@ResponseBody
	private List<FeeType> getFeeTypes(@PathVariable("unitId") Integer unitId) {
		Unit u = unitRepository.findById(unitId);
		return feeTypeRepository.findByUnit(u);
	}

	protected int getUnitId(FeeResource resource) {
		return resource.getUnit() != null ? resource.getUnit().getId() : 0;
	}
	
	protected boolean isPrincipalAdmin(UserPrincipal principal) {
		User u = userRepository.findById(principal.getId());
		return u.getRole().equals(roleRepository.findByName("ADMIN"));
	}
	
	protected boolean isPrincipalModerator(UserPrincipal principal) {
		User u = userRepository.findById(principal.getId());
		return u.getRole().equals(roleRepository.findByName("MODERATOR"));
	}
	
	protected UserRole getRole(String name) {
		return roleRepository.findByName(name);
	}

	
}