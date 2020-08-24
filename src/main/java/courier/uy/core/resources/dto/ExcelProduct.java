package courier.uy.core.resources.dto;

import courier.uy.core.entity.Producto;
import org.apache.poi.ss.usermodel.Row;

public class ExcelProduct {
	private Producto product;

	private Boolean hasErrors = false;

	private Boolean wasCreated = false;

	private Row errorRow;

	public String bddErrors = "";

	public void setProduct(Producto product) {
		this.product = product;
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

	public Producto getProduct() {
		return this.product;
	}

}
