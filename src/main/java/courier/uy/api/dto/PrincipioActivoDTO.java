package courier.uy.api.dto;

import courier.uy.core.entity.ComponenteActivo;

public class PrincipioActivoDTO {

	private String nombre;

	private ContenidoDTO concentracion;

	private ContenidoDTO enMedio;

	public PrincipioActivoDTO(ComponenteActivo componenteActivo) {
		this.nombre = componenteActivo.getNombre();
		concentracion = new ContenidoDTO(componenteActivo.getCantidad(), componenteActivo.getUnidad());
		enMedio = new ContenidoDTO(componenteActivo.getEnCantidad(), componenteActivo.getEnUnidad());
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public ContenidoDTO getConcentracion() {
		return concentracion;
	}

	public void setConcentracion(ContenidoDTO concentracion) {
		this.concentracion = concentracion;
	}

	public ContenidoDTO getEnMedio() {
		return enMedio;
	}

	public void setEnMedio(ContenidoDTO enMedio) {
		this.enMedio = enMedio;
	}
}
