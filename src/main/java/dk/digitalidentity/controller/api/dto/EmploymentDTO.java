package dk.digitalidentity.controller.api.dto;

import java.util.List;

import dk.digitalidentity.service.dto.Employment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmploymentDTO {
	private String ssn;
	private long employmentId;
	private String institutionDtrId;
	private String email;
	private String mobilePhone;
	private List<String> roles;
	
	public EmploymentDTO(Employment employment) {
		this.ssn = employment.getSsn();
		this.employmentId = employment.getEmploymentId();
		this.institutionDtrId = employment.getInstitutionDtrId();
		this.email = employment.getEmail();
		this.mobilePhone = employment.getMobilePhone();
		this.roles = employment.getRoles();
	}
}
