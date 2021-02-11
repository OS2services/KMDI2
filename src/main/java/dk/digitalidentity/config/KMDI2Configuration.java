package dk.digitalidentity.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KMDI2Configuration {
	private boolean enabled = false;
	private String url = "";
	private String apiKey = "";
	
	public String getUrl() {
		if (url == null) {
			return url;
		}
		
		if (!url.endsWith("/")) {
			return url + "/";
		}
		
		return url;
	}
}
