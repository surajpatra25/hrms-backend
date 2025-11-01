package io.kodlama.hrms.business.concretes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.kodlama.hrms.business.abstracts.LeaveService;
import io.kodlama.hrms.business.abstracts.UserService;
import io.kodlama.hrms.core.entities.User;
import io.kodlama.hrms.core.utilities.results.DataResult;
import io.kodlama.hrms.core.utilities.results.ErrorDataResult;
import io.kodlama.hrms.core.utilities.results.ErrorResult;
import io.kodlama.hrms.core.utilities.results.Result;
import io.kodlama.hrms.core.utilities.results.SuccessDataResult;
import io.kodlama.hrms.core.utilities.results.SuccessResult;
import io.kodlama.hrms.dataAccess.abstracts.LeaveBalanceDao;
import io.kodlama.hrms.dataAccess.abstracts.LeaveDao;
import io.kodlama.hrms.entities.concretes.Leave;
import io.kodlama.hrms.entities.concretes.LeaveBalance;

@Service
public class LeaveManager implements LeaveService {

	private LeaveDao leaveDao;
	private LeaveBalanceDao leaveBalanceDao;
	private UserService userService;

	@Autowired
	public LeaveManager(LeaveDao leaveDao, LeaveBalanceDao leaveBalanceDao, UserService userService) {
		this.leaveDao = leaveDao;
		this.leaveBalanceDao = leaveBalanceDao;
		this.userService = userService;
	}

	@Override
	public Result add(Leave entity) {
		this.leaveDao.save(entity);
		return new SuccessResult("Leave application added successfully");
	}

	@Override
	public Result update(Leave entity) {
		this.leaveDao.save(entity);
		return new SuccessResult("Leave application updated successfully");
	}

	@Override
	public Result delete(int id) {
		this.leaveDao.deleteById(id);
		return new SuccessResult("Leave application deleted successfully");
	}

	@Override
	public DataResult<List<Leave>> getAll() {
		return new SuccessDataResult<List<Leave>>(this.leaveDao.findAll(), "All leaves listed successfully");
	}

	@Override
	public DataResult<Leave> getById(int id) {
		Optional<Leave> leave = this.leaveDao.findById(id);
		if (leave.isPresent()) {
			return new SuccessDataResult<Leave>(leave.get(), "Leave found successfully");
		}
		return new ErrorDataResult<Leave>("Leave not found");
	}

	@Override
	public Result applyLeave(int employeeId, LocalDate startDate, LocalDate endDate, 
							Leave.LeaveType leaveType, String reason) {
		
		// Validate employee exists
		DataResult<User> employeeResult = this.userService.getById(employeeId);
		if (!employeeResult.isSuccess()) {
			return new ErrorResult("Employee not found");
		}

		// Validate dates
		if (startDate.isBefore(LocalDate.now())) {
			return new ErrorResult("Start date cannot be in the past");
		}

		if (endDate.isBefore(startDate)) {
			return new ErrorResult("End date cannot be before start date");
		}

		// Calculate leave days
		long leaveDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

		// Check for annual leave balance if it's an annual leave
		if (leaveType == Leave.LeaveType.ANNUAL) {
			int currentYear = LocalDate.now().getYear();
			LeaveBalance balance = getOrCreateLeaveBalance(employeeId, currentYear);
			
			if (balance.getRemainingLeaves() < leaveDays) {
				return new ErrorResult("Insufficient leave balance. Remaining: " + balance.getRemainingLeaves() + " days");
			}
		}

		// Check for overlapping leaves
		List<Leave> overlappingLeaves = this.leaveDao.getByEmployeeIdAndDateRange(employeeId, startDate, endDate);
		if (!overlappingLeaves.isEmpty()) {
			return new ErrorResult("You already have a leave application for this period");
		}

		// Check for pending leaves limit (max 3 pending applications)
		int pendingCount = this.leaveDao.countPendingLeavesByEmployeeId(employeeId);
		if (pendingCount >= 3) {
			return new ErrorResult("You cannot have more than 3 pending leave applications");
		}

		// Create leave application
		Leave leave = new Leave(employeeResult.getData(), startDate, endDate, leaveType, reason);
		this.leaveDao.save(leave);

		return new SuccessResult("Leave application submitted successfully");
	}

	@Override
	public DataResult<LeaveBalance> getLeaveBalance(int employeeId, int year) {
		LeaveBalance balance = getOrCreateLeaveBalance(employeeId, year);
		return new SuccessDataResult<LeaveBalance>(balance, "Leave balance retrieved successfully");
	}

	@Override
	public DataResult<List<Leave>> getEmployeeLeaves(int employeeId) {
		List<Leave> leaves = this.leaveDao.getByEmployee_Id(employeeId);
		return new SuccessDataResult<List<Leave>>(leaves, "Employee leaves listed successfully");
	}

	@Override
	public DataResult<List<Leave>> getEmployeeLeavesByStatus(int employeeId, Leave.LeaveStatus status) {
		List<Leave> leaves = this.leaveDao.getByEmployee_IdAndStatus(employeeId, status);
		return new SuccessDataResult<List<Leave>>(leaves, "Employee leaves by status listed successfully");
	}

