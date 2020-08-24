package courier.uy.api.dto;


public class ContenidoDTO {

	private String valor;

	private String unidad;

	public ContenidoDTO(String valor, String unidad) {
		this.valor = valor;
		this.unidad = unidad;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getUnidad() {
		return unidad;
	}

	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}
}
