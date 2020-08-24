package courier.uy.core.resources.dto;

import java.util.List;

public class EliminarProductos {

	private Boolean eliminarTodos = false;

	private List<String> productosEliminar;

	private List<String> productosNoEliminar;

	public Boolean getEliminarTodos() {
		return eliminarTodos;
	}

	public void setEliminarTodos(Boolean eliminarTodos) {
		this.eliminarTodos = eliminarTodos;
	}

	public void setProductosEliminar(List<String> productosEliminar) {
		this.productosEliminar = productosEliminar;
	}

	public List<String> getProductosEliminar() {
		return this.productosEliminar;
	}

	public void setProductosNoEliminar(List<String> productosNoEliminar) {
		this.productosNoEliminar = productosNoEliminar;
	}

	public List<String> getProductosNoEliminar() {
		return this.productosNoEliminar;
	}
	
}