	@Override
	public DataResult<List<Leave>> getEmployeeLeavesByType(int employeeId, Leave.LeaveType leaveType) {
		List<Leave> leaves = this.leaveDao.getByEmployee_IdAndLeaveType(employeeId, leaveType);
		return new SuccessDataResult<List<Leave>>(leaves, "Employee leaves by type listed successfully");
	}

	@Override
	public DataResult<List<Leave>> getEmployeeLeavesByDateRange(int employeeId, LocalDate startDate, LocalDate endDate) {
		List<Leave> leaves = this.leaveDao.getByEmployeeIdAndDateRange(employeeId, startDate, endDate);
		return new SuccessDataResult<List<Leave>>(leaves, "Employee leaves by date range listed successfully");
	}

	@Override
	public Result approveLeave(int leaveId, int approvedBy) {
		Optional<Leave> leaveOpt = this.leaveDao.findById(leaveId);
		if (!leaveOpt.isPresent()) {
			return new ErrorResult("Leave application not found");
		}

		Leave leave = leaveOpt.get();
		leave.setStatus(Leave.LeaveStatus.APPROVED);
		leave.setApprovedBy(approvedBy);
		leave.setApprovedDate(LocalDate.now());

		// Update leave balance if it's an annual leave
		if (leave.getLeaveType() == Leave.LeaveType.ANNUAL) {
			int year = leave.getStartDate().getYear();
			LeaveBalance balance = getOrCreateLeaveBalance(leave.getEmployee().getId(), year);
			
			long leaveDays = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
			balance.updateUsedLeaves((int) leaveDays);
			this.leaveBalanceDao.save(balance);
		}

		this.leaveDao.save(leave);
		return new SuccessResult("Leave application approved successfully");
	}

	@Override
	public Result rejectLeave(int leaveId, int approvedBy, String rejectionReason) {
		Optional<Leave> leaveOpt = this.leaveDao.findById(leaveId);
		if (!leaveOpt.isPresent()) {
			return new ErrorResult("Leave application not found");
		}

		Leave leave = leaveOpt.get();
		leave.setStatus(Leave.LeaveStatus.REJECTED);
		leave.setApprovedBy(approvedBy);
		leave.setApprovedDate(LocalDate.now());
		leave.setRejectionReason(rejectionReason);

		this.leaveDao.save(leave);
		return new SuccessResult("Leave application rejected successfully");
	}

	@Override
	public Result cancelLeave(int leaveId, int employeeId) {
		Optional<Leave> leaveOpt = this.leaveDao.findById(leaveId);
		if (!leaveOpt.isPresent()) {
			return new ErrorResult("Leave application not found");
		}

		Leave leave = leaveOpt.get();
		if (leave.getEmployee().getId() != employeeId) {
			return new ErrorResult("You can only cancel your own leave applications");
		}

		if (leave.getStatus() != Leave.LeaveStatus.PENDING) {
			return new ErrorResult("Only pending leave applications can be cancelled");
		}

		leave.setStatus(Leave.LeaveStatus.CANCELLED);
		this.leaveDao.save(leave);
		return new SuccessResult("Leave application cancelled successfully");
	}

	@Override
	public DataResult<Integer> getRemainingLeaveBalance(int employeeId, int year) {
		LeaveBalance balance = getOrCreateLeaveBalance(employeeId, year);
		return new SuccessDataResult<Integer>(balance.getRemainingLeaves(), "Remaining leave balance retrieved successfully");
	}

	@Override
	public DataResult<List<Leave>> getAllPendingLeaves() {
		List<Leave> leaves = this.leaveDao.getByEmployee_IdAndStatus(0, Leave.LeaveStatus.PENDING);
		return new SuccessDataResult<List<Leave>>(leaves, "All pending leaves listed successfully");
	}

	@Override
	public DataResult<List<Leave>> getLeavesByEmployeeAndYear(int employeeId, int year) {
		LocalDate yearStart = LocalDate.of(year, 1, 1);
		LocalDate yearEnd = LocalDate.of(year, 12, 31);
		List<Leave> leaves = this.leaveDao.getApprovedAnnualLeavesByEmployeeAndYear(employeeId, yearStart, yearEnd);
		return new SuccessDataResult<List<Leave>>(leaves, "Employee leaves for year listed successfully");
	}

	private LeaveBalance getOrCreateLeaveBalance(int employeeId, int year) {
		Optional<LeaveBalance> balanceOpt = this.leaveBalanceDao.getByEmployee_IdAndYear(employeeId, year);
		if (balanceOpt.isPresent()) {
			return balanceOpt.get();
		}

		// Create new leave balance for the year
		DataResult<User> employeeResult = this.userService.getById(employeeId);
		if (!employeeResult.isSuccess()) {
			throw new RuntimeException("Employee not found");
		}

		LeaveBalance newBalance = new LeaveBalance(employeeResult.getData(), year);
		return this.leaveBalanceDao.save(newBalance);
	}

}
