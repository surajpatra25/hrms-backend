package io.kodlama.hrms.dataAccess.abstracts;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.kodlama.hrms.entities.concretes.Leave;

public interface LeaveDao extends JpaRepository<Leave, Integer> {

	List<Leave> getByEmployee_Id(int employeeId);

	List<Leave> getByEmployee_IdAndStatus(int employeeId, Leave.LeaveStatus status);

	List<Leave> getByEmployee_IdAndLeaveType(int employeeId, Leave.LeaveType leaveType);

	@Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId AND l.startDate >= :startDate AND l.endDate <= :endDate")
	List<Leave> getByEmployeeIdAndDateRange(@Param("employeeId") int employeeId, 
											@Param("startDate") LocalDate startDate, 
											@Param("endDate") LocalDate endDate);

	@Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId AND l.status = 'APPROVED' AND l.leaveType = 'ANNUAL' AND l.startDate >= :yearStart AND l.endDate <= :yearEnd")
	List<Leave> getApprovedAnnualLeavesByEmployeeAndYear(@Param("employeeId") int employeeId, 
														@Param("yearStart") LocalDate yearStart, 
														@Param("yearEnd") LocalDate yearEnd);

	@Query("SELECT COUNT(l) FROM Leave l WHERE l.employee.id = :employeeId AND l.status = 'PENDING'")
	int countPendingLeavesByEmployeeId(@Param("employeeId") int employeeId);

}
