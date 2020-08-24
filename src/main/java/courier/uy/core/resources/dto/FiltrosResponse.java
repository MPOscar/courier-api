package courier.uy.core.resources.dto;

import java.util.Set;

public class FiltrosResponse {
	
	private Set<String> marcas;

	private Set<String> divisiones;

	private Set<String> lineas;


	public void setMarcas(Set<String> marcas) {
		this.marcas = marcas;
	}

	public Set<String> getMarcas() {
		return this.marcas;
	}

	public void setDivisiones(Set<String> divisiones) {
		this.divisiones = divisiones;
	}

	public Set<String> getDivisiones() {
		return this.divisiones;
	}

	public void setLineas(Set<String> lineas) {
		this.lineas = lineas;
	}

	public Set<String> getLineas() {
		return this.lineas;
	}
}
