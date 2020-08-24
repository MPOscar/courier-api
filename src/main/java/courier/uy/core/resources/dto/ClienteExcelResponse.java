package courier.uy.core.resources.dto;

import java.util.List;

public class ClienteExcelResponse {

	private String menssage;

	private List<ExcelCliente> clientesErrors;

	public ClienteExcelResponse(String menssage, List<ExcelCliente> clientesError) {
		this.menssage = menssage;
		this.clientesErrors = clientesError;
	}

	public void setMenssage(String menssage) {
		this.menssage = menssage;
	}

	public String getMenssage() {
		return menssage;
	}

	public void setClientesErrors(List<ExcelCliente> clientesErrors) {
		this.clientesErrors = clientesErrors;
	}

	public List<ExcelCliente> getClientesErrors() {
		return this.clientesErrors;
	}

}