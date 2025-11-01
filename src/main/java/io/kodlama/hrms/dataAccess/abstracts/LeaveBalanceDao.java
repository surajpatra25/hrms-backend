package io.kodlama.hrms.dataAccess.abstracts;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.kodlama.hrms.entities.concretes.LeaveBalance;

public interface LeaveBalanceDao extends JpaRepository<LeaveBalance, Integer> {

	Optional<LeaveBalance> getByEmployee_IdAndYear(int employeeId, int year);

}
