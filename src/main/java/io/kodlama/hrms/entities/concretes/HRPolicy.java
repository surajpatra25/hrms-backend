package io.kodlama.hrms.entities.concretes;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "hr_policies")
public class HRPolicy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "policy_name", unique = true, nullable = false)
	private String policyName;

	@Column(name = "file_name", nullable = false)
	private String fileName;

	@Column(name = "uploaded_date", nullable = false)
	private LocalDateTime uploadedDate;

	@Column(name = "description")
	private String description;

	@Column(name = "file_size")
	private Long fileSize;

	@Column(name = "file_path", nullable = false)
	private String filePath;

	public HRPolicy(String policyName, String fileName, String filePath, Long fileSize, String description) {
		this.policyName = policyName;
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileSize = fileSize;
		this.description = description;
		this.uploadedDate = LocalDateTime.now();
	}

}

