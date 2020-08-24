package courier.uy.core.resources.dto;

import courier.uy.core.entity.Company;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

public class ExcelCliente {
	private Company empresa;

	private Boolean hasErrors = false;

	private Boolean wasCreated = false;

	private Row errorRow;

	public String bddErrors = "";

	public List<String> grupos = new ArrayList<>();

	public void setEmpresa(Company empresa) {
		this.empresa = empresa;
	}

	public void setHasErrors(Boolean hasErrors) {
		this.hasErrors = hasErrors;
	}

	public void setWasCreated(Boolean wasCreated) {
		this.wasCreated = wasCreated;
	}

	public void setRow(Row row) {
		this.errorRow = row;
	}

	public Row getRow() {
		return this.errorRow;
	}

	public Boolean getHasErrors() {
		return this.hasErrors;
	}

	public Boolean getWasCreated() {
		return this.wasCreated;
	}

	public Company getEmpresa() {
		return this.empresa;
	}

}
