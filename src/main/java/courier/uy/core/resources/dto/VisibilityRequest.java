package courier.uy.core.resources.dto;

import java.util.List;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Grupo;
import courier.uy.core.entity.Producto;

public class VisibilityRequest {

	private Boolean esPrivado = false;

	public Boolean getEsPrivado() {
		return esPrivado;
	}

	public void setEsPrivado(Boolean esPrivado) {
		this.esPrivado = esPrivado;
	}

	public Boolean getEsPublico() {
		return esPublico;
	}

	public void setEsPublico(Boolean esPublico) {
		this.esPublico = esPublico;
	}

	public Boolean getIsMasive() {
		return isMasive;
	}

	public void setIsMasive(Boolean isMasive) {
		this.isMasive = isMasive;
	}

	private Boolean esPublico = false;

	private Boolean isMasive = false;

	private List<Producto> productos;

	private List<Company> empresas;

	private List<Grupo> grupos;

	private boolean addVisibility;

	public void setSetProductos(List<Producto> products) {
		this.productos = products;
	}

	public List<Producto> getProductos() {
		return this.productos;
	}

	public void setEmpresas(List<Company> businesses) {
		this.empresas = businesses;
	}

	public List<Company> getEmpresas() {
		return this.empresas;
	}

	public void setGrupos(List<Grupo> groups) {
		this.grupos = groups;
	}

	public List<Grupo> getGrupos() {
		return this.grupos;
	}

	public void setAddVisibility(boolean addVisibility) {
		this.addVisibility = addVisibility;
	}

	public boolean getAddVisibility() {
		return this.addVisibility;
	}
}
