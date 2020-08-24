package courier.uy.core.entity;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Ubicacion")
public class Ubicacion extends Entidad {

	private String codigo;

	private String descripcion;

	private String tipo;

	protected DateTime fechaAlta;

	protected DateTime fechaBaja;

	//ManyToOne
	@DBRef(lazy = true)
	private Company empresa;

	private String sempresa;

	public Ubicacion(String codigo, String descripcion, String tipo, DateTime fechaAlta, DateTime fechaBaja,
					  Company empresa) {
		super();
		this.codigo = codigo;
		this.descripcion = descripcion;
		this.tipo = tipo;
		this.fechaAlta = fechaAlta;
		this.fechaBaja = fechaBaja;
		this.empresa = empresa;
	}

	public Ubicacion(Ubicacion ubicacion) {
		super();
		this.oldId = ubicacion.getOldId();
		this.codigo = ubicacion.getCodigo();
		this.descripcion = ubicacion.getDescripcion();
		this.tipo = ubicacion.getTipo();
		this.fechaAlta = ubicacion.getFechaAlta();
		this.fechaBaja = ubicacion.getFechaBaja();
	}

	public Ubicacion() {
		super();
	}

	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public DateTime getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(DateTime fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	public DateTime getFechaBaja() {
		return this.fechaBaja;
	}

	public void setFechaBaja(DateTime fechaBaja) {
		this.fechaBaja = fechaBaja;
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

	public void update(Ubicacion ubicacion) {
		this.setCodigo(ubicacion.getCodigo());
		this.setDescripcion(ubicacion.getDescripcion());
		this.setTipo(ubicacion.getTipo());
		this.setFechaAlta(ubicacion.getFechaAlta());
		this.setFechaBaja(ubicacion.getFechaBaja());
		this.eliminado = false;
		this.setFechaEdicion();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ubicacion other = (Ubicacion) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}

}
