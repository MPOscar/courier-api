package courier.uy.core.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import courier.uy.core.utils.serializer.CustomDateTimeSerializer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ProductoAccion")
@CompoundIndexes({
		@CompoundIndex(name = "producto_empresa_accion", def = "{ 'productoId': 1 }, { 'empresa': 1 }", unique = true),
})
public class ProductoAccion extends Entidad {

	private String accion;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private DateTime fechaRecibido;

	private String proveedorId;

	private String productoId;

	private long proveedorRUT;

	private long empresaRUT;

	private String empresaId;

	protected Boolean recibido;

	@DBRef(lazy = true)
	private Company empresa;

	private String sempresa;

	@DBRef(lazy = true)
	private Company proveedor;

	private String sproveedor;

	@DBRef(lazy = true)
	private Producto producto;

	private String sproducto;

	public ProductoAccion() {
		super();
		this.recibido = false;
	}

	public ProductoAccion(ProductoAccion productoAccion) {
		super();
		this.oldId = productoAccion.getOldId();
		this.proveedorRUT = productoAccion.getProveedorRUT();
		this.empresaRUT = productoAccion.getEmpresaRUT();
		this.accion = productoAccion.getAccion();
		this.fechaRecibido = productoAccion.getFechaRecibido();
		this.recibido = productoAccion.getRecibido();
		this.fechaCreacion = productoAccion.getFechaCreacion();
		this.fechaEdicion = productoAccion.getFechaEdicion();
		this.eliminado = productoAccion.getEliminado();

	}

	public void setProveedorId(String idProveedor) {
		this.proveedorId = idProveedor;
	}

	public String getProveedorId() {
		return this.proveedorId;
	}

	public void setEmpresaId(String idEmpresa) {
		this.empresaId = idEmpresa;
	}

	public String getEmpresaId() {
		return this.empresaId;
	}

	public void setProductoId(String idProducto) {
		this.productoId = idProducto;
	}

	public String getProductoId() {
		return this.productoId;
	}

	public String getAccion() {
		return this.accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public void setRecibido() {
		this.recibido = true;
	}

	public Boolean getRecibido() {
		return this.recibido;
	}

	public DateTime getFechaRecibido() {
		return this.fechaRecibido;
	}

	public void setFechaRecibido() {
		DateTimeZone uruguay = DateTimeZone.forID("America/Argentina/Buenos_Aires");
		this.fechaRecibido = DateTime.now().withZone(uruguay);
		this.setRecibido();
	}

	public void setFechaRecibido(DateTime date) {
		this.fechaRecibido = date;
	}

	public long getProveedorRUT() {
		return this.proveedorRUT;
	}

	public void setProveedorRUT(long proveedorRUT) {
		this.proveedorRUT = proveedorRUT;
	}

	public long getEmpresaRUT() {
		return this.empresaRUT;
	}

	public void setEmpresaRUT(long empresaRUT) {
		this.empresaRUT = empresaRUT;
	}

	public void setRecibido(Boolean recibido) {
		this.recibido = recibido;
	}

	public Company getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Company empresa) {
		this.empresa = empresa;
	}

	public String getSempresa() {
		return sempresa;
	}

	public void setSempresa(String sempresa) {
		this.sempresa = sempresa;
	}

	public Company getProveedor() {
		return proveedor;
	}

	public void setProveedor(Company proveedor) {
		this.proveedor = proveedor;
	}

	public String getSproveedor() {
		return sproveedor;
	}

	public void setSproveedor(String sproveedor) {
		this.sproveedor = sproveedor;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public String getSproducto() {
		return sproducto;
	}

	public void setSproducto(String sproducto) {
		this.sproducto = sproducto;
	}
}