package courier.uy.core.excel.formats;

import java.math.BigDecimal;
import java.util.List;

import courier.uy.core.entity.Producto;
import courier.uy.core.entity.Empaque;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TataVolumetry implements IExcelFormat {

	@Override
	public Workbook GetWorkbook(List<Producto> allProducts) {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Productos");

		Font headerRedFont = workbook.createFont();
		headerRedFont.setBold(true);
		headerRedFont.setColor(IndexedColors.RED.getIndex());

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);

		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setBorderTop(BorderStyle.MEDIUM);
		headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
		headerCellStyle.setBorderLeft(BorderStyle.MEDIUM);
		headerCellStyle.setBorderRight(BorderStyle.MEDIUM);
		headerCellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerCellStyle.setFont(headerFont);

		CellStyle redHeaderStyle = workbook.createCellStyle();
		redHeaderStyle.setAlignment(HorizontalAlignment.LEFT);
		redHeaderStyle.setBorderTop(BorderStyle.MEDIUM);
		redHeaderStyle.setBorderBottom(BorderStyle.MEDIUM);
		redHeaderStyle.setBorderLeft(BorderStyle.MEDIUM);
		redHeaderStyle.setBorderRight(BorderStyle.MEDIUM);
		redHeaderStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		redHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		redHeaderStyle.setFont(headerRedFont);

		CellStyle subtitleCellStyle = workbook.createCellStyle();
		subtitleCellStyle.setAlignment(HorizontalAlignment.CENTER);
		subtitleCellStyle.setBorderTop(BorderStyle.MEDIUM);
		subtitleCellStyle.setBorderBottom(BorderStyle.MEDIUM);
		subtitleCellStyle.setBorderLeft(BorderStyle.MEDIUM);
		subtitleCellStyle.setBorderRight(BorderStyle.MEDIUM);
		subtitleCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		subtitleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		subtitleCellStyle.setWrapText(true);
		subtitleCellStyle.setFont(headerFont);

		CellStyle productCellStyle = workbook.createCellStyle();
		productCellStyle.setAlignment(HorizontalAlignment.CENTER);
		productCellStyle.setBorderTop(BorderStyle.THIN);
		productCellStyle.setBorderBottom(BorderStyle.THIN);
		productCellStyle.setBorderLeft(BorderStyle.THIN);
		productCellStyle.setBorderRight(BorderStyle.THIN);
		productCellStyle.setFont(headerFont);

		Row headerRow = sheet.createRow((short) 0);
		for (int i = 0; i < 6; i++) {
			Cell redCell = headerRow.createCell((short) i);
			if (i == 0) {
				redCell.setCellValue("Proveedor:");
			}
			redCell.setCellStyle(redHeaderStyle);
		}
		sheet.addMergedRegion(CellRangeAddress.valueOf("A1:F1"));

		for (int i = 12; i < 16; i++) {
			Cell cell = headerRow.createCell((short) i);
			if (i == 12) {
				cell.setCellValue("Datos unidad de Venta");
			}
			cell.setCellStyle(headerCellStyle);
		}

		sheet.addMergedRegion(CellRangeAddress.valueOf("M1:P1"));
		for (int i = 16; i < 20; i++) {
			Cell cell = headerRow.createCell((short) i);
			if (i == 16) {
				cell.setCellValue("Datos inner/pack");
			}
			cell.setCellStyle(headerCellStyle);
		}
		sheet.addMergedRegion(CellRangeAddress.valueOf("Q1:T1"));
		for (int i = 20; i < 24; i++) {
			Cell cell = headerRow.createCell((short) i);
			if (i == 20) {
				cell.setCellValue("Datos caja/master");
			}
			cell.setCellStyle(headerCellStyle);
		}
		sheet.addMergedRegion(CellRangeAddress.valueOf("U1:X1"));

		Row subtitleRow = sheet.createRow((short) 1);
		String[] subtitles = { "Estadistico Ta-Ta S.A.", "Descripción", "", "", "", "",
				"Código Caja *Embalaje* (DUN 14)", "Código Unidad *Empaque* (EAN 13)",
				"Unidades por Empaque (Pack/Inner)", "Unidades x Embalaje (Cajas/Master)", "Tipo Empaque (caja)",
				"Etiqueta Empaque", "Largo Empaque (*CM)", "Alto Empaque (*CM)", "Ancho Empaque (*CM)",
				"Peso Empaque (*KG)", "Largo Empaque (*CM)", "Alto Empaque (*CM)", "Ancho Empaque (*CM)",
				"Peso Empaque (*KG)", "Largo Embalaje (*CM)", "Alto Embalaje (*CM)", "Ancho Embalaje (*CM)",
				"Peso Embalaje (*KG)", "Cajas x base de pallet", "Cantidad de pisos por pallet",
				"Vida útil (cantidad de días)", "Art. Referencia", "Foto" };
		for (int i = 0; i < subtitles.length; i++) {
			Cell cell = subtitleRow.createCell((short) i);
			if (i != 2 && i != 3 && i != 4 && i != 5) {
				cell.setCellValue(subtitles[i]);
			}
			cell.setCellStyle(subtitleCellStyle);
		}
		sheet.addMergedRegion(CellRangeAddress.valueOf("B2:F2"));

		for (int i = 2; i < allProducts.size() + 2; i++) {
			Producto product = allProducts.get(i - 2);
			try {
				Row productRow = sheet.createRow((short) i);
				Empaque emp = new Empaque();
				Empaque padre = new Empaque();
				String presentacion = "";

				if (product.getEmpaques().size() != 0) {
					emp = product.getEmpaques().iterator().next();
					if (emp.getPadre() != null)
						padre = emp.getPadre();

				}
				String pesoProducto = "";

				String productoFoto = "";

				if (product.getPesoBruto() != null) {
					pesoProducto = "" + product.getPesoBruto().divide(new BigDecimal(1000));
				}

				if (product.getFoto() != null) {
					productoFoto = product.getFoto();
				}

				String dun14 = "";
				int middleProducts = 0;
				int finalProducts = 0;
				double altoCaja = 0;
				double anchoCaja = 0;
				double largoCaja = 0;
				double pesoCaja = 0;
				double altoEmpaque = 0;
				double anchoEmpaque = 0;
				double largoEmpaque = 0;
				double pesoEmpaque = 0;
				String altoEmpaqueString = "";
				String anchoEmpaqueString = "";
				String largoEmpaqueString = "";
				String pesoEmpaqueString = "";

				if (emp.getGtin() != null
						&& (emp.getGtin().length() == 14 || emp.getGtin().length() == 11 || emp.getPadre() == null)) {
					dun14 = emp.getGtin();
					finalProducts = emp.getCantidad().intValue();
					if (emp.getPresentacion() != null) {
						if (emp.getPresentacion().getNombre() != null)
							presentacion = emp.getPresentacion().getNombre();

					}
					if (emp.getAlto() != null) {
						altoCaja = emp.getAlto().doubleValue();
						anchoCaja = emp.getProfundidad().doubleValue();
						largoCaja = emp.getAncho().doubleValue();
					}
					if (emp.getPesoBruto() != null) {
						pesoCaja = emp.getPesoBruto().divide(new BigDecimal(1000)).doubleValue();
					}

				} else if (padre.getGtin() != null/* && padre.getGtin().length() == 14 */) {
					dun14 = padre.getGtin();
					middleProducts = emp.getCantidad().intValue();
					finalProducts = padre.getCantidad().intValue() * middleProducts;
					if (padre.getPresentacion() != null) {
						if (padre.getPresentacion().getNombre() != null)
							presentacion = padre.getPresentacion().getNombre();

					}
					if (padre.getAlto() != null) {
						altoCaja = padre.getAlto().doubleValue();
						anchoCaja = padre.getProfundidad().doubleValue();
						largoCaja = padre.getAncho().doubleValue();
					}

					if (padre.getPesoBruto() != null) {
						pesoCaja = padre.getPesoBruto().divide(new BigDecimal(1000)).doubleValue();
					}
					if (emp.getAlto() != null) {
						altoEmpaque = emp.getAlto().doubleValue();
						anchoEmpaque = emp.getProfundidad().doubleValue();
						largoEmpaque = emp.getAncho().doubleValue();
					}

					if (emp.getPesoBruto() != null) {
						pesoEmpaque = emp.getPesoBruto().divide(new BigDecimal(1000)).doubleValue();
					}
					altoEmpaqueString = "" + altoEmpaque;
					anchoEmpaqueString = "" + anchoEmpaque;
					largoEmpaqueString = "" + largoEmpaque;
					pesoEmpaqueString = "" + pesoEmpaque;

				}
				String middleProductsString = "";
				if (middleProducts != 0)
					middleProductsString = "" + middleProducts;

				String[] values = { "", product.getDescripcion(), "", "", "", "", dun14, product.getGtinPresentacion(),
						middleProductsString, "" + finalProducts, presentacion, product.getCpp(),
						"" + product.getAncho().doubleValue(), "" + product.getAlto().doubleValue(),
						"" + product.getProfundidad().doubleValue(), pesoProducto, largoEmpaqueString,
						altoEmpaqueString, anchoEmpaqueString, pesoEmpaqueString, "" + largoCaja, "" + altoCaja,
						"" + anchoCaja, "" + pesoCaja, "", "", "", "",
						"https://s3.us-east-2.amazonaws.com/rondanet/" + productoFoto };
				for (int j = 0; j < values.length; j++) {
					Cell cell = productRow.createCell((short) j);
					if (j == 1 || j == 6 || j == 7 || j == 10 || j == 28) {
						cell.setCellValue(values[j]);
					} else if (j != 2 && j != 3 && j != 4 && j != 5) {
						if (!values[j].equals(""))
							cell.setCellValue(Double.parseDouble(values[j]));
					}
					cell.setCellStyle(productCellStyle);
				}
			} catch (Exception ex) {
				// TODO Manejar excepción apropiadamente
			}

			sheet.addMergedRegion(CellRangeAddress.valueOf("B" + (i + 1) + ":F" + (i + 1)));
		}

		sheet.setDefaultColumnWidth(12);
		return workbook;
	}

}
