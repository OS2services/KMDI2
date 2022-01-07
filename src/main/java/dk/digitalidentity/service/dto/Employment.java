package dk.digitalidentity.service.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import dk.digitalidentity.controller.api.dto.EmploymentDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employment {
	private String ssn;
	private String institutionDtrId;
	private String email;
	private String mobilePhone;
	private List<String> roles;

	// these fields are never edited, just read and returned on updates
	private long employmentId;
	private String institutionProductionNumber;
	private long institutionId;
	private boolean manuallyAdded;
	private String aliasName;
	private String workPhone;
	
	// default values for create, will be overwritten on updates by whatever is in KMD I2
	private String endDate = "9999-12-31T00:00:00";
	private String startDate =  LocalDate.now().toString() + "T00:00:00";
	private boolean transferToUserAdministration = true;

	public void copyFromEmployment(EmploymentDTO e) {
		this.ssn = e.getSsn();
		this.institutionDtrId = e.getInstitutionDtrId();
		this.email = e.getEmail();
		this.mobilePhone = e.getMobilePhone();
		this.roles = e.getRoles();
	}
}
