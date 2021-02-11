package dk.digitalidentity.config;

import java.util.List;

import dk.digitalidentity.controller.api.dto.EmploymentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestDataConfiguration {
	private List<EmploymentDTO> employments;
}
