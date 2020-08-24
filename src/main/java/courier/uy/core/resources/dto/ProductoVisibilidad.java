package courier.uy.core.resources.dto;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Grupo;

import java.util.HashSet;
import java.util.Set;

public class ProductoVisibilidad {

	private Set<Company> empresasConVisibilidad = new HashSet<Company>();

	private Set<Grupo> gruposConVisibilidad = new HashSet<Grupo>();

	public ProductoVisibilidad() {
	}

	public ProductoVisibilidad(Set<Company> empresasConVisibilidad, Set<Grupo> gruposConVisibilidad) {

		this.empresasConVisibilidad = empresasConVisibilidad;
		this.gruposConVisibilidad = gruposConVisibilidad;
	}

	public void setEmpresasConVisibilidad(Set<Company> visibilidad) {
		this.empresasConVisibilidad = visibilidad;
	}

	public Set<Company> getEmpresasConVisibilidad() {
		return this.empresasConVisibilidad;
	}

	public void setGruposConVisibilidad(Set<Grupo> grupos) {
		this.gruposConVisibilidad = grupos;
	}

	public Set<Grupo> getGruposConVisibilidad() {
		return this.gruposConVisibilidad;
	}

}