package io.kodlama.hrms.entities.concretes;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.kodlama.hrms.core.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "leaves")
public class Leave {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@ManyToOne()
	@JoinColumn(name = "employee_id")
	private User employee;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "leave_type")
	@Enumerated(EnumType.STRING)
	private LeaveType leaveType;

	@Column(name = "reason")
	private String reason;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private LeaveStatus status;

	@Column(name = "applied_date")
	private LocalDate appliedDate;

	@Column(name = "approved_by")
	private Integer approvedBy;

	@Column(name = "approved_date")
	private LocalDate approvedDate;

	@Column(name = "rejection_reason")
	private String rejectionReason;

	public enum LeaveType {
		ANNUAL, SICK, PERSONAL, EMERGENCY, MATERNITY, PATERNITY
	}

	public enum LeaveStatus {
		PENDING, APPROVED, REJECTED, CANCELLED
	}

	public Leave(User employee, LocalDate startDate, LocalDate endDate, LeaveType leaveType, String reason) {
		this.employee = employee;
		this.startDate = startDate;
		this.endDate = endDate;
		this.leaveType = leaveType;
		this.reason = reason;
		this.status = LeaveStatus.PENDING;
		this.appliedDate = LocalDate.now();
	}

}
