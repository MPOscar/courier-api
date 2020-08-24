package courier.uy.core.utils;

import courier.uy.core.entity.Categoria;
import courier.uy.core.entity.Precio;
import courier.uy.core.entity.Presentacion;
import courier.uy.core.entity.Producto;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.exceptions.PackException;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.ExcelProduct;
import courier.uy.core.repository.IPrecioRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.*;

public class ExcelUtilityPrecios {

	private final IPrecioRepository precioRepository;

	public ExcelUtilityPrecios(IPrecioRepository precioRepository){
		this.precioRepository = precioRepository;
	}


	public static List<Precio> excelToProductPrecioList(InputStream uploadedInputStream)
			throws ServiceException {
		HSSFWorkbook workbook;

		try {
			workbook = new HSSFWorkbook(uploadedInputStream);
			Sheet sheet = workbook.getSheetAt(0);
			workbook.getNumberOfSheets();
			int totalRows = sheet.getPhysicalNumberOfRows();

			List<ExcelProduct> productos = new ArrayList<ExcelProduct>(totalRows);
			List<String> productosCPP = new ArrayList<>();
			List<String> productosGtin = new ArrayList<>();

			String moneda;
			double precio;
			String cpp;
			Date suspendidoDesde = null, suspendidoHasta = null;

			Producto producto;

			Categoria categoria;
			Presentacion presentacion;

			Iterator<Row> i = sheet.rowIterator();
			DataFormatter formatter = new DataFormatter();

			String glnUbicacion = "";

			List<Precio> precios = new ArrayList<>();
			while (i.hasNext()) {
				Row dataRow = i.next();
				if (dataRow.getRowNum() == 1) {
					glnUbicacion = dataRow.getCell(0).getStringCellValue();
				}

				if (dataRow.getRowNum() > 4) {
					cpp = dataRow.getCell(0).getStringCellValue();
					moneda = dataRow.getCell(3).getStringCellValue();
					precio = dataRow.getCell(4).getNumericCellValue();
					Precio Productoprecio = new Precio(cpp, glnUbicacion, moneda, String.valueOf(precio));
					precios.add(Productoprecio);
				}
			}
			workbook.close();
			return precios;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Ocurrió un error leyendo los archivos del excel: " + e.getMessage());
		}
	}

	public static void chequearQueElGtinNoEsteRepetido(List<String> gtinProductosExcel, String gtin,
			Boolean productosOEmpaques) throws ModelException, PackException {
		for (int i = 0; i < gtinProductosExcel.size(); i++) {
			if (gtinProductosExcel.get(i).equals(gtin)) {
				if (productosOEmpaques)
					throw new ModelException("Ya existe un produto en el excel con este gtin asosiado");
				else
					throw new PackException("Ya existe un empaque en el excel con este gtin asosiado");
			}
		}
	}

	public static void chequearQueElCPPNoEsteRepetido(List<String> productosCPP, String cpp, Boolean productosOEmpaques)
			throws ModelException {
		for (int i = 0; i < productosCPP.size(); i++) {
			if (productosCPP.get(i).equals(cpp)) {
				if (productosOEmpaques)
					throw new ModelException("Ya existe un produto en el excel con este cpp asosiado");
			}
		}
	}



}
