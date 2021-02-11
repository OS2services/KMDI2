package dk.digitalidentity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "configuration")
public class AppConfiguration {
	private String apiKey;
	private String[] allowedRoles = "Teacher,Pedagogue,Substitute,InstitutionManager,Management,TAP,Consultant".split(",");
	
	private KMDI2Configuration kmdi2 = new KMDI2Configuration();
	private TestDataConfiguration testData = new TestDataConfiguration();
}