package dk.digitalidentity.controller.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.config.AppConfiguration;
import dk.digitalidentity.controller.api.dto.EmploymentDTO;
import dk.digitalidentity.service.KMDI2Service;
import dk.digitalidentity.service.dto.Employment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ApiController {

	@Autowired
	private KMDI2Service kmdI2Service;
	
	@Autowired
	private AppConfiguration configuration;

	@GetMapping("/api/employments")
	public ResponseEntity<?> getEmployments() {
		log.info("getEmployments called");

		List<EmploymentDTO> employmentsDTO = null;
		if (!configuration.getKmdi2().isEnabled()) {
			log.info("returning stubbed response");
			employmentsDTO = configuration.getTestData().getEmployments();
		}
		else {
			List<Employment> employments = kmdI2Service.getEmployments();
			if (employments == null) {
				return new ResponseEntity<>("Unable to retrieve employments from KMD I2", HttpStatus.INTERNAL_SERVER_ERROR);
			}
	
			employmentsDTO = employments.stream()
					.filter(e -> StringUtils.hasLength(e.getInstitutionDtrId()))
					.map(e -> new EmploymentDTO(e))
					.collect(Collectors.toList());
		}

		return ResponseEntity.ok(employmentsDTO);
	}

	@PostMapping("/api/employments")
	public ResponseEntity<?> createEmployment(@RequestBody EmploymentDTO employment) {
		log.info("createEmployment called");

		ResponseEntity<?> errorResponse = validate(employment);
		if (errorResponse != null) {
			return errorResponse;
		}

		if (!configuration.getKmdi2().isEnabled()) {
			log.info("returning stubbed response");
			return ResponseEntity.ok("");
		}

		boolean success = kmdI2Service.createEmployment(employment);
		if (success) {
			return ResponseEntity.ok("");			
		}

		return new ResponseEntity<String>("Failed to update KMD I2", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PostMapping("/api/employments/{id}")
	public ResponseEntity<?> updateEmployment(@PathVariable("id") String id, @RequestBody EmploymentDTO employment) {
		log.info("updateEmployment called for employee ID " + id);

		ResponseEntity<?> errorResponse = validate(employment);
		if (errorResponse != null) {
			return errorResponse;
		}

		if (!configuration.getKmdi2().isEnabled()) {
			log.info("returning stubbed response");
			return ResponseEntity.ok("");
		}

		boolean success = kmdI2Service.updateEmployment(id, employment);
		if (success) {
			return ResponseEntity.ok("");			
		}

		return new ResponseEntity<String>("Failed to update KMD I2", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@DeleteMapping("/api/employments/{id}")
	public ResponseEntity<?> deleteEmployment(@PathVariable("id") String id) {
		log.info("deleteEmployment called for employee ID " + id);

		if (!configuration.getKmdi2().isEnabled()) {
			log.info("returning stubbed response");
			return ResponseEntity.ok("");
		}

		boolean success = kmdI2Service.deleteEmployment(id);
		if (success) {
			return ResponseEntity.ok("");
		}

		return new ResponseEntity<String>("Failed to update KMD I2", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<?> validate(EmploymentDTO employment) {
		if (!StringUtils.hasLength(employment.getSsn()) || employment.getSsn().length() != 10) {
			log.warn("Bad SSN '" + employment.getSsn() + "'");
			return new ResponseEntity<String>("Bad SSN '" + employment.getSsn() + "'", HttpStatus.BAD_REQUEST);
		}
		
		if (!StringUtils.hasLength(employment.getInstitutionDtrId())) {
			log.warn("DTR ID must be present");
			return new ResponseEntity<String>("DTR ID must be present", HttpStatus.BAD_REQUEST);
		}
		
		return null;
	}
}
