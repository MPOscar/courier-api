package courier.uy.core.resources.dto;

import java.util.HashMap;

public class PedidoResponse {

	public HashMap<String, Integer> divisiones;
	public HashMap<String, Integer> marcas;
	public HashMap<String, Integer> lineas;

	public PedidoResponse(){

	}

	public PedidoResponse(HashMap<String, Integer> divisiones, HashMap<String, Integer> marcas, HashMap<String, Integer> lineas) {
		this.divisiones = divisiones;
		this.marcas = marcas;
		this.lineas = lineas;
	}
}