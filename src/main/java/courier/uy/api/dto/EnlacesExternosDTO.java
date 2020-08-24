package courier.uy.api.dto;


public class EnlacesExternosDTO {

	private String prospecto;

	private String website;

	public EnlacesExternosDTO(String prospecto, String website) {
		this.prospecto = prospecto;
		this.website = website;
	}

	public String getProspecto() {
		return prospecto;
	}

	public void setProspecto(String prospecto) {
		this.prospecto = prospecto;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
}
