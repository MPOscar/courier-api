package courier.uy.core.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Pallet")
public class Pallet extends Entidad {

	private String unidadesVenta;

	private String cajas;

	private String camadas;

	private String alto;

	private String ancho;

	private String profundidad;

	public Pallet() {
		super();
	}

	public Pallet(String palletAlto, String palletAncho, String palletProfundidad, String palletCajas,
				   String palletCamadas, String palletUnidadesVenta) {
		super();
		this.alto = palletAlto;
		this.ancho = palletAncho;
		this.profundidad = palletProfundidad;
		this.cajas = palletCajas;
		this.camadas = palletCamadas;
		this.unidadesVenta = palletUnidadesVenta;
	}

	public Pallet(Pallet pallet) {
		super();
		this.oldId = pallet.getOldId();
		this.alto = pallet.getAlto();
		this.ancho = pallet.getAncho();
		this.profundidad = pallet.getProfundidad();
		this.cajas = pallet.getCajas();
		this.camadas = pallet.getCamadas();
		this.unidadesVenta = pallet.getUnidadesVenta();
		this.fechaCreacion = pallet.getFechaCreacion();
		this.fechaEdicion = pallet.getFechaEdicion();
		this.eliminado = pallet.getEliminado();
	}

	public void setCajas(String cajas) {
		this.cajas = cajas;
	}

	public void setCamadas(String camadas) {
		this.camadas = camadas;
	}

	public void setUnidadesVenta(String unidadesVenta) {
		this.unidadesVenta = unidadesVenta;
	}

	public String getCajas() {
		return this.cajas;
	}

	public String getCamadas() {
		return this.camadas;
	}

	public String getUnidadesVenta() {
		return this.unidadesVenta;
	}

	public void setAlto(String alto) {
		this.alto = alto;
	}

	public void setAncho(String ancho) {
		this.ancho = ancho;
	}

	public void setProfundidad(String profundidad) {
		this.profundidad = profundidad;
	}

	public String getAlto() {
		return this.alto;
	}

	public String getAncho() {
		return this.ancho;
	}

	public String getProfundidad() {
		return this.profundidad;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pallet other = (Pallet) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}
}
