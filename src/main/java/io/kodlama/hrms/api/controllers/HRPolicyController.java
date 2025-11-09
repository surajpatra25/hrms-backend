package io.kodlama.hrms.api.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.kodlama.hrms.business.abstracts.HRPolicyService;
import io.kodlama.hrms.core.utilities.results.DataResult;
import io.kodlama.hrms.core.utilities.results.ErrorDataResult;
import io.kodlama.hrms.core.utilities.results.Result;
import io.kodlama.hrms.entities.concretes.HRPolicy;

@RestController
@RequestMapping("/api/hrpolicies")
@CrossOrigin
public class HRPolicyController {

	private HRPolicyService hrPolicyService;

	@Autowired
	public HRPolicyController(HRPolicyService hrPolicyService) {
		this.hrPolicyService = hrPolicyService;
	}

	@PostMapping("/upload")
	public ResponseEntity<?> uploadPolicy(@RequestParam("policyName") String policyName,
										 @RequestParam("file") MultipartFile file,
										 @RequestParam(value = "description", required = false) String description) {
		Result result = hrPolicyService.uploadPolicy(policyName, file, description);
		
		if (result.isSuccess()) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.badRequest().body(result);
		}
	}

	@GetMapping
	public ResponseEntity<?> getAllPolicies() {
		DataResult<List<HRPolicy>> result = hrPolicyService.getAllPolicies();
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{policyName}")
	public ResponseEntity<?> getPolicyByName(@PathVariable String policyName) {
		DataResult<HRPolicy> result = hrPolicyService.getPolicyByName(policyName);
		
		if (result.isSuccess()) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
		}
	}

	@GetMapping("/download")
	public ResponseEntity<?> downloadPolicy(@RequestParam String policyName, 
										   @RequestParam String fileName) {
		// First get the policy metadata
		DataResult<HRPolicy> policyResult = hrPolicyService.getPolicyByName(policyName);
		
		if (!policyResult.isSuccess()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorDataResult<>("Policy not found"));
		}

		// Get the file content with both policyName and fileName validation
		DataResult<byte[]> fileResult = hrPolicyService.downloadPolicy(policyName, fileName);
		
		if (!fileResult.isSuccess()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorDataResult<>(fileResult.getMessage()));
		}

		HRPolicy policy = policyResult.getData();
		byte[] fileContent = fileResult.getData();

		// Set headers for file download
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", policy.getFileName());
		headers.setContentLength(fileContent.length);

		return ResponseEntity.ok()
			.headers(headers)
			.body(fileContent);
	}

	@DeleteMapping("/{policyName}")
	public ResponseEntity<?> deletePolicy(@PathVariable String policyName) {
		Result result = hrPolicyService.deleteByPolicyName(policyName);
		
		if (result.isSuccess()) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
		}
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

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorDataResult<Object> handleGenericException(Exception exception) {
		return new ErrorDataResult<Object>("An error occurred: " + exception.getMessage());
	}

}

