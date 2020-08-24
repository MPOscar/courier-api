package courier.uy.core.resources.dto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Link;

import courier.uy.core.utils.serializer.CustomDateTimeSerializer;
import courier.uy.core.utils.serializer.FechaPedidoCustomDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

import courier.uy.core.entity.Categoria;
import courier.uy.core.entity.Empaque;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Grupo;
import courier.uy.core.entity.Pallet;
import courier.uy.core.entity.Presentacion;

public class ProductoAccionesResponse {

	Set<Link> links = new HashSet<Link>();

	protected String id;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	protected DateTime fechaCreacion;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	protected DateTime fechaEdicion;

	protected Boolean eliminado;

	private String cpp;

	private Pallet pallet;

	private String gtin;

	private String gtinPresentacion;

	private String descripcion;

	private String mercadoObjetivo;

	private String unidadMedidaPesoBruto;

	private Integer nivelMinimoVenta;

	private String marca;

	private BigDecimal contenidoNeto;

	private String unidadMedida;

	private String paisOrigen;

	private String foto;

	private BigDecimal alto;

	private BigDecimal ancho;

	private BigDecimal profundidad;

	private BigDecimal pesoBruto;

	private Boolean esPromo;

	private Boolean esPrivado;

	private Boolean esPublico;

	@JsonSerialize(using = FechaPedidoCustomDateTimeSerializer.class)
	protected DateTime suspendidoDesde;

	@JsonSerialize(using = FechaPedidoCustomDateTimeSerializer.class)
	protected DateTime suspendidoHasta;

	private Company empresa;

	private Categoria categoria;

	private Presentacion presentacion;

	private Set<Empaque> empaques = new HashSet<Empaque>();

	private Set<Company> empresasConVisibilidad = new HashSet<Company>();

	private Set<Grupo> gruposConVisibilidad = new HashSet<Grupo>();
	
	public ProductoAccionesResponse() {
	}

