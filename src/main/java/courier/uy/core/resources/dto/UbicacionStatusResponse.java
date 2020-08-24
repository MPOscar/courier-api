package courier.uy.core.resources.dto;

import courier.uy.core.entity.Ubicacion;

public class UbicacionStatusResponse {
	private boolean creado;
	private Ubicacion ubicacion;
	private String mensaje;

	public UbicacionStatusResponse(boolean creado, Ubicacion ubicacion, String mensaje) {
		this.creado = creado;
		this.ubicacion = ubicacion;
		this.mensaje = mensaje;
	}

	public boolean isCreado() {
		return this.creado;
	}

	public boolean getCreado() {
		return this.creado;
	}

	public void setCreado(boolean creado) {
		this.creado = creado;
	}

	public Ubicacion getUbicacion() {
		return this.ubicacion;
	}

	public void setUbicacion(Ubicacion ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getMensaje() {
		return this.mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
