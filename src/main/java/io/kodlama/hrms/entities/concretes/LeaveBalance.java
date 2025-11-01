package io.kodlama.hrms.entities.concretes;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "leave_balances")
public class LeaveBalance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@ManyToOne()
	@JoinColumn(name = "employee_id")
	private User employee;

	@Column(name = "year")
	private int year;

	@Column(name = "total_annual_leaves")
	private int totalAnnualLeaves = 15; // Default 15 leaves per year

	@Column(name = "used_leaves")
	private int usedLeaves = 0;

	@Column(name = "remaining_leaves")
	private int remainingLeaves = 15;

	@Column(name = "created_date")
	private LocalDate createdDate;

	@Column(name = "updated_date")
	private LocalDate updatedDate;

	public LeaveBalance(User employee, int year) {
		this.employee = employee;
		this.year = year;
		this.totalAnnualLeaves = 15;
		this.usedLeaves = 0;
		this.remainingLeaves = 15;
		this.createdDate = LocalDate.now();
		this.updatedDate = LocalDate.now();
	}

	public void updateUsedLeaves(int additionalUsedLeaves) {
		this.usedLeaves += additionalUsedLeaves;
		this.remainingLeaves = this.totalAnnualLeaves - this.usedLeaves;
		this.updatedDate = LocalDate.now();
	}

	public void setUsedLeaves(int usedLeaves) {
		this.usedLeaves = usedLeaves;
		this.remainingLeaves = this.totalAnnualLeaves - this.usedLeaves;
		this.updatedDate = LocalDate.now();
	}

}
