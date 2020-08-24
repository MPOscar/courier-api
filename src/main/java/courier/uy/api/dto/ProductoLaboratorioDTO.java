package courier.uy.api.dto;

import courier.uy.core.entity.ComponenteActivo;
import courier.uy.core.entity.Producto;

import java.util.ArrayList;
import java.util.List;


public class ProductoLaboratorioDTO {

	private String tipo;

	private AtributosBasicosDTO atributosBasicos;

	private String formaFarmaceutica;

	private String viaAdministracion;

	private List<PrincipioActivoDTO> principioActivo = new ArrayList<>();

	private ContenidoDTO contenidoPorUso;

	private AlertasYAvisosDTO alertasyAvisos;

	private List<KitPromocionalDTO> kitPromocional = new ArrayList<>();

	private EnlacesExternosDTO enlacesExternos;

	public ProductoLaboratorioDTO() {
	}

	public ProductoLaboratorioDTO(Producto producto) {
		this.tipo = producto.getTipo();
		this.atributosBasicos = new AtributosBasicosDTO(producto);
		this.formaFarmaceutica = producto.getAtributosLaboratorio().getFormaFarmaceutica();
		this.viaAdministracion = producto.getAtributosLaboratorio().getFormaDeAdministracion();
		for (ComponenteActivo componenteActivo: producto.getAtributosLaboratorio().getComponentesActivos()) {
			PrincipioActivoDTO principioActivoDTO = new PrincipioActivoDTO(componenteActivo);
			this.principioActivo.add(principioActivoDTO);
		}
		this.contenidoPorUso = new ContenidoDTO(producto.getAtributosLaboratorio().getContenidoNetoPorUsoCantidad(), producto.getAtributosLaboratorio().getContenidoNetoPorUsoUnidad());
		this.alertasyAvisos = new AlertasYAvisosDTO(producto.getAtributosLaboratorio().isContieneAzucar(), producto.getAtributosLaboratorio().isContieneLactosa());
		if(producto.getKitPromocional() != null){
			for (String gtin: producto.getKitPromocional().getSproductos()) {
				KitPromocionalDTO kitPromocionalDTO = new KitPromocionalDTO(gtin);
				this.kitPromocional.add(kitPromocionalDTO);
			}
		}
		this.enlacesExternos = new EnlacesExternosDTO(producto.getAtributosLaboratorio().getProspecto(), producto.getAtributosLaboratorio().getWebsite());
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public AtributosBasicosDTO getAtributosBasicos() {
		return atributosBasicos;
	}

	public void setAtributosBasicos(AtributosBasicosDTO atributosBasicos) {
		this.atributosBasicos = atributosBasicos;
	}

	public String getFormaFarmaceutica() {
		return formaFarmaceutica;
	}

	public void setFormaFarmaceutica(String formaFarmaceutica) {
		this.formaFarmaceutica = formaFarmaceutica;
	}

	public String getViaAdministracion() {
		return viaAdministracion;
	}

	public void setViaAdministracion(String viaAdministracion) {
		this.viaAdministracion = viaAdministracion;
	}

	public List<PrincipioActivoDTO> getPrincipioActivo() {
		return principioActivo;
	}

	public void setPrincipioActivo(List<PrincipioActivoDTO> principioActivo) {
		this.principioActivo = principioActivo;
	}

	public ContenidoDTO getContenidoPorUso() {
		return contenidoPorUso;
	}

	public void setContenidoPorUso(ContenidoDTO contenidoPorUso) {
		this.contenidoPorUso = contenidoPorUso;
	}

	public AlertasYAvisosDTO getAlertasyAvisos() {
		return alertasyAvisos;
	}

	public void setAlertasyAvisos(AlertasYAvisosDTO alertasyAvisos) {
		this.alertasyAvisos = alertasyAvisos;
	}


	public List<KitPromocionalDTO> getKitPromocional() {
		return kitPromocional;
	}

	public void setKitPromocional(List<KitPromocionalDTO> kitPromocional) {
		this.kitPromocional = kitPromocional;
	}

	public EnlacesExternosDTO getEnlacesExternos() {
		return enlacesExternos;
	}

	public void setEnlacesExternos(EnlacesExternosDTO enlacesExternos) {
		this.enlacesExternos = enlacesExternos;
	}
}
