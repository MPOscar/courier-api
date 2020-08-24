package courier.uy.api.dto;


public class AlertasYAvisosDTO {

	private boolean contieneAzucar;

	private boolean contieneLactosa;

	public AlertasYAvisosDTO(boolean contieneAzucar, boolean contieneLactosa) {
		this.contieneAzucar = contieneAzucar;
		this.contieneLactosa = contieneLactosa;
	}

	public boolean isContieneAzucar() {
		return contieneAzucar;
	}

	public void setContieneAzucar(boolean contieneAzucar) {
		this.contieneAzucar = contieneAzucar;
	}

	public boolean isContieneLactosa() {
		return contieneLactosa;
	}

	public void setContieneLactosa(boolean contieneLactosa) {
		this.contieneLactosa = contieneLactosa;
	}
}
