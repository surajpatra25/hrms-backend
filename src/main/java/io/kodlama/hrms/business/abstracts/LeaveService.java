package io.kodlama.hrms.business.abstracts;

import java.time.LocalDate;
import java.util.List;

import io.kodlama.hrms.core.utilities.results.DataResult;
import io.kodlama.hrms.core.utilities.results.Result;
import io.kodlama.hrms.entities.concretes.Leave;
import io.kodlama.hrms.entities.concretes.LeaveBalance;

public interface LeaveService extends BaseEntityService<Leave> {

	Result applyLeave(int employeeId, LocalDate startDate, LocalDate endDate, 
					 Leave.LeaveType leaveType, String reason);

	DataResult<LeaveBalance> getLeaveBalance(int employeeId, int year);

	DataResult<List<Leave>> getEmployeeLeaves(int employeeId);

	DataResult<List<Leave>> getEmployeeLeavesByStatus(int employeeId, Leave.LeaveStatus status);

	DataResult<List<Leave>> getEmployeeLeavesByType(int employeeId, Leave.LeaveType leaveType);

	DataResult<List<Leave>> getEmployeeLeavesByDateRange(int employeeId, LocalDate startDate, LocalDate endDate);

	Result approveLeave(int leaveId, int approvedBy);

	Result rejectLeave(int leaveId, int approvedBy, String rejectionReason);

	Result cancelLeave(int leaveId, int employeeId);

	DataResult<Integer> getRemainingLeaveBalance(int employeeId, int year);

	DataResult<List<Leave>> getAllPendingLeaves();

	DataResult<List<Leave>> getLeavesByEmployeeAndYear(int employeeId, int year);

}