	public ProductoAccionesResponse(Set<Link> links, String id, DateTime fechaCreacion, DateTime fechaEdicion,
                                    Boolean eliminado, String cpp, Pallet pallet, String gtin, String gtinPresentacion, String descripcion,
                                    String mercadoObjetivo, String unidadMedidaPesoBruto, Integer nivelMinimoVenta, String marca,
                                    BigDecimal contenidoNeto, String unidadMedida, String paisOrigen, String foto, BigDecimal alto,
                                    BigDecimal ancho, BigDecimal profundidad, BigDecimal pesoBruto, Boolean esPromo, Boolean esPrivado, Boolean esPublico, DateTime suspendidoDesde, DateTime suspendidoHasta, Company empresa, Categoria categoria,
                                    Presentacion presentacion, Set<Empaque> empaques, Set<Company> empresasConVisibilidad, Set<Grupo> gruposConVisibilidad) {
		this.links = links;
		this.id = id;
		this.fechaCreacion = fechaCreacion;
		this.fechaEdicion = fechaEdicion;
		this.eliminado = eliminado;
		this.cpp = cpp;
		this.pallet = pallet;
		this.gtin = gtin;
		this.gtinPresentacion = gtinPresentacion;
		this.descripcion = descripcion;
		this.mercadoObjetivo = mercadoObjetivo;
		this.unidadMedidaPesoBruto = unidadMedidaPesoBruto;
		this.nivelMinimoVenta = nivelMinimoVenta;
		this.marca = marca;
		this.contenidoNeto = contenidoNeto;
		this.unidadMedida = unidadMedida;
		this.paisOrigen = paisOrigen;
		this.foto = foto;
		this.alto = alto;
		this.ancho = ancho;
		this.profundidad = profundidad;
		this.pesoBruto = pesoBruto;
		this.esPromo = esPromo;
		this.esPrivado = esPrivado;
		this.esPublico	 = esPublico;
		this.suspendidoDesde = suspendidoDesde;
		this.suspendidoHasta = suspendidoHasta;
		this.empresa = empresa;
		this.categoria = categoria;
		this.presentacion = presentacion;
		this.empaques = empaques;
		this.empresasConVisibilidad = empresasConVisibilidad;
		this.gruposConVisibilidad = gruposConVisibilidad;
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

	public Boolean isEliminado() {
		return this.eliminado;
	}

	public Boolean getEliminado() {
		return this.eliminado;
	}

	public void setEliminado(Boolean eliminado) {
		this.eliminado = eliminado;
	}

	public String getCpp() {
		return this.cpp;
	}

	public void setCpp(String cpp) {
		this.cpp = cpp;
	}

	public Pallet getPallet() {
		return this.pallet;
	}

	public void setPallet(Pallet pallet) {
		this.pallet = pallet;
	}

	public String getGtin() {
		return this.gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	public String getGtinPresentacion() {
		return this.gtinPresentacion;
	}

	public void setGtinPresentacion(String gtinPresentacion) {
		this.gtinPresentacion = gtinPresentacion;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getMercadoObjetivo() {
		return this.mercadoObjetivo;
	}

	public void setMercadoObjetivo(String mercadoObjetivo) {
		this.mercadoObjetivo = mercadoObjetivo;
	}

	public String getUnidadMedidaPesoBruto() {
		return this.unidadMedidaPesoBruto;
	}

	public void setUnidadMedidaPesoBruto(String unidadMedidaPesoBruto) {
		this.unidadMedidaPesoBruto = unidadMedidaPesoBruto;
	}

	public Integer getNivelMinimoVenta() {
		return this.nivelMinimoVenta;
	}

	public void setNivelMinimoVenta(Integer nivelMinimoVenta) {
		this.nivelMinimoVenta = nivelMinimoVenta;
	}

	public String getMarca() {
		return this.marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public BigDecimal getContenidoNeto() {
		return this.contenidoNeto;
	}

	public void setContenidoNeto(BigDecimal contenidoNeto) {
		this.contenidoNeto = contenidoNeto;
	}

	public String getUnidadMedida() {
		return this.unidadMedida;
	}

	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}

	public String getPaisOrigen() {
		return this.paisOrigen;
	}

	public void setPaisOrigen(String paisOrigen) {
		this.paisOrigen = paisOrigen;
	}

	public String getFoto() {
		return this.foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public BigDecimal getAlto() {
		return this.alto;
	}

	public void setAlto(BigDecimal alto) {
		this.alto = alto;
	}

	public BigDecimal getAncho() {
		return this.ancho;
	}

	public void setAncho(BigDecimal ancho) {
		this.ancho = ancho;
	}

	public BigDecimal getProfundidad() {
		return this.profundidad;
	}

	public void setProfundidad(BigDecimal profundidad) {
		this.profundidad = profundidad;
	}

	public BigDecimal getPesoBruto() {
		return this.pesoBruto;
	}

	public void setPesoBruto(BigDecimal pesoBruto) {
		this.pesoBruto = pesoBruto;
	}

	public Boolean isEsPromo() {
		return this.esPromo;
	}

	public Boolean getEsPromo() {
		return this.esPromo;
	}

	public void setEsPromo(Boolean esPromo) {
		this.esPromo = esPromo;
	}

	public Boolean isEsPrivado() {
		return this.esPrivado;
	}

	public Boolean getEsPrivado() {
		return this.esPrivado;
	}

	public void setEsPrivado(Boolean esPrivado) {
		this.esPrivado = esPrivado;
	}

	public Boolean isEsPublico() {
		return this.esPublico;
	}

	public Boolean getEsPublico() {
		return this.esPublico;
	}

	public void setEsPublico(Boolean esPublico) {
		this.esPublico = esPublico;
	}

	public DateTime getSuspendidoDesde() {
		return this.suspendidoDesde;
	}

	public void setSuspendidoDesde(DateTime suspendidoDesde) {
		this.suspendidoDesde = suspendidoDesde;
	}

	public DateTime getSuspendidoHasta() {
		return this.suspendidoHasta;
	}

	public void setSuspendidoHasta(DateTime suspendidoHasta) {
		this.suspendidoHasta = suspendidoHasta;
	}

	public Company getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(Company empresa) {
		this.empresa = empresa;
	}

	public Categoria getCategoria() {
		return this.categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Presentacion getPresentacion() {
		return this.presentacion;
	}

	public void setPresentacion(Presentacion presentacion) {
		this.presentacion = presentacion;
	}

	public Set<Empaque> getEmpaques() {
		return this.empaques;
	}

	public void setEmpaques(Set<Empaque> empaques) {
		this.empaques = empaques;
	}

	public ProductoAccionesResponse links(Set<Link> links) {
		this.links = links;
		return this;
	}

	public ProductoAccionesResponse id(String id) {
		this.id = id;
		return this;
	}

	public ProductoAccionesResponse fechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
		return this;
	}

	public ProductoAccionesResponse fechaEdicion(DateTime fechaEdicion) {
		this.fechaEdicion = fechaEdicion;
		return this;
	}

	public ProductoAccionesResponse eliminado(Boolean eliminado) {
		this.eliminado = eliminado;
		return this;
	}

	public ProductoAccionesResponse cpp(String cpp) {
		this.cpp = cpp;
		return this;
	}

	public ProductoAccionesResponse pallet(Pallet pallet) {
		this.pallet = pallet;
		return this;
	}

	public ProductoAccionesResponse gtin(String gtin) {
		this.gtin = gtin;
		return this;
	}

	public ProductoAccionesResponse gtinPresentacion(String gtinPresentacion) {
		this.gtinPresentacion = gtinPresentacion;
		return this;
	}

	public ProductoAccionesResponse descripcion(String descripcion) {
		this.descripcion = descripcion;
		return this;
	}

	public ProductoAccionesResponse mercadoObjetivo(String mercadoObjetivo) {
		this.mercadoObjetivo = mercadoObjetivo;
		return this;
	}

	public ProductoAccionesResponse unidadMedidaPesoBruto(String unidadMedidaPesoBruto) {
		this.unidadMedidaPesoBruto = unidadMedidaPesoBruto;
		return this;
	}

	public ProductoAccionesResponse nivelMinimoVenta(Integer nivelMinimoVenta) {
		this.nivelMinimoVenta = nivelMinimoVenta;
		return this;
	}

	public ProductoAccionesResponse marca(String marca) {
		this.marca = marca;
		return this;
	}

	public ProductoAccionesResponse contenidoNeto(BigDecimal contenidoNeto) {
		this.contenidoNeto = contenidoNeto;
		return this;
	}

	public ProductoAccionesResponse unidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
		return this;
	}

	public ProductoAccionesResponse paisOrigen(String paisOrigen) {
		this.paisOrigen = paisOrigen;
		return this;
	}

	public ProductoAccionesResponse foto(String foto) {
		this.foto = foto;
		return this;
	}

	public ProductoAccionesResponse alto(BigDecimal alto) {
		this.alto = alto;
		return this;
	}

	public ProductoAccionesResponse ancho(BigDecimal ancho) {
		this.ancho = ancho;
		return this;
	}

	public ProductoAccionesResponse profundidad(BigDecimal profundidad) {
		this.profundidad = profundidad;
		return this;
	}

	public ProductoAccionesResponse pesoBruto(BigDecimal pesoBruto) {
		this.pesoBruto = pesoBruto;
		return this;
	}

	public ProductoAccionesResponse esPromo(Boolean esPromo) {
		this.esPromo = esPromo;
		return this;
	}

	public ProductoAccionesResponse esPrivado(Boolean esPrivado) {
		this.esPrivado = esPrivado;
		return this;
	}

	public ProductoAccionesResponse suspendidoDesde(DateTime suspendidoDesde) {
		this.suspendidoDesde = suspendidoDesde;
		return this;
	}

	public ProductoAccionesResponse suspendidoHasta(DateTime suspendidoHasta) {
		this.suspendidoHasta = suspendidoHasta;
		return this;
	}

	public ProductoAccionesResponse empresa(Company empresa) {
		this.empresa = empresa;
		return this;
	}

	public ProductoAccionesResponse categoria(Categoria categoria) {
		this.categoria = categoria;
		return this;
	}

	public ProductoAccionesResponse presentacion(Presentacion presentacion) {
		this.presentacion = presentacion;
		return this;
	}

	public ProductoAccionesResponse empaques(Set<Empaque> empaques) {
		this.empaques = empaques;
		return this;
	}

	public void setEmpresasConVisibilidad(Set<Company> visibilidad) {
		this.empresasConVisibilidad = visibilidad;
	}

	@JsonIgnore
	public Set<Company> getEmpresasConVisibilidad() {
		return this.empresasConVisibilidad;
	}

	public void setGruposConVisibilidad(Set<Grupo> grupos) {
		this.gruposConVisibilidad = grupos;
	}

	@JsonIgnore
	public Set<Grupo> getGruposConVisibilidad() {
		return this.gruposConVisibilidad;
	}

	@Override
	public String toString() {
		return "{" + " links='" + getLinks() + "'" + ", id='" + getId() + "'" + ", fechaCreacion='" + getFechaCreacion()
				+ "'" + ", fechaEdicion='" + getFechaEdicion() + "'" + ", eliminado='" + isEliminado() + "'" + ", cpp='"
				+ getCpp() + "'" + ", pallet='" + getPallet() + "'" + ", gtin='" + getGtin() + "'"
				+ ", gtinPresentacion='" + getGtinPresentacion() + "'" + ", descripcion='" + getDescripcion() + "'"
				+ ", mercadoObjetivo='" + getMercadoObjetivo() + "'" + ", unidadMedidaPesoBruto='"
				+ getUnidadMedidaPesoBruto() + "'" + ", nivelMinimoVenta='" + getNivelMinimoVenta() + "'" + ", marca='"
				+ getMarca() + "'" + ", contenidoNeto='" + getContenidoNeto() + "'" + ", unidadMedida='"
				+ getUnidadMedida() + "'" + ", paisOrigen='" + getPaisOrigen() + "'" + ", foto='" + getFoto() + "'"
				+ ", alto='" + getAlto() + "'" + ", ancho='" + getAncho() + "'" + ", profundidad='" + getProfundidad()
				+ "'" + ", pesoBruto='" + getPesoBruto() + "'" + ", esPromo='" + isEsPromo() + "'" + ", esPrivado='"
				+ isEsPrivado() + "'" + ", suspendidoDesde='" + getSuspendidoDesde() + "'" + ", suspendidoHasta='"
				+ getSuspendidoHasta() + "'" + ", empresa='" + getEmpresa() + "'" + ", categoria='" + getCategoria()
				+ "'" + ", presentacion='" + getPresentacion() + "'" + ", empaques='" + getEmpaques() + "'" + "}";
	}

}