package courier.uy.core.resources.dto;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Link;

import courier.uy.core.utils.serializer.CustomDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

public class PalletResponse {
	Set<Link> links = new HashSet<Link>();

	protected String id;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	protected DateTime fechaCreacion;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	protected DateTime fechaEdicion;

	private String unidadesVenta;

	private String cajas;

	private String camadas;

	private String alto;

	private String ancho;

	private String profundidad;

	public PalletResponse() {
	}

	public PalletResponse(Set<Link> links, String id, DateTime fechaCreacion, DateTime fechaEdicion, String unidadesVenta,
			String cajas, String camadas, String alto, String ancho, String profundidad) {
		this.links = links;
		this.id = id;
		this.fechaCreacion = fechaCreacion;
		this.fechaEdicion = fechaEdicion;
		this.unidadesVenta = unidadesVenta;
		this.cajas = cajas;
		this.camadas = camadas;
		this.alto = alto;
		this.ancho = ancho;
		this.profundidad = profundidad;
	}

	public Set<Link> getLinks() {
		return this.links;
	}

	public void setLinks(Set<Link> links) {
		this.links = links;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DateTime getFechaCreacion() {
		return this.fechaCreacion;
	}

	public void setFechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public DateTime getFechaEdicion() {
		return this.fechaEdicion;
	}

	public void setFechaEdicion(DateTime fechaEdicion) {
		this.fechaEdicion = fechaEdicion;
	}

	public String getUnidadesVenta() {
		return this.unidadesVenta;
	}

	public void setUnidadesVenta(String unidadesVenta) {
		this.unidadesVenta = unidadesVenta;
	}

	public String getCajas() {
		return this.cajas;
	}

	public void setCajas(String cajas) {
		this.cajas = cajas;
	}

	public String getCamadas() {
		return this.camadas;
	}

	public void setCamadas(String camadas) {
		this.camadas = camadas;
	}

	public String getAlto() {
		return this.alto;
	}

	public void setAlto(String alto) {
		this.alto = alto;
	}

	public String getAncho() {
		return this.ancho;
	}

	public void setAncho(String ancho) {
		this.ancho = ancho;
	}

	public String getProfundidad() {
		return this.profundidad;
	}

	public void setProfundidad(String profundidad) {
		this.profundidad = profundidad;
	}

	public PalletResponse links(Set<Link> links) {
		this.links = links;
		return this;
	}

	public PalletResponse id(String id) {
		this.id = id;
		return this;
	}

	public PalletResponse fechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
		return this;
	}

	public PalletResponse fechaEdicion(DateTime fechaEdicion) {
		this.fechaEdicion = fechaEdicion;
		return this;
	}

	public PalletResponse unidadesVenta(String unidadesVenta) {
		this.unidadesVenta = unidadesVenta;
		return this;
	}

	public PalletResponse cajas(String cajas) {
		this.cajas = cajas;
		return this;
	}

	public PalletResponse camadas(String camadas) {
		this.camadas = camadas;
		return this;
	}

	public PalletResponse alto(String alto) {
		this.alto = alto;
		return this;
	}

	public PalletResponse ancho(String ancho) {
		this.ancho = ancho;
		return this;
	}

	public PalletResponse profundidad(String profundidad) {
		this.profundidad = profundidad;
		return this;
	}

	@Override
	public String toString() {
		return "{" + " links='" + getLinks() + "'" + ", id='" + getId() + "'" + ", fechaCreacion='" + getFechaCreacion()
				+ "'" + ", fechaEdicion='" + getFechaEdicion() + "'" + ", unidadesVenta='" + getUnidadesVenta() + "'"
				+ ", cajas='" + getCajas() + "'" + ", camadas='" + getCamadas() + "'" + ", alto='" + getAlto() + "'"
				+ ", ancho='" + getAncho() + "'" + ", profundidad='" + getProfundidad() + "'" + "}";
	}

}