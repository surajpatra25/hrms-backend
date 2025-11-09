package io.kodlama.hrms.dataAccess.abstracts;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.kodlama.hrms.entities.concretes.HRPolicy;

public interface HRPolicyDao extends JpaRepository<HRPolicy, Integer> {

	Optional<HRPolicy> findByPolicyName(String policyName);

	boolean existsByPolicyName(String policyName);

}

