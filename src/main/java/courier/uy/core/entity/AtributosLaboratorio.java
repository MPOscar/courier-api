package courier.uy.core.entity;

import java.util.HashSet;
import java.util.Set;

public class AtributosLaboratorio extends Entidad {

	private String contenidoNetoCantidad;

	private String contenidoNetoUnidad;

	private String contenidoNetoPorUsoCantidad;

	private String contenidoNetoPorUsoUnidad;

	private String formaFarmaceutica;

	private String formaDeAdministracion;

	private boolean contieneAzucar;

	private boolean contieneLactosa;

	private String prospecto;

	private String website;

	private Set<ComponenteActivo> componentesActivos = new HashSet<>();

	public AtributosLaboratorio(String contenidoNetoCantidad,
								String contenidoNetoUnidad,
								String contenidoNetoPorUsoCantidad,
								String contenidoNetoPorUsoUnidad,
								String formaFarmaceutica,
								String formaDeAdministracion,
								boolean contieneAzucar,
								boolean contieneLactosa,
								Set<ComponenteActivo> componentesActivos) {
		this.contenidoNetoCantidad = contenidoNetoCantidad;
		this.contenidoNetoUnidad = contenidoNetoUnidad;
		this.contenidoNetoPorUsoCantidad = contenidoNetoPorUsoCantidad;
		this.contenidoNetoPorUsoUnidad = contenidoNetoPorUsoUnidad;
		this.formaFarmaceutica = formaFarmaceutica;
		this.formaDeAdministracion = formaDeAdministracion;
		this.contieneAzucar = contieneAzucar;
		this.contieneLactosa = contieneLactosa;
		this.componentesActivos = componentesActivos;
	}

	public String getContenidoNetoCantidad() {
		return contenidoNetoCantidad;
	}

	public void setContenidoNetoCantidad(String contenidoNetoCantidad) {
		this.contenidoNetoCantidad = contenidoNetoCantidad;
	}

	public String getContenidoNetoUnidad() {
		return contenidoNetoUnidad;
	}

	public void setContenidoNetoUnidad(String contenidoNetoUnidad) {
		this.contenidoNetoUnidad = contenidoNetoUnidad;
	}

	public String getContenidoNetoPorUsoCantidad() {
		return contenidoNetoPorUsoCantidad;
	}

	public void setContenidoNetoPorUsoCantidad(String contenidoNetoPorUsoCantidad) {
		this.contenidoNetoPorUsoCantidad = contenidoNetoPorUsoCantidad;
	}

	public String getContenidoNetoPorUsoUnidad() {
		return contenidoNetoPorUsoUnidad;
	}

	public void setContenidoNetoPorUsoUnidad(String contenidoNetoPorUsoUnidad) {
		this.contenidoNetoPorUsoUnidad = contenidoNetoPorUsoUnidad;
	}

	public String getFormaFarmaceutica() {
		return formaFarmaceutica;
	}

	public void setFormaFarmaceutica(String formaFarmaceutica) {
		this.formaFarmaceutica = formaFarmaceutica;
	}

	public String getFormaDeAdministracion() {
		return formaDeAdministracion;
	}

	public void setFormaDeAdministracion(String formaDeAdministracion) {
		this.formaDeAdministracion = formaDeAdministracion;
	}

	public boolean getContieneAzucar() {
		return contieneAzucar;
	}

	public void setContieneAzucar(boolean contieneAzucar) {
		this.contieneAzucar = contieneAzucar;
	}

	public boolean getContieneLactosa() {
		return contieneLactosa;
	}

	public void setContieneLactosa(boolean contieneLactosa) {
		this.contieneLactosa = contieneLactosa;
	}

	public Set<ComponenteActivo> getComponentesActivos() {
		return componentesActivos;
	}

	public void setComponentesActivos(Set<ComponenteActivo> componentesActivos) {
		this.componentesActivos = componentesActivos;
	}

	public boolean isContieneAzucar() {
		return contieneAzucar;
	}

	public boolean isContieneLactosa() {
		return contieneLactosa;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AtributosLaboratorio other = (AtributosLaboratorio) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}
}
