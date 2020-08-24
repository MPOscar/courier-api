package courier.uy.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import courier.uy.core.exceptions.PackException;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.ws.rs.core.Link;
import java.math.BigDecimal;

@Document(collection = "Empaque")
public class Empaque extends Entidad {

	private String gtin;

	private String cpp;

	public String getCpp() {
		return this.cpp;
	}

	public void setCpp(String cpp) {
		this.cpp = cpp;
	}

	private Integer nivel;

	private String descripcion;

	private String unidadMedida;

	private BigDecimal alto;

	private BigDecimal ancho;

	private BigDecimal profundidad;

	private BigDecimal pesoBruto;

	private BigDecimal cantidad;

	private String clasificacion;

	//ManyToOne
	@DBRef(lazy = true)
	private Company empresa;

	private String sempresa;

	//ManyToOne
	@DBRef(lazy = true)
	private Empaque padre;

	private String spadre;

	//ManyToOne
	@DBRef(lazy = true)
	private Presentacion presentacion;

	private String spresentacion;

	public Empaque() {
		super();
	};

	private static boolean isLong(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void isValid(String gtin, String nivel, String unidadMedida, String alto, String ancho, String profundidad,
						String pesoBruto, String cantidad, String cpp, String clasificacion) throws PackException {
		Boolean hasErrors = false;
		String errors = "Error en empaque: ";

		if (cpp.equals("") && gtin.equals("")) {
			hasErrors = true;
			errors += "Gtin y Cpp no pueden estar vacíos - ";
		}

		/*if (unidadMedida.equals("")) {
			hasErrors = true;
			errors += "La unidad de medida no puede estar vacía - ";
		}*/

		if (!isLong(gtin)) {
			hasErrors = true;
			errors += "El Gtin debe ser numérico - ";
		}

		if (!isLong(cantidad)) {
			hasErrors = true;
			errors += "La cantidad debe ser numérica - ";
		}
		/*if (!isLong(pesoBruto)) {
			hasErrors = true;
			errors += "El peso bruto debe ser numérico - ";
		}*/

		/*if (!isLong(alto)) {
			hasErrors = true;
			errors += "La altura debe ser numérica - ";
		}*/

		/*if (!isLong(ancho)) {
			hasErrors = true;
			errors += "El ancho debe ser numérico - ";
		}*/

		/*if (!isLong(profundidad)) {
			hasErrors = true;
			errors += "La profundidad debe ser numérica - ";
		}*/

		if (!isLong(nivel)) {
			hasErrors = true;
			errors += "El nivel debe ser numérico - ";
		}
		if (!clasificacion.toLowerCase().equals("inner pack") && !clasificacion.toLowerCase().equals("display")
				&& !clasificacion.toLowerCase().equals("caja")) {
			hasErrors = true;
			errors += "La clasificación puede ser 'Inner Pack', 'Display' o 'Caja'";
		}

		if (hasErrors)
			throw new PackException(errors);
	}

	public void copyForExcel(String gtin, String nivel, String unidadMedida, String alto, String ancho,
							 String profundidad, String pesoBruto, String cantidad, String cpp, String clasificacion) {
		this.gtin = gtin;
		this.cpp = cpp;
		this.unidadMedida = unidadMedida;
		if (isLong(nivel)) {
			this.nivel = Integer.parseInt(nivel);
		}

		if (isLong(cantidad)) {
			this.cantidad = BigDecimal.valueOf(Double.parseDouble(cantidad));
		}

		if (isLong(alto)) {
			this.alto = BigDecimal.valueOf(Double.parseDouble(alto));
		}
		if (isLong(ancho)) {
			this.ancho = BigDecimal.valueOf(Double.parseDouble(ancho));
		}
		if (isLong(profundidad)) {
			this.profundidad = BigDecimal.valueOf(Double.parseDouble(profundidad));
		}
		if (isLong(pesoBruto)) {
			this.pesoBruto = BigDecimal.valueOf(Double.parseDouble(pesoBruto));
		}
		if (isLong(cantidad)) {
			this.cantidad = BigDecimal.valueOf(Double.parseDouble(cantidad));
		}
		this.clasificacion = clasificacion;

	}

	public Empaque(String gtin, Integer nivel, String descripcion, String unidadMedida, BigDecimal alto,
					BigDecimal ancho, BigDecimal profundidad, BigDecimal pesoBruto, BigDecimal cantidad,
					Presentacion presentacion, String cpp, String clasificacion) {
		super();
		this.gtin = gtin;
		this.nivel = nivel;
		this.descripcion = descripcion;
		this.cantidad = cantidad;
		this.unidadMedida = unidadMedida;
		this.alto = alto;
		this.ancho = ancho;
		this.profundidad = profundidad;
		this.pesoBruto = pesoBruto;
		this.cantidad = cantidad;
		this.presentacion = presentacion;
		this.cpp = cpp;
		this.clasificacion = clasificacion;
	}

	public void copyForExcelWithParent(String gtin, String nivel, String descripcion, String unidadMedida, String alto,
									   String ancho, String profundidad, String pesoBruto, String cantidad, Empaque padre, String cpp,
									   String clasificacion) {
		this.gtin = gtin;
		this.cpp = cpp;
		this.descripcion = descripcion;
		this.unidadMedida = unidadMedida;
		if (isLong(nivel)) {
			this.nivel = Integer.parseInt(nivel);
		}

		if (isLong(cantidad)) {
			this.cantidad = BigDecimal.valueOf(Double.parseDouble(cantidad));
		}

		if (isLong(alto)) {
			this.alto = BigDecimal.valueOf(Double.parseDouble(alto));
		}
		if (isLong(ancho)) {
			this.ancho = BigDecimal.valueOf(Double.parseDouble(ancho));
		}
		if (isLong(profundidad)) {
			this.profundidad = BigDecimal.valueOf(Double.parseDouble(profundidad));
		}
		if (isLong(pesoBruto)) {
			this.pesoBruto = BigDecimal.valueOf(Double.parseDouble(pesoBruto));
		}
		if (isLong(cantidad)) {
			this.cantidad = BigDecimal.valueOf(Double.parseDouble(cantidad));
		}
		this.padre = padre;
		this.clasificacion = clasificacion;
	}

	public Empaque(String gtin, Integer nivel, String descripcion, String unidadMedida, BigDecimal alto,
					BigDecimal ancho, BigDecimal profundidad, BigDecimal pesoBruto, BigDecimal cantidad, Empaque padre,
					Presentacion presentacion, String cpp, String clasificacion) {
		super();
		this.gtin = gtin;
		this.nivel = nivel;
		this.descripcion = descripcion;
		this.cantidad = cantidad;
		this.unidadMedida = unidadMedida;
		this.alto = alto;
		this.ancho = ancho;
		this.profundidad = profundidad;
		this.pesoBruto = pesoBruto;
		this.cantidad = cantidad;
		this.presentacion = presentacion;
		this.padre = padre;
		this.cpp = cpp;
		this.clasificacion = clasificacion;
	}

	public Empaque(Empaque empaque) {
		super();
		this.oldId = empaque.getOldId();
		this.cpp = empaque.getCpp();
		this.cantidad = empaque.getCantidad();
		this.clasificacion = empaque.getClasificacion();
		this.alto = empaque.getAlto();
		this.ancho = empaque.getAncho();
		this.descripcion = empaque.getDescripcion();
		this.pesoBruto = empaque.getPesoBruto();
		this.gtin = empaque.getGtin();
		this.nivel = empaque.getNivel();
		this.profundidad = empaque.getProfundidad();
		this.unidadMedida = empaque.getUnidadMedida();
		this.fechaCreacion = empaque.getFechaCreacion();
		this.fechaEdicion = empaque.getFechaEdicion();
		this.eliminado = empaque.getEliminado();
		this.gtin = empaque.getGtin();
	};


	public String getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}

	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	public Integer getNivel() {
		return nivel;
	}

	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	public String getUnidadMedida() {
		return unidadMedida;
	}

	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}

	public BigDecimal getAlto() {
		return alto;
	}

	public void setAlto(BigDecimal alto) {
		this.alto = alto;
	}

	public BigDecimal getAncho() {
		return ancho;
	}

	public void setAncho(BigDecimal ancho) {
		this.ancho = ancho;
	}

	public BigDecimal getProfundidad() {
		return profundidad;
	}

	public void setProfundidad(BigDecimal profundidad) {
		this.profundidad = profundidad;
	}

	public BigDecimal getPesoBruto() {
		return pesoBruto;
	}

	public void setPesoBruto(BigDecimal pesoBruto) {
		this.pesoBruto = pesoBruto;
	}

	@JsonIgnore
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

	public Empaque getPadre() {
		return padre;
	}

	public void setPadre(Empaque padre) {
		this.padre = padre;
	}

	public String getSpadre() {
		return spadre;
	}

	public void setSpadre(String spadre) {
		this.spadre = spadre;
	}

	public Presentacion getPresentacion() {
		return presentacion;
	}

	public void setPresentacion(Presentacion presentacion) {
		this.presentacion = presentacion;
	}

	public String getSpresentacion() {
		return spresentacion;
	}

	public void setSpresentacion(String spresentacion) {
		this.spresentacion = spresentacion;
	}

	@Override
	public void inicializarLinkData() {
		Link self = Link.fromUri("/empaques/" + this.id).rel("self").title("Obtener Empaque").type("GET").build();
		//this.links.add(self);
	}

	//@ValidationMethod(message = "Descripcion may not be Coda")
	@JsonIgnore
	public boolean isNotCoda() {
		return !"Coda".equals(descripcion);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Empaque other = (Empaque) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}

}