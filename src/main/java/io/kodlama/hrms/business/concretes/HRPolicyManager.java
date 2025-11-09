package io.kodlama.hrms.business.concretes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.kodlama.hrms.business.abstracts.HRPolicyService;
import io.kodlama.hrms.core.utilities.results.DataResult;
import io.kodlama.hrms.core.utilities.results.ErrorDataResult;
import io.kodlama.hrms.core.utilities.results.ErrorResult;
import io.kodlama.hrms.core.utilities.results.Result;
import io.kodlama.hrms.core.utilities.results.SuccessDataResult;
import io.kodlama.hrms.core.utilities.results.SuccessResult;
import io.kodlama.hrms.dataAccess.abstracts.HRPolicyDao;
import io.kodlama.hrms.entities.concretes.HRPolicy;

@Service
public class HRPolicyManager implements HRPolicyService {

	private HRPolicyDao hrPolicyDao;

	@Value("${hrms.policy.upload-dir:./policy-files}")
	private String uploadDir;

	@Value("${hrms.policy.max-file-size:10485760}") // 10MB default
	private long maxFileSize;

	@Autowired
	public HRPolicyManager(HRPolicyDao hrPolicyDao) {
		this.hrPolicyDao = hrPolicyDao;
	}

	@Override
	public Result add(HRPolicy entity) {
		this.hrPolicyDao.save(entity);
		return new SuccessResult("HR Policy added successfully");
	}

	@Override
	public Result update(HRPolicy entity) {
		this.hrPolicyDao.save(entity);
		return new SuccessResult("HR Policy updated successfully");
	}

	@Override
	public Result delete(int id) {
		Optional<HRPolicy> policy = this.hrPolicyDao.findById(id);
		if (policy.isPresent()) {
			// Delete the physical file
			deletePhysicalFile(policy.get().getFilePath());
			this.hrPolicyDao.deleteById(id);
			return new SuccessResult("HR Policy deleted successfully");
		}
		return new ErrorResult("HR Policy not found");
	}

	@Override
	public DataResult<List<HRPolicy>> getAll() {
		return new SuccessDataResult<List<HRPolicy>>(this.hrPolicyDao.findAll(), "All HR Policies listed successfully");
	}

	@Override
	public DataResult<HRPolicy> getById(int id) {
		Optional<HRPolicy> policy = this.hrPolicyDao.findById(id);
		if (policy.isPresent()) {
			return new SuccessDataResult<HRPolicy>(policy.get(), "HR Policy found successfully");
		}
		return new ErrorDataResult<HRPolicy>("HR Policy not found");
	}

	@Override
	public Result uploadPolicy(String policyName, MultipartFile file, String description) {
		// Validate policy name
		if (policyName == null || policyName.trim().isEmpty()) {
			return new ErrorResult("Policy name cannot be empty");
		}

		// Check if policy with same name already exists
		if (this.hrPolicyDao.existsByPolicyName(policyName)) {
			return new ErrorResult("A policy with this name already exists");
		}

		// Validate file
		if (file == null || file.isEmpty()) {
			return new ErrorResult("File cannot be empty");
		}

		// Validate file size
		if (file.getSize() > maxFileSize) {
			return new ErrorResult("File size exceeds maximum allowed size of " + (maxFileSize / 1024 / 1024) + "MB");
		}

		// Validate file type (PDF only)
		String contentType = file.getContentType();
		String originalFilename = file.getOriginalFilename();
		
		if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
			return new ErrorResult("Only PDF files are allowed");
		}

		if (contentType == null || !contentType.equals("application/pdf")) {
			return new ErrorResult("Invalid file type. Only PDF files are allowed");
		}

		try {
			// Create upload directory if it doesn't exist
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// Generate safe filename
			String safeFileName = policyName.replaceAll("[^a-zA-Z0-9-_]", "_") + ".pdf";
			Path filePath = uploadPath.resolve(safeFileName);

			// Copy file to the target location
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			// Create HRPolicy entity
			HRPolicy hrPolicy = new HRPolicy(
				policyName,
				originalFilename,
				filePath.toString(),
				file.getSize(),
				description
			);

			// Save to database
			this.hrPolicyDao.save(hrPolicy);

			return new SuccessResult("HR Policy uploaded successfully");

		} catch (IOException e) {
			return new ErrorResult("Failed to upload file: " + e.getMessage());
		}
	}

	@Override
	public DataResult<byte[]> downloadPolicy(String policyName, String fileName) {
		Optional<HRPolicy> policyOpt = this.hrPolicyDao.findByPolicyName(policyName);
		
		if (!policyOpt.isPresent()) {
			return new ErrorDataResult<byte[]>("HR Policy not found");
		}

		HRPolicy policy = policyOpt.get();
		
		// Validate that the fileName matches the stored fileName
		if (!policy.getFileName().equals(fileName)) {
			return new ErrorDataResult<byte[]>("File name does not match the policy");
		}

		Path filePath = Paths.get(policy.getFilePath());

		if (!Files.exists(filePath)) {
			return new ErrorDataResult<byte[]>("Policy file not found on server");
		}

		try {
			byte[] fileContent = Files.readAllBytes(filePath);
			return new SuccessDataResult<byte[]>(fileContent, "Policy file retrieved successfully");
		} catch (IOException e) {
			return new ErrorDataResult<byte[]>("Failed to read file: " + e.getMessage());
		}
	}

	@Override
	public DataResult<List<HRPolicy>> getAllPolicies() {
		return getAll();
	}

	@Override
	public DataResult<HRPolicy> getPolicyByName(String policyName) {
		Optional<HRPolicy> policy = this.hrPolicyDao.findByPolicyName(policyName);
		if (policy.isPresent()) {
			return new SuccessDataResult<HRPolicy>(policy.get(), "HR Policy found successfully");
		}
		return new ErrorDataResult<HRPolicy>("HR Policy not found");
	}

	@Override
	public Result deleteByPolicyName(String policyName) {
		Optional<HRPolicy> policyOpt = this.hrPolicyDao.findByPolicyName(policyName);
		if (policyOpt.isPresent()) {
			HRPolicy policy = policyOpt.get();
			// Delete the physical file
			deletePhysicalFile(policy.getFilePath());
			this.hrPolicyDao.delete(policy);
			return new SuccessResult("HR Policy deleted successfully");
		}
		return new ErrorResult("HR Policy not found");
	}

	private void deletePhysicalFile(String filePath) {
		try {
			Path path = Paths.get(filePath);
			if (Files.exists(path)) {
				Files.delete(path);
			}
		} catch (IOException e) {
			// Log the error but don't fail the operation
			System.err.println("Failed to delete physical file: " + e.getMessage());
		}
	}

}

