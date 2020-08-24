package courier.uy.core.excel.formats;

import courier.uy.core.entity.Producto;
import org.apache.poi.ss.usermodel.Workbook;
import java.util.List;

public interface IExcelFormat {
	Workbook GetWorkbook(List<Producto> products);
}
