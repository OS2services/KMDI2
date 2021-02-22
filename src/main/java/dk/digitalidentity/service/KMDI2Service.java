package dk.digitalidentity.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dk.digitalidentity.config.AppConfiguration;
import dk.digitalidentity.controller.api.dto.EmploymentDTO;
import dk.digitalidentity.service.dto.Employment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KMDI2Service {
	private static final String ApiKeyHeader = "Ocp-Apim-Subscription-Key";

	@Autowired
	private AppConfiguration configuration;

	@Autowired
	private RestTemplate restTemplate;

	public List<Employment> getEmployments() {
		String url = configuration.getKmdi2().getUrl() + "employments";

		HttpHeaders headers = new HttpHeaders();
		headers.add(ApiKeyHeader, configuration.getKmdi2().getApiKey());

		HttpEntity<String> request = new HttpEntity<>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				try {
					List<Employment> employments = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Employment>>() { });

					return employments;
				}
				catch (JsonProcessingException ex) {
					log.error("Unable to parse JSON response: " + response.getBody(), ex);
				}
			}
			else {
				log.error("Read gave NON-200 response from KMD-I2: " + response.getStatusCode() + ": " + response.getBody());
			}
		}
		catch (RestClientException ex) {
			log.error("Error occured while connecting to KMD I2", ex);
		}

		return null;
	}

	public boolean createEmployment(EmploymentDTO employment) {
		if (configuration.getKmdi2().isDryRun()) {
			log.info("DRYRUN: Did NOT call KMD I2 API with the following CREATE payload: " + employment.toString());
			return true;
		}

		String url = configuration.getKmdi2().getUrl() + "employment/dtrId/" + employment.getInstitutionDtrId();

		HttpHeaders headers = new HttpHeaders();
		headers.add(ApiKeyHeader, configuration.getKmdi2().getApiKey());

		Employment e = new Employment();
		e.copyFromEmployment(employment);
		
		HttpEntity<Employment> request = new HttpEntity<>(e, headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
			if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() <= 299) {
				return true;
			}
			else {
				log.error("Create gave NON-200 response from KMD-I2: " + response.getStatusCode() + ": " + response.getBody() + ". Payload was: " + e.toString());
			}
		}
		catch (RestClientException ex) {
			log.error("Error occured while connecting to KMD I2", ex);
		}

		return false;
	}

	public boolean updateEmployment(String id, EmploymentDTO employment) {
		if (configuration.getKmdi2().isDryRun()) {
			log.info("DRYRUN: Did NOT call KMD I2 API with the following UPDATE payload: " + employment.toString());
			return true;
		}

		String url = configuration.getKmdi2().getUrl() + "employment/" + id;

		HttpHeaders headers = new HttpHeaders();
		headers.add(ApiKeyHeader, configuration.getKmdi2().getApiKey());

		Employment e = readEmployment(id);
		if (e == null) {
			return false;
		}

		e.copyFromEmployment(employment);
		
		HttpEntity<Employment> request = new HttpEntity<>(e, headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
			if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() <= 299) {
				return true;
			}
			else {
				log.error("Update gave NON-200 response from KMD-I2: " + response.getStatusCode() + ": " + response.getBody() + ". Payload was: " + e.toString());
			}
		}
		catch (RestClientException ex) {
			log.error("Error occured while connecting to KMD I2", ex);
		}

		return false;
	}

	public boolean deleteEmployment(String id) {
		if (configuration.getKmdi2().isDryRun()) {
			log.info("DRYRUN: Did NOT call KMD I2 API with the following DELETE payload: [employmentId=" + id + "]");
			return true;
		}

		String url = configuration.getKmdi2().getUrl() + "employment/" + id;

		HttpHeaders headers = new HttpHeaders();
		headers.add(ApiKeyHeader, configuration.getKmdi2().getApiKey());

		HttpEntity<String> request = new HttpEntity<>(headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
			if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() <= 299) {
				return true;
			}
			else {
				log.error("Delete gave NON-200 response from KMD-I2: " + response.getStatusCode() + ": " + response.getBody());
			}
		}
		catch (RestClientException ex) {
			log.error("Error occured while connecting to KMD I2", ex);
		}

		return false;
	}
	
	private Employment readEmployment(String id) {
		String url = configuration.getKmdi2().getUrl() + "employment/" + id;

		HttpHeaders headers = new HttpHeaders();
		headers.add(ApiKeyHeader, configuration.getKmdi2().getApiKey());

		HttpEntity<Employment> request = new HttpEntity<>(headers);
		try {
			ResponseEntity<Employment> response = restTemplate.exchange(url, HttpMethod.GET, request, Employment.class);
			if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() <= 299) {
				return response.getBody();
			}
			else {
				log.error("Read gave NON-200 response from KMD-I2: " + response.getStatusCode() + ": " + response.getBody());
			}
		}
		catch (RestClientException ex) {
			log.error("Error occured while connecting to KMD I2", ex);
		}

		return null;
	}
}
