package courier.uy.api.dto;


public class KitPromocionalDTO {

	private String gtin;

	public KitPromocionalDTO(String gtin) {
		this.gtin = gtin;
	}

	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}
}
