package io.kodlama.hrms.api.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.kodlama.hrms.business.abstracts.LeaveService;
import io.kodlama.hrms.core.utilities.results.DataResult;
import io.kodlama.hrms.core.utilities.results.ErrorDataResult;
import io.kodlama.hrms.core.utilities.results.Result;
import io.kodlama.hrms.entities.concretes.Leave;
import io.kodlama.hrms.entities.concretes.LeaveBalance;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin
public class LeaveController {

	private LeaveService leaveService;

	@Autowired
	public LeaveController(LeaveService leaveService) {
		this.leaveService = leaveService;
	}

	@PostMapping("/apply")
	public ResponseEntity<?> applyLeave(@RequestParam int employeeId, 
									   @RequestParam String startDate, 
									   @RequestParam String endDate, 
									   @RequestParam String leaveType, 
									   @RequestParam(required = false) String reason) {
		
		try {
			LocalDate start = LocalDate.parse(startDate);
			LocalDate end = LocalDate.parse(endDate);
			Leave.LeaveType type = Leave.LeaveType.valueOf(leaveType.toUpperCase());
			
			Result result = leaveService.applyLeave(employeeId, start, end, type, reason);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorDataResult<>("Invalid date format or leave type"));
		}
	}

	@GetMapping("/balance")
	public ResponseEntity<?> getLeaveBalance(@RequestParam int employeeId, 
											@RequestParam(required = false) Integer year) {
		int targetYear = year != null ? year : LocalDate.now().getYear();
		DataResult<LeaveBalance> result = leaveService.getLeaveBalance(employeeId, targetYear);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/remaining")
	public ResponseEntity<?> getRemainingLeaveBalance(@RequestParam int employeeId, 
													 @RequestParam(required = false) Integer year) {
		int targetYear = year != null ? year : LocalDate.now().getYear();
		DataResult<Integer> result = leaveService.getRemainingLeaveBalance(employeeId, targetYear);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/employee")
	public ResponseEntity<?> getEmployeeLeaves(@RequestParam int employeeId) {
		DataResult<List<Leave>> result = leaveService.getEmployeeLeaves(employeeId);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/employee/status")
	public ResponseEntity<?> getEmployeeLeavesByStatus(@RequestParam int employeeId, 
													  @RequestParam String status) {
		try {
			Leave.LeaveStatus leaveStatus = Leave.LeaveStatus.valueOf(status.toUpperCase());
			DataResult<List<Leave>> result = leaveService.getEmployeeLeavesByStatus(employeeId, leaveStatus);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorDataResult<>("Invalid status"));
		}
	}

	@GetMapping("/employee/type")
	public ResponseEntity<?> getEmployeeLeavesByType(@RequestParam int employeeId, 
													@RequestParam String leaveType) {
		try {
			Leave.LeaveType type = Leave.LeaveType.valueOf(leaveType.toUpperCase());
			DataResult<List<Leave>> result = leaveService.getEmployeeLeavesByType(employeeId, type);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorDataResult<>("Invalid leave type"));
		}
	}

	@GetMapping("/employee/dateRange")
	public ResponseEntity<?> getEmployeeLeavesByDateRange(@RequestParam int employeeId, 
														 @RequestParam String startDate, 
														 @RequestParam String endDate) {
		try {
			LocalDate start = LocalDate.parse(startDate);
			LocalDate end = LocalDate.parse(endDate);
			DataResult<List<Leave>> result = leaveService.getEmployeeLeavesByDateRange(employeeId, start, end);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorDataResult<>("Invalid date format"));
		}
	}

	@GetMapping("/employee/year")
	public ResponseEntity<?> getLeavesByEmployeeAndYear(@RequestParam int employeeId, 
													   @RequestParam int year) {
		DataResult<List<Leave>> result = leaveService.getLeavesByEmployeeAndYear(employeeId, year);
		return ResponseEntity.ok(result);
	}

	@PutMapping("/approve")
	public ResponseEntity<?> approveLeave(@RequestParam int leaveId, 
										 @RequestParam int approvedBy) {
		Result result = leaveService.approveLeave(leaveId, approvedBy);
		return ResponseEntity.ok(result);
	}

	@PutMapping("/reject")
	public ResponseEntity<?> rejectLeave(@RequestParam int leaveId, 
										@RequestParam int approvedBy, 
										@RequestParam String rejectionReason) {
		Result result = leaveService.rejectLeave(leaveId, approvedBy, rejectionReason);
		return ResponseEntity.ok(result);
	}

	@PutMapping("/cancel")
	public ResponseEntity<?> cancelLeave(@RequestParam int leaveId, 
										@RequestParam int employeeId) {
		Result result = leaveService.cancelLeave(leaveId, employeeId);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/pending")
	public ResponseEntity<?> getAllPendingLeaves() {
		DataResult<List<Leave>> result = leaveService.getAllPendingLeaves();
		return ResponseEntity.ok(result);
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAllLeaves() {
		DataResult<List<Leave>> result = leaveService.getAll();
		return ResponseEntity.ok(result);
	}

	@GetMapping("/getById")
	public ResponseEntity<?> getLeaveById(@RequestParam int id) {
		DataResult<Leave> result = leaveService.getById(id);
		return ResponseEntity.ok(result);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorDataResult<Object> handleValidationException(MethodArgumentNotValidException exceptions) {
		Map<String, String> validationErrors = new HashMap<String, String>();
		for (FieldError fieldError : exceptions.getBindingResult().getFieldErrors()) {
			validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return new ErrorDataResult<Object>(validationErrors);
	}

}
