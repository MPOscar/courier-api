package courier.uy.core.resources.dto;

import java.util.List;

import courier.uy.core.entity.ProductoAccion;

public class AcknowledgeRequest {
	private List<ProductoAccion> acciones;
	private String idEmpresa;

	public List<ProductoAccion> getAcciones() {
		return acciones;
	}

	public void setAcciones(List<ProductoAccion> acciones) {
		this.acciones = acciones;
	}

	public String getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(String idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

}
