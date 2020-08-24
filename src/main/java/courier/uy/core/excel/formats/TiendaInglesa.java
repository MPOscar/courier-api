package courier.uy.core.excel.formats;

import java.util.List;

import courier.uy.core.entity.Producto;
import org.apache.poi.ss.usermodel.Workbook;

public class TiendaInglesa implements IExcelFormat {

	@Override
	public Workbook GetWorkbook(List<Producto> products) {
		return null;
	}

}
