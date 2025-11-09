package io.kodlama.hrms.business.abstracts;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.kodlama.hrms.core.utilities.results.DataResult;
import io.kodlama.hrms.core.utilities.results.Result;
import io.kodlama.hrms.entities.concretes.HRPolicy;

public interface HRPolicyService extends BaseEntityService<HRPolicy> {

	Result uploadPolicy(String policyName, MultipartFile file, String description);

	DataResult<byte[]> downloadPolicy(String policyName, String fileName);

	DataResult<List<HRPolicy>> getAllPolicies();

	DataResult<HRPolicy> getPolicyByName(String policyName);

	Result deleteByPolicyName(String policyName);

}

