package courier.uy.core.utils;

import java.io.InputStream;
import java.io.IOException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import courier.uy.core.entity.Categoria;
import courier.uy.core.entity.Presentacion;
import courier.uy.core.entity.Producto;
import courier.uy.core.utils.poiji.ExcelRNCliente;
import courier.uy.core.utils.poiji.ExcelRNProducto;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import com.poiji.option.PoijiOptions.PoijiOptionsBuilder;

import courier.uy.core.exceptions.ModelException;
import courier.uy.core.exceptions.PackException;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.ExcelProduct;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import org.apache.poi.ss.util.CellRangeAddress;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import courier.uy.core.entity.Empaque;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Pallet;
import io.netty.util.internal.ThreadLocalRandom;

public class ExcelUtility {

	public static Workbook generateTataCompleteExcel(List<Producto> allProducts) {
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

	public static Workbook generateTataExcel(List<Producto> allProducts) {
		Workbook workbook = new XSSFWorkbook();

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 36);
		headerFont.setFontName("Verdana");

		Font titleFont = workbook.createFont();
		titleFont.setBold(true);
		titleFont.setFontHeightInPoints((short) 24);
		titleFont.setFontName("Verdana");

		Font subtitleFont = workbook.createFont();
		subtitleFont.setBold(true);
		subtitleFont.setFontHeightInPoints((short) 18);
		subtitleFont.setFontName("Arial");

		Font barcodeFont = workbook.createFont();
		barcodeFont.setFontHeightInPoints((short) 22);
		barcodeFont.setFontName("Verdana");

		Font unitTitleFont = workbook.createFont();
		unitTitleFont.setBold(true);
		unitTitleFont.setFontHeightInPoints((short) 14);
		unitTitleFont.setFontName("Verdana");

		Font unitSubtitleFont = workbook.createFont();
		unitSubtitleFont.setFontHeightInPoints((short) 14);
		unitSubtitleFont.setFontName("Verdana");

		// Create a Sheet
		Set<String> addedGtins = new HashSet<String>();
		for (Producto producto : allProducts) {
			try {
				Producto product = producto;
				Empaque emp = new Empaque();
				Empaque padre = new Empaque();

				if (product.getEmpaques().size() != 0) {
					emp = product.getEmpaques().iterator().next();
					if (emp.getPadre() != null)
						padre = emp.getPadre();

				}

				String dun14 = "";
				String gtinIntermedio = "";
				int middleProducts = 0;
				int finalProducts = 0;
				double pesoProducto = 0;
				double altoCaja = 0;
				double anchoCaja = 0;
				double largoCaja = 0;
				double pesoCaja = 0;
				double altoEmpaque = 0;
				double anchoEmpaque = 0;
				double largoEmpaque = 0;
				double pesoEmpaque = 0;

				if (producto.getPesoBruto() != null) {
					pesoProducto = producto.getPesoBruto().doubleValue();
				}

				if (emp.getGtin() != null
						&& (emp.getGtin().length() == 14 || emp.getGtin().length() == 11 || emp.getPadre() == null)) {
					dun14 = emp.getGtin();
					finalProducts = emp.getCantidad().intValue();

					if (emp.getAlto() != null) {
						altoCaja = emp.getAlto().doubleValue();
						anchoCaja = emp.getProfundidad().doubleValue();
						largoCaja = emp.getAncho().doubleValue();
					}
					if (emp.getPesoBruto() != null) {
						pesoCaja = emp.getPesoBruto().doubleValue();
					}

				} else if (padre.getGtin() != null/* && padre.getGtin().length() == 14 */) {
					gtinIntermedio = emp.getGtin();
					dun14 = padre.getGtin();
					middleProducts = emp.getCantidad().intValue();
					finalProducts = padre.getCantidad().intValue() * middleProducts;

					if (padre.getAlto() != null) {
						altoCaja = padre.getAlto().doubleValue();
						anchoCaja = padre.getProfundidad().doubleValue();
						largoCaja = padre.getAncho().doubleValue();
					}

					if (padre.getPesoBruto() != null) {
						pesoCaja = padre.getPesoBruto().doubleValue();
					}
					if (emp.getAlto() != null) {
						altoEmpaque = emp.getAlto().doubleValue();
						anchoEmpaque = emp.getProfundidad().doubleValue();
						largoEmpaque = emp.getAncho().doubleValue();
					}

					if (emp.getPesoBruto() != null) {
						pesoEmpaque = emp.getPesoBruto().doubleValue();
					}

				}

				String sheetName = producto.getGtinPresentacion();
				if (addedGtins.contains(producto.getGtinPresentacion())) {
					int randomNum = ThreadLocalRandom.current().nextInt(1, 1000 + 1);
					sheetName += " - " + randomNum;
				} else {
					addedGtins.add(producto.getGtinPresentacion());
				}

				Sheet sheet = workbook.createSheet(sheetName);

				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFont(headerFont);
				headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
				headerCellStyle.setBorderTop(BorderStyle.MEDIUM);
				headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
				headerCellStyle.setBorderLeft(BorderStyle.MEDIUM);
				headerCellStyle.setBorderRight(BorderStyle.MEDIUM);
				headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
				headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				Row headerRow = sheet.createRow((short) 1);
				for (int i = 1; i < 17; i++) {
					Cell headerCell = headerRow.createCell((short) i);
					if (i == 1) {
						headerCell.setCellValue("Nuevos Ingresos - Datos Logísticos");
					}
					headerCell.setCellStyle(headerCellStyle);

				}

				sheet.addMergedRegion(CellRangeAddress.valueOf("B2:Q2"));

				Row providerRow = sheet.createRow((short) 3);
				CellStyle providerCellStyle = workbook.createCellStyle();
				providerCellStyle.setFont(titleFont);
				providerCellStyle.setAlignment(HorizontalAlignment.CENTER);
				providerCellStyle.setBorderTop(BorderStyle.MEDIUM);
				providerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
				providerCellStyle.setBorderLeft(BorderStyle.MEDIUM);
				providerCellStyle.setBorderRight(BorderStyle.MEDIUM);
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY");
				LocalDateTime now = LocalDateTime.now();
				for (int i = 1; i < 17; i++) {
					Cell cell = providerRow.createCell((short) i);
					if (i == 1) {
						cell.setCellValue("Proveedor");
					} else if (i == 3) {
						cell.setCellValue(producto.getEmpresa().getRazonSocial());
					} else if (i == 6) {
						cell.setCellValue("Origen");
					} else if (i == 8) {
						cell.setCellValue(producto.getPaisOrigen());
					} else if (i == 13) {
						cell.setCellValue("Fecha");
					} else if (i == 14) {
						cell.setCellValue(dtf.format(now));
					}
					cell.setCellStyle(providerCellStyle);

				}
				sheet.addMergedRegion(CellRangeAddress.valueOf("B4:C4"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("D4:F4"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("G4:H4"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("I4:M4"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("O4:Q4"));

				Row barcodeTitleRow = sheet.createRow((short) 6);
				CellStyle barcodeTitleRowStyle = workbook.createCellStyle();
				barcodeTitleRowStyle.setBorderTop(BorderStyle.MEDIUM);
				barcodeTitleRowStyle.setBorderBottom(BorderStyle.MEDIUM);
				barcodeTitleRowStyle.setBorderLeft(BorderStyle.MEDIUM);
				barcodeTitleRowStyle.setBorderRight(BorderStyle.MEDIUM);
				barcodeTitleRowStyle.setFont(subtitleFont);
				barcodeTitleRowStyle.setAlignment(HorizontalAlignment.CENTER);
				for (int i = 1; i < 17; i++) {
					Cell cell = barcodeTitleRow.createCell((short) i);
					if (i == 1) {
						cell.setCellValue("Código de Barras Sub-Unidad");
					} else if (i == 4) {
						cell.setCellValue("Código de Barras Unidad/Fact.");
					} else if (i == 7) {
						cell.setCellValue("Código de Barras Display");
					} else if (i == 10) {
						cell.setCellValue("Código de Barras Master o Caja");
					} else if (i == 13) {
						cell.setCellValue("Código");
					}
					cell.setCellStyle(barcodeTitleRowStyle);
				}

				sheet.addMergedRegion(CellRangeAddress.valueOf("B7:D7"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("E7:G7"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("H7:J7"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("K7:M7"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("N7:Q7"));

				Row barcodeRow = sheet.createRow((short) 8);

				CellStyle barcodeRowStyle = workbook.createCellStyle();
				barcodeRowStyle.setBorderTop(BorderStyle.THIN);
				barcodeRowStyle.setBorderBottom(BorderStyle.THIN);
				barcodeRowStyle.setBorderLeft(BorderStyle.THIN);
				barcodeRowStyle.setBorderRight(BorderStyle.THIN);
				barcodeRowStyle.setFont(barcodeFont);
				barcodeRowStyle.setAlignment(HorizontalAlignment.CENTER);

				for (int i = 1; i < 17; i++) {
					Cell cell = barcodeRow.createCell((short) i);
					if (i == 1) {
						cell.setCellValue("");

					} else if (i == 4) {
						cell.setCellValue(producto.getGtinPresentacion());
					} else if (i == 7) {
						cell.setCellValue(gtinIntermedio);
					} else if (i == 10) {
						cell.setCellValue(dun14);
					} else if (i == 13) {
						cell.setCellValue(producto.getCpp());
					}
					cell.setCellStyle(barcodeRowStyle);

				}

				sheet.addMergedRegion(CellRangeAddress.valueOf("B9:D9"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("E9:G9"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("H9:J9"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("K9:M9"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("N9:Q9"));

				Row descriptionTitleRow = sheet.createRow((short) 10);
				CellStyle descriptionTitleStyle = workbook.createCellStyle();
				descriptionTitleStyle.setFont(titleFont);
				descriptionTitleStyle.setBorderTop(BorderStyle.MEDIUM);
				descriptionTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
				descriptionTitleStyle.setBorderLeft(BorderStyle.MEDIUM);
				descriptionTitleStyle.setBorderRight(BorderStyle.MEDIUM);
				descriptionTitleStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
				descriptionTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				descriptionTitleStyle.setAlignment(HorizontalAlignment.CENTER);

				for (int i = 1; i < 17; i++) {
					Cell descriptionTitleCell = descriptionTitleRow.createCell((short) i);
					if (i == 1) {
						descriptionTitleCell.setCellValue("Descripción");
					}
					descriptionTitleCell.setCellStyle(descriptionTitleStyle);
				}
				sheet.addMergedRegion(CellRangeAddress.valueOf("B11:Q11"));

				Row descriptionRow = sheet.createRow((short) 11);
				CellStyle descriptionStyle = workbook.createCellStyle();
				descriptionStyle.setBorderTop(BorderStyle.MEDIUM);
				descriptionStyle.setBorderBottom(BorderStyle.MEDIUM);
				descriptionStyle.setBorderLeft(BorderStyle.MEDIUM);
				descriptionStyle.setBorderRight(BorderStyle.MEDIUM);
				descriptionStyle.setFont(titleFont);
				descriptionStyle.setAlignment(HorizontalAlignment.CENTER);

				for (int i = 1; i < 17; i++) {
					Cell descriptionCell = descriptionRow.createCell((short) i);
					if (i == 1) {
						descriptionCell.setCellValue(producto.getDescripcion());
					}
					descriptionCell.setCellStyle(descriptionStyle);
				}
				sheet.addMergedRegion(CellRangeAddress.valueOf("B12:Q12"));

				Row unitsTitleRow = sheet.createRow((short) 13);
				CellStyle unitsTitleStyle = workbook.createCellStyle();
				unitsTitleStyle.setFont(unitTitleFont);
				unitsTitleStyle.setAlignment(HorizontalAlignment.CENTER);
				unitsTitleStyle.setBorderTop(BorderStyle.MEDIUM);
				unitsTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
				unitsTitleStyle.setBorderLeft(BorderStyle.MEDIUM);
				unitsTitleStyle.setBorderRight(BorderStyle.MEDIUM);

				for (int i = 1; i < 17; i++) {
					Cell cell = unitsTitleRow.createCell((short) i);
					if (i == 1) {
						cell.setCellValue("Unidad");
					} else if (i == 5) {
						cell.setCellValue("Display");
					} else if (i == 9) {
						cell.setCellValue("Master o Embalaje");
					} else if (i == 13) {
						cell.setCellValue("Presentación");
					}
					cell.setCellStyle(unitsTitleStyle);

				}

				sheet.addMergedRegion(CellRangeAddress.valueOf("B14:E14"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("F14:I14"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("J14:M14"));
				sheet.addMergedRegion(CellRangeAddress.valueOf("N14:Q14"));

				Row unitsSubtitleRow = sheet.createRow((short) 14);
				CellStyle unitSubtitleStyle = workbook.createCellStyle();
				unitSubtitleStyle.setFont(unitSubtitleFont);
				unitSubtitleStyle.setAlignment(HorizontalAlignment.CENTER);
				unitSubtitleStyle.setBorderTop(BorderStyle.MEDIUM);
				unitSubtitleStyle.setBorderBottom(BorderStyle.MEDIUM);
				unitSubtitleStyle.setBorderLeft(BorderStyle.MEDIUM);
				unitSubtitleStyle.setBorderRight(BorderStyle.MEDIUM);
				unitSubtitleStyle.setWrapText(true);

				Cell unitNetWeightTitleCell = unitsSubtitleRow.createCell((short) 1);
				unitNetWeightTitleCell.setCellValue("Peso neto en Grs.");
				unitNetWeightTitleCell.setCellStyle(unitSubtitleStyle);

				Cell unitLengthTitleCell = unitsSubtitleRow.createCell((short) 2);
				unitLengthTitleCell.setCellValue("Largo en Cm.");
				unitLengthTitleCell.setCellStyle(unitSubtitleStyle);

				Cell unitWidthTitleCell = unitsSubtitleRow.createCell((short) 3);
				unitWidthTitleCell.setCellValue("Ancho en Cm.");
				unitWidthTitleCell.setCellStyle(unitSubtitleStyle);

				Cell unitHeightTitleCell = unitsSubtitleRow.createCell((short) 4);
				unitHeightTitleCell.setCellValue("Alto en Cm.");
				unitHeightTitleCell.setCellStyle(unitSubtitleStyle);

				Cell displayNetWeightTitleCell = unitsSubtitleRow.createCell((short) 5);
				displayNetWeightTitleCell.setCellValue("Peso neto en Grs.");
				displayNetWeightTitleCell.setCellStyle(unitSubtitleStyle);

				Cell displayLengthTitleCell = unitsSubtitleRow.createCell((short) 6);
				displayLengthTitleCell.setCellValue("Largo en Cm.");
				displayLengthTitleCell.setCellStyle(unitSubtitleStyle);

				Cell displayWidthTitleCell = unitsSubtitleRow.createCell((short) 7);
				displayWidthTitleCell.setCellValue("Ancho en Cm.");
				displayWidthTitleCell.setCellStyle(unitSubtitleStyle);

				Cell displayHeightTitleCell = unitsSubtitleRow.createCell((short) 8);
				displayHeightTitleCell.setCellValue("Alto en Cm.");
				displayHeightTitleCell.setCellStyle(unitSubtitleStyle);

				Cell masterNetWeightTitleCell = unitsSubtitleRow.createCell((short) 9);
				masterNetWeightTitleCell.setCellValue("Peso neto en Grs.");
				masterNetWeightTitleCell.setCellStyle(unitSubtitleStyle);

				Cell masterLengthTitleCell = unitsSubtitleRow.createCell((short) 10);
				masterLengthTitleCell.setCellValue("Largo en Cm.");
				masterLengthTitleCell.setCellStyle(unitSubtitleStyle);

				Cell masterWidthTitleCell = unitsSubtitleRow.createCell((short) 11);
				masterWidthTitleCell.setCellValue("Ancho en Cm.");
				masterWidthTitleCell.setCellStyle(unitSubtitleStyle);

				Cell masterHeightTitleCell = unitsSubtitleRow.createCell((short) 12);
				masterHeightTitleCell.setCellValue("Alto en Cm.");
				masterHeightTitleCell.setCellStyle(unitSubtitleStyle);

				Cell presentationNetWeightTitleCell = unitsSubtitleRow.createCell((short) 13);
				presentationNetWeightTitleCell.setCellValue("Presentación/Subunidad");
				presentationNetWeightTitleCell.setCellStyle(unitSubtitleStyle);

				Cell presentationLengthTitleCell = unitsSubtitleRow.createCell((short) 14);
				presentationLengthTitleCell.setCellValue("Presentación/Unidad");
				presentationLengthTitleCell.setCellStyle(unitSubtitleStyle);

				Cell presentationWidthTitleCell = unitsSubtitleRow.createCell((short) 15);
				presentationWidthTitleCell.setCellValue("Presentación/Display");
				presentationWidthTitleCell.setCellStyle(unitSubtitleStyle);

				Cell presentationHeightTitleCell = unitsSubtitleRow.createCell((short) 16);
				presentationHeightTitleCell.setCellValue("Presentación/Master o Caja");
				presentationHeightTitleCell.setCellStyle(unitSubtitleStyle);

				Row unitsRow = sheet.createRow((short) 16);
				CellStyle unitStyle = workbook.createCellStyle();
				unitStyle.setFont(barcodeFont);
				unitStyle.setAlignment(HorizontalAlignment.CENTER);
				unitStyle.setAlignment(HorizontalAlignment.CENTER);
				unitStyle.setBorderTop(BorderStyle.THIN);
				unitStyle.setBorderBottom(BorderStyle.THIN);
				unitStyle.setBorderLeft(BorderStyle.THIN);
				unitStyle.setBorderRight(BorderStyle.THIN);

				Cell unitNetWeightCell = unitsRow.createCell((short) 1);
				unitNetWeightCell.setCellValue(pesoProducto);
				unitNetWeightCell.setCellStyle(unitStyle);

				Cell unitLengthCell = unitsRow.createCell((short) 2);
				unitLengthCell.setCellValue(producto.getAncho().doubleValue());
				unitLengthCell.setCellStyle(unitStyle);

				Cell unitWidthCell = unitsRow.createCell((short) 3);
				unitWidthCell.setCellValue(producto.getProfundidad().doubleValue());
				unitWidthCell.setCellStyle(unitStyle);

				Cell unitHeightCell = unitsRow.createCell((short) 4);
				unitHeightCell.setCellValue(producto.getAlto().doubleValue());
				unitHeightCell.setCellStyle(unitStyle);

				Cell displayNetWeightCell = unitsRow.createCell((short) 5);
				if (pesoEmpaque != 0)
					displayNetWeightCell.setCellValue(pesoEmpaque);
				displayNetWeightCell.setCellStyle(unitStyle);

				Cell displayLengthCell = unitsRow.createCell((short) 6);
				if (anchoEmpaque != 0)
					displayLengthCell.setCellValue(anchoEmpaque);
				displayLengthCell.setCellStyle(unitStyle);

				Cell displayWidthCell = unitsRow.createCell((short) 7);
				if (largoEmpaque != 0)
					displayWidthCell.setCellValue(largoEmpaque);
				displayWidthCell.setCellStyle(unitStyle);

				Cell displayHeightCell = unitsRow.createCell((short) 8);
				if (altoEmpaque != 0)
					displayHeightCell.setCellValue(altoEmpaque);
				displayHeightCell.setCellStyle(unitStyle);

				Cell masterNetWeightCell = unitsRow.createCell((short) 9);
				masterNetWeightCell.setCellValue(pesoCaja);
				masterNetWeightCell.setCellStyle(unitStyle);

				Cell masterLengthCell = unitsRow.createCell((short) 10);
				masterLengthCell.setCellValue(anchoCaja);
				masterLengthCell.setCellStyle(unitStyle);

				Cell masterWidthCell = unitsRow.createCell((short) 11);
				masterWidthCell.setCellValue(largoCaja);
				masterWidthCell.setCellStyle(unitStyle);

				Cell masterHeightCell = unitsRow.createCell((short) 12);
				masterHeightCell.setCellValue(altoCaja);
				masterHeightCell.setCellStyle(unitStyle);

				Cell presentationNetWeightCell = unitsRow.createCell((short) 13);
				presentationNetWeightCell.setCellValue("-");
				presentationNetWeightCell.setCellStyle(unitStyle);

				Cell presentationLengthCell = unitsRow.createCell((short) 14);
				presentationLengthCell.setCellValue("1");
				presentationLengthCell.setCellStyle(unitStyle);

				Cell presentationWidthCell = unitsRow.createCell((short) 15);
				if (middleProducts != 0)
					presentationWidthCell.setCellValue(middleProducts);
				presentationWidthCell.setCellStyle(unitStyle);

				Cell presentationHeightCell = unitsRow.createCell((short) 16);
				presentationHeightCell.setCellValue(finalProducts);
				presentationHeightCell.setCellStyle(unitStyle);

				Row observationsTitleRow = sheet.createRow((short) 19);
				CellStyle observationsTitleStyle = workbook.createCellStyle();
				observationsTitleStyle.setFont(titleFont);
				observationsTitleStyle.setAlignment(HorizontalAlignment.CENTER);
				observationsTitleStyle.setBorderTop(BorderStyle.MEDIUM);
				observationsTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
				observationsTitleStyle.setBorderLeft(BorderStyle.MEDIUM);
				observationsTitleStyle.setBorderRight(BorderStyle.MEDIUM);

				for (int i = 1; i < 17; i++) {
					Cell cell = observationsTitleRow.createCell((short) i);
					if (i == 1) {
						cell.setCellValue("Observaciones");
					}
					cell.setCellStyle(observationsTitleStyle);

				}
				sheet.addMergedRegion(CellRangeAddress.valueOf("B20:Q20"));

				Row observationsRow = sheet.createRow((short) 20);
				for (int i = 1; i < 17; i++) {
					Cell observationsCell = observationsRow.createCell((short) i);
					if (i == 1) {
						observationsCell.setCellValue("");
					}
					observationsCell.setCellStyle(observationsTitleStyle);
				}

				sheet.addMergedRegion(CellRangeAddress.valueOf("B21:Q21"));

				Row createdSignatureRow = sheet.createRow((short) 23);
				CellStyle signatureCellStyle = workbook.createCellStyle();
				signatureCellStyle.setBorderBottom(BorderStyle.THIN);
				for (int i = 6; i < 9; i++) {
					Cell createdSignatureCell = createdSignatureRow.createCell((short) i);
					createdSignatureCell.setCellValue("");
					createdSignatureCell.setCellStyle(signatureCellStyle);
				}

				sheet.addMergedRegion(CellRangeAddress.valueOf("G24:I24"));

				Row createdRow = sheet.createRow((short) 24);
				CellStyle createdRowStyle = workbook.createCellStyle();
				createdRowStyle.setFont(unitTitleFont);
				createdRowStyle.setAlignment(HorizontalAlignment.CENTER);

				Cell createdCell = createdRow.createCell((short) 7);
				createdCell.setCellStyle(createdRowStyle);
				createdCell.setCellValue("Creado");

				Row controlledSignatureRow = sheet.createRow((short) 35);
				for (int i = 6; i < 9; i++) {
					Cell createdSignatureCell = controlledSignatureRow.createCell((short) i);
					createdSignatureCell.setCellValue("");
					createdSignatureCell.setCellStyle(signatureCellStyle);
				}

				sheet.addMergedRegion(CellRangeAddress.valueOf("G35:I35"));

				Row controlledRow = sheet.createRow((short) 36);
				Cell controlledCell = controlledRow.createCell((short) 7);
				controlledCell.setCellStyle(createdRowStyle);
				controlledCell.setCellValue("Controlado");
				sheet.setDefaultColumnWidth(15);

			} catch (Exception ex) {
				// TODO Manejar excepción apropiadamente
			}
		}

		return workbook;
	}

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtility.class);

	public static Workbook getErrorExcel(List<ExcelProduct> excelProducts, S3FileManager s3FileManager) {
		XSSFWorkbook xSSFWorkbook = new XSSFWorkbook();
		try {
			S3FileManager fileManager = s3FileManager;
			InputStream inputStream = fileManager.getFile("excelDescargar/productosDescargar.xlsx");

			XSSFWorkbook errorWB = new XSSFWorkbook(inputStream);
			XSSFSheet errorSheet = errorWB.getSheetAt(0);

			ArrayList<Row> linesWithIssues = new ArrayList<Row>();
			DataFormatter formatter = new DataFormatter();

			CellStyle cellStyle = errorWB.createCellStyle();
			CreationHelper createHelper = errorWB.getCreationHelper();
			cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

			int count = 0;
			for (ExcelProduct excelProduct : excelProducts) {
				count++;
				if (count == 1) {
					Row dataRow = excelProduct.getRow();
					// linesWithIssues.add(dataRow);
				} else if (excelProduct.getHasErrors()) {
					Row dataRow = excelProduct.getRow();
					String errorType = "Advertencia: se crea el producto pero no todos sus componentes";
					if (!excelProduct.getWasCreated()) {
						errorType = "Error: no se creó el producto";
					}
					Cell typeCell = dataRow.createCell(ExcelMap.ERROR_TYPE_COLUMN);
					typeCell.setCellValue(errorType);
					Cell errorCell = dataRow.createCell(ExcelMap.ERROR_COLUMN);
					errorCell.setCellValue(excelProduct.bddErrors);
					linesWithIssues.add(dataRow);
				}
			}
			int rowNum = 1;
			for (Row errorRow : linesWithIssues) {
				Row toAdd = errorSheet.createRow(rowNum);
				Iterator<Cell> cells = errorRow.cellIterator();
				while (cells.hasNext()) {
					Cell existingCell = cells.next();
					Cell cellToAdd = toAdd.createCell(existingCell.getColumnIndex());
					cellToAdd.setCellValue(formatter.formatCellValue(existingCell));
					if (existingCell.getColumnIndex() == 18 || existingCell.getColumnIndex() == 19) {
						cellToAdd.setCellStyle(cellStyle);
					}
				}
				rowNum++;
			}
			return errorWB;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return xSSFWorkbook;
	}

	public static XSSFWorkbook exportProductList(List<Producto> productos, S3FileManager s3FileManager) {
		XSSFWorkbook xSSFWorkbook = new XSSFWorkbook();
		try {
			S3FileManager fileManager = s3FileManager;
			InputStream inputStream = fileManager.getFile("excelDescargar/productosDescargar.xlsx");

			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			XSSFSheet worksheet = wb.getSheetAt(0);
			CellStyle style = wb.createCellStyle();

			style.setRightBorderColor(IndexedColors.BLACK.getIndex());
			style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		
			int rowNumber = 1;
			for (Producto producto : productos) {
				Row row = worksheet.createRow(rowNumber);
				Cell cell = row.createCell(ExcelMap.PRODUCT_CPP);
				cell.setCellStyle(style);

				cell.setCellValue(producto.getCpp() != null ? producto.getCpp() : "");

				cell = row.createCell(ExcelMap.PRODUCT_GTIN);
				cell.setCellValue(producto.getGtin() != null ? producto.getGtin() : "");

				cell = row.createCell(ExcelMap.PRODUCT_ORIGIN_COUNTRY);
				cell.setCellValue(producto.getPaisOrigen() != null ? producto.getPaisOrigen() : "");

				cell = row.createCell(ExcelMap.PRODUCT_DESCRIPTION);
				cell.setCellValue(producto.getDescripcion() != null ? producto.getDescripcion() : "");

				cell = row.createCell(ExcelMap.PRODUCT_TARGET_MARKET);
				cell.setCellValue(producto.getMercadoObjetivo() != null ? producto.getMercadoObjetivo() : "");

				cell = row.createCell(ExcelMap.PRODUCT_BRAND);
				cell.setCellValue(producto.getMarca() != null ? producto.getMarca() : "");

				if (producto.getCategoria() != null) {
					cell = row.createCell(ExcelMap.PRODUCT_MAIN_CATEGORY);
					cell.setCellValue(
							producto.getCategoria().getNombre() != null ? producto.getCategoria().getNombre() : "");
					if (producto.getCategoria().getPadre() != null) {
						cell = row.createCell(ExcelMap.PRODUCT_SECONDARY_CATEGORY);
						cell.setCellValue(producto.getCategoria().getPadre().getNombre() != null
								? producto.getCategoria().getPadre().getNombre()
								: "");
					}
				}

				cell = row.createCell(ExcelMap.PRODUCT_PRESENTATION);
				cell.setCellValue(producto.getPresentacion() != null ? producto.getPresentacion().getNombre() : "");

				cell = row.createCell(ExcelMap.PRODUCT_NET_CONTENT);
				if(producto.getContenidoNeto() != null)
				cell.setCellValue(producto.getContenidoNeto().doubleValue());

				cell = row.createCell(ExcelMap.PRODUCT_CONTENT_UNIT);
				cell.setCellValue(producto.getUnidadMedida() != null ? producto.getUnidadMedida() : "");

				cell = row.createCell(ExcelMap.PRODUCT_NET_WEIGHT);
				if(producto.getPesoBruto() != null)
				cell.setCellValue(producto.getPesoBruto().doubleValue());

				cell = row.createCell(ExcelMap.PRODUCT_WEIGHT_UNIT);
				cell.setCellValue(
						producto.getUnidadMedidaPesoBruto() != null ? producto.getUnidadMedidaPesoBruto() : "");

				cell = row.createCell(ExcelMap.PRODUCT_HEIGHT);
				if(producto.getAlto() != null)
				cell.setCellValue(producto.getAlto().doubleValue());

				cell = row.createCell(ExcelMap.PRODUCT_WIDTH);
				if(producto.getAncho() != null)
				cell.setCellValue(producto.getAncho().doubleValue());

				cell = row.createCell(ExcelMap.PRODUCT_DEPTH);
				if(producto.getProfundidad() != null)
				cell.setCellValue(producto.getProfundidad().doubleValue());

				cell = row.createCell(ExcelMap.PRODUCT_MINIMUM_SELL_LEVEL);
				if(producto.getNivelMinimoVenta() != null)
				cell.setCellValue(producto.getNivelMinimoVenta());

				cell = row.createCell(ExcelMap.PRODUCT_PROMO);
				cell.setCellValue(producto.getEsPromo() != null ? producto.getEsPromo() : false);

				CellStyle cellStyle = wb.createCellStyle();
				CreationHelper createHelper = wb.getCreationHelper();
				cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

				cell = row.createCell(ExcelMap.PRODUCT_SUSPENDIDO_DESDE);
				if (producto.getSuspendidoDesde() != null)
					cell.setCellValue(producto.getSuspendidoDesde().toDate());
				cell.setCellStyle(cellStyle);

				cell = row.createCell(ExcelMap.PRODUCT_SUSPENDIDO_HASTA);
				if (producto.getSuspendidoHasta() != null)
					cell.setCellValue(producto.getSuspendidoHasta().toDate());
				cell.setCellStyle(cellStyle);

				if (producto.getPallet() != null) {
					Pallet pallet = producto.getPallet();
					cell = row.createCell(ExcelMap.PALLET_HEIGHT);
					cell.setCellValue(pallet.getAlto() != null ? pallet.getAlto() : "");

					cell = row.createCell(ExcelMap.PALLET_WIDTH);
					cell.setCellValue(pallet.getAncho() != null ? pallet.getAncho() : "");

					cell = row.createCell(ExcelMap.PALLET_DEPTH);
					cell.setCellValue(pallet.getProfundidad() != null ? pallet.getProfundidad() : "");

					cell = row.createCell(ExcelMap.PALLET_UNITS_INCLUDED);
					cell.setCellValue(pallet.getUnidadesVenta() != null ? pallet.getUnidadesVenta() : "");

					cell = row.createCell(ExcelMap.PALLET_BOXES_INCLUDED);
					cell.setCellValue(pallet.getCajas() != null ? pallet.getCajas() : "");

					cell = row.createCell(ExcelMap.PALLET_BATCHES_INCLUDED);
					cell.setCellValue(pallet.getCamadas() != null ? pallet.getCamadas() : "");
				}

				if (producto.getEmpaques() != null) {
					Set<Empaque> empaques = producto.getEmpaques();
					for (Empaque empaque : empaques) {
						cell = row.createCell(ExcelMap.FIRST_PACK_CPP);
						cell.setCellValue(empaque.getCpp() != null ? empaque.getCpp() : "");

						cell = row.createCell(ExcelMap.FIRST_PACK_GTIN);
						cell.setCellValue(empaque.getGtin() != null ? empaque.getGtin() : "");

						cell = row.createCell(ExcelMap.FIRST_PACK_CLASIFICATION);
						cell.setCellValue(empaque.getClasificacion() != null ? empaque.getClasificacion() : "");

						cell = row.createCell(ExcelMap.FIRST_PACK_NET_WEIGHT);
						if(empaque.getPesoBruto()  != null)
						cell.setCellValue(empaque.getPesoBruto().doubleValue());

						cell = row.createCell(ExcelMap.FIRST_PACK_NET_WEIGHT_UNIT);
						cell.setCellValue(empaque.getUnidadMedida() != null ? empaque.getUnidadMedida() : "");

						cell = row.createCell(ExcelMap.FIRST_PACK_HEIGHT);
						if(empaque.getAlto()  != null)
						cell.setCellValue(empaque.getAlto().doubleValue());

						cell = row.createCell(ExcelMap.FIRST_PACK_WIDTH);
						if(empaque.getAncho()  != null)
						cell.setCellValue(empaque.getAncho().doubleValue());

						cell = row.createCell(ExcelMap.FIRST_PACK_DEPTH);
						if(empaque.getProfundidad()  != null)
						cell.setCellValue(empaque.getProfundidad().doubleValue());

						cell = row.createCell(ExcelMap.FIRST_PACK_QUANTITY);
						if(empaque.getCantidad()  != null)
						cell.setCellValue(empaque.getCantidad().doubleValue());

						if (empaque.getPadre() != null) {
							cell = row.createCell(ExcelMap.SECOND_PACK_CPP);
							cell.setCellValue(empaque.getPadre().getCpp() != null ? empaque.getPadre().getGtin() : "");

							cell = row.createCell(ExcelMap.SECOND_PACK_GTIN);
							cell.setCellValue(empaque.getPadre().getGtin() != null ? empaque.getPadre().getGtin() : "");

							cell = row.createCell(ExcelMap.SECOND_PACK_CLASIFICATION);
							cell.setCellValue(empaque.getPadre().getClasificacion() != null
									? empaque.getPadre().getClasificacion()
									: "");

							cell = row.createCell(ExcelMap.SECOND_PACK_NET_WEIGHT);
							if(empaque.getPadre().getPesoBruto()  != null)
							cell.setCellValue(empaque.getPadre().getPesoBruto().doubleValue());

							cell = row.createCell(ExcelMap.SECOND_PACK_NET_WEIGHT_UNIT);
							cell.setCellValue(
									empaque.getPadre().getUnidadMedida() != null ? empaque.getPadre().getUnidadMedida()
											: "");

							cell = row.createCell(ExcelMap.SECOND_PACK_HEIGHT);
							if(empaque.getPadre().getAlto()  != null)
							cell.setCellValue(empaque.getPadre().getAlto().doubleValue());

							cell = row.createCell(ExcelMap.SECOND_PACK_WIDTH);
							if(empaque.getPadre().getAncho()  != null)
							cell.setCellValue(empaque.getPadre().getAncho().doubleValue());

							cell = row.createCell(ExcelMap.SECOND_PACK_DEPTH);
							if(empaque.getPadre().getProfundidad()  != null)
							cell.setCellValue(empaque.getPadre().getProfundidad().doubleValue());

							cell = row.createCell(ExcelMap.SECOND_PACK_QUANTITY);
							if(empaque.getPadre().getCantidad()  != null)
							cell.setCellValue(empaque.getPadre().getCantidad().doubleValue());

							if (empaque.getPadre().getPadre() != null) {
								cell = row.createCell(ExcelMap.THIRD_PACK_CPP);
								cell.setCellValue(empaque.getPadre().getPadre().getCpp() != null
										? empaque.getPadre().getPadre().getGtin()
										: "");

								cell = row.createCell(ExcelMap.THIRD_PACK_GTIN);
								cell.setCellValue(empaque.getPadre().getPadre().getGtin() != null
										? empaque.getPadre().getPadre().getGtin()
										: "");

								cell = row.createCell(ExcelMap.THIRD_PACK_CLASIFICATION);
								cell.setCellValue(empaque.getPadre().getPadre().getClasificacion() != null
										? empaque.getPadre().getPadre().getClasificacion()
										: "");

								cell = row.createCell(ExcelMap.THIRD_PACK_NET_WEIGHT);
								if( empaque.getPadre().getPadre().getPesoBruto() != null)
								cell.setCellValue(empaque.getPadre().getPadre().getPesoBruto().doubleValue());

								cell = row.createCell(ExcelMap.THIRD_PACK_NET_WEIGHT_UNIT);
								cell.setCellValue(empaque.getPadre().getPadre().getUnidadMedida()  != null ? empaque.getPadre().getPadre().getUnidadMedida() : "");

								cell = row.createCell(ExcelMap.THIRD_PACK_HEIGHT);
								if( empaque.getPadre().getPadre().getAlto() != null)
								cell.setCellValue(empaque.getPadre().getPadre().getAlto().doubleValue());

								cell = row.createCell(ExcelMap.THIRD_PACK_WIDTH);
								if( empaque.getPadre().getPadre().getAncho() != null)
								cell.setCellValue(empaque.getPadre().getPadre().getAncho().doubleValue());

								cell = row.createCell(ExcelMap.THIRD_PACK_DEPTH);
								cell.setCellValue(empaque.getPadre().getPadre().getProfundidad() != null
										? empaque.getPadre().getPadre().getProfundidad().doubleValue()
										: 0);

								cell = row.createCell(ExcelMap.THIRD_PACK_QUANTITY);
								if( empaque.getPadre().getPadre().getCantidad() != null)
								cell.setCellValue(empaque.getPadre().getPadre().getCantidad().doubleValue());
							}

						}
					}
				}
				rowNumber++;
			}
			inputStream.close();
			xSSFWorkbook = wb;			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return xSSFWorkbook;
	}

	public static List<ExcelProduct> excelToProductList(InputStream uploadedInputStream, Company empresa)
			throws ServiceException {
		XSSFWorkbook workbook;

		try {
			workbook = new XSSFWorkbook(uploadedInputStream);
			Sheet sheet = workbook.getSheetAt(0);
			workbook.getNumberOfSheets();
			int totalRows = sheet.getPhysicalNumberOfRows();

			List<ExcelProduct> productos = new ArrayList<ExcelProduct>(totalRows);
			List<String> productosCPP = new ArrayList<>();
			List<String> productosGtin = new ArrayList<>();

			String gtin, cpp, eq1Cpp, eq1Gtin, eq2Cpp, eq2Gtin, eq3Cpp, eq3Gtin, esPromo;
			String mercadoObjetivo, unidadMedidaPesoBruto, nivelMinimoVenta;
			String pais, descripcion, marca, unidadMedida, eq1UnidadMedida, eq2UnidadMedida, eq3UnidadMedida,
					palletCajas, palletCamadas, palletUnidadesVenta, palletAlto, palletAncho, palletProfundidad;
			String contenidoNeto, pesoBruto, alto, ancho, profundidad, eq1PesoBruto, eq1Alto, eq1Ancho, eq1Profundidad,
					eq1Cantidad, eq2PesoBruto, eq2Alto, eq2Ancho, eq2Profundidad, eq2Cantidad, eq1Clasificacion,
					eq2Clasificacion, eq3PesoBruto, eq3Alto, eq3Ancho, eq3Profundidad, eq3Cantidad, eq3Clasificacion,
					linea, division;
			Date suspendidoDesde = null, suspendidoHasta = null;

			Producto producto;

			Categoria categoria;
			Presentacion presentacion;

			Iterator<Row> i = sheet.rowIterator();
			DataFormatter formatter = new DataFormatter();

			if (i.hasNext()) {
				Row dataRow = i.next();
				cpp = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_CPP));
				if (cpp != null && !cpp.trim().isEmpty()) {
					ExcelProduct excelProduct = new ExcelProduct();
					excelProduct.setHasErrors(true);
					excelProduct.setWasCreated(false);
					Cell typeCell = dataRow.createCell(ExcelMap.ERROR_TYPE_COLUMN);
					typeCell.setCellValue("Tipo de Error");
					Cell errorCell = dataRow.createCell(ExcelMap.ERROR_COLUMN);
					errorCell.setCellValue("ERROR");
					excelProduct.setRow(dataRow);
					productos.add(excelProduct);
				}
			}

			while (i.hasNext()) {
				Row dataRow = i.next();
				ExcelProduct excelProduct = new ExcelProduct();
				cpp = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_CPP));
				if (cpp == null || cpp.trim().isEmpty()) {
					continue;
				}
				try {

					excelProduct.setWasCreated(true);
					excelProduct.setHasErrors(false);

					// PRODUCTO
					gtin = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_GTIN));
					pais = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_ORIGIN_COUNTRY));
					descripcion = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_DESCRIPTION));
					mercadoObjetivo = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_TARGET_MARKET));
					unidadMedidaPesoBruto = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_WEIGHT_UNIT));
					nivelMinimoVenta = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_MINIMUM_SELL_LEVEL));
					marca = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_BRAND));
					alto = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_HEIGHT));
					ancho = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_WIDTH));
					profundidad = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_DEPTH));
					unidadMedida = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_CONTENT_UNIT));
					pesoBruto = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_NET_WEIGHT));
					contenidoNeto = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_NET_CONTENT));
					esPromo = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_PROMO));
					Boolean promo = false;
					if (esPromo.toLowerCase().trim().equals("si"))
						promo = true;

					producto = new Producto();
					producto.isValid(cpp, gtin, pais, descripcion, marca, unidadMedida, pesoBruto, contenidoNeto, alto,
							ancho, profundidad, mercadoObjetivo, unidadMedidaPesoBruto, nivelMinimoVenta);
					producto.setExcelData(cpp, gtin, pais, descripcion, marca, unidadMedida, pesoBruto, contenidoNeto,
							alto, ancho, profundidad, mercadoObjetivo, unidadMedidaPesoBruto, nivelMinimoVenta, promo);

					chequearQueElCPPNoEsteRepetido(productosCPP, cpp, true);
					productosCPP.add(cpp);

					try {
						if (DateUtil.isCellDateFormatted(dataRow.getCell(ExcelMap.PRODUCT_SUSPENDIDO_DESDE))) {
							try {
								suspendidoDesde = dataRow.getCell(ExcelMap.PRODUCT_SUSPENDIDO_DESDE).getDateCellValue();
								producto.setSuspendidoDesde(new DateTime(suspendidoDesde));
							} catch (Exception e) {
								suspendidoDesde = null;
							}
						}
					} catch (Exception e) {
						suspendidoDesde = null;
					}

					try {
						if (DateUtil.isCellDateFormatted(dataRow.getCell(ExcelMap.PRODUCT_SUSPENDIDO_HASTA))) {
							try {
								suspendidoHasta = dataRow.getCell(ExcelMap.PRODUCT_SUSPENDIDO_HASTA).getDateCellValue();
								producto.setSuspendidoHasta(new DateTime(suspendidoHasta));
							} catch (Exception e) {
								suspendidoHasta = null;
							}
						}
					} catch (Exception e) {
						suspendidoHasta = null;
					}

					linea = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_MAIN_CATEGORY));
					division = formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_SECONDARY_CATEGORY));
					if ((!linea.equals("") && !division.equals("")) && linea.equals(division)) {
						throw new ModelException(
								"La Categoría Principal (División) no puede ser igual a la Categoría Secundaria (Línea o Familia)");
					}
					categoria = new Categoria();
					Categoria catPadre = new Categoria(linea, (long) 1);
					catPadre.setEmpresa(empresa);
					categoria.setEmpresa(empresa);

					categoria.copyForExcel(division, (long) 2, catPadre);

					presentacion = new Presentacion(
							formatter.formatCellValue(dataRow.getCell(ExcelMap.PRODUCT_PRESENTATION)));

					if (presentacion.getNombre().equals("")) {
						producto.setPresentacion(null);
					} else
						producto.setPresentacion(presentacion);

					producto.setCategoria(categoria);

					// EMPAQUE

					eq1Gtin = formatter.formatCellValue(dataRow.getCell(ExcelMap.FIRST_PACK_GTIN));
					eq2Gtin = formatter.formatCellValue(dataRow.getCell(ExcelMap.SECOND_PACK_GTIN));
					eq3Gtin = formatter.formatCellValue(dataRow.getCell(ExcelMap.THIRD_PACK_GTIN));
					eq1Cpp = formatter.formatCellValue(dataRow.getCell(ExcelMap.FIRST_PACK_CPP));
					eq2Cpp = formatter.formatCellValue(dataRow.getCell(ExcelMap.SECOND_PACK_CPP));
					eq3Cpp = formatter.formatCellValue(dataRow.getCell(ExcelMap.THIRD_PACK_CPP));

					eq1PesoBruto = formatter.formatCellValue(dataRow.getCell(ExcelMap.FIRST_PACK_NET_WEIGHT));
					eq1UnidadMedida = formatter.formatCellValue(dataRow.getCell(ExcelMap.FIRST_PACK_NET_WEIGHT_UNIT));
					eq1Alto = formatter.formatCellValue(dataRow.getCell(ExcelMap.FIRST_PACK_HEIGHT));
					eq1Ancho = formatter.formatCellValue(dataRow.getCell(ExcelMap.FIRST_PACK_WIDTH));
					eq1Profundidad = formatter.formatCellValue(dataRow.getCell(ExcelMap.FIRST_PACK_DEPTH));
					eq1Cantidad = formatter.formatCellValue(dataRow.getCell(ExcelMap.FIRST_PACK_QUANTITY));
					eq1Clasificacion = formatter.formatCellValue(dataRow.getCell(ExcelMap.FIRST_PACK_CLASIFICATION));

					eq2PesoBruto = formatter.formatCellValue(dataRow.getCell(ExcelMap.SECOND_PACK_NET_WEIGHT));
					eq2UnidadMedida = formatter.formatCellValue(dataRow.getCell(ExcelMap.SECOND_PACK_NET_WEIGHT_UNIT));
					eq2Alto = formatter.formatCellValue(dataRow.getCell(ExcelMap.SECOND_PACK_HEIGHT));
					eq2Ancho = formatter.formatCellValue(dataRow.getCell(ExcelMap.SECOND_PACK_WIDTH));
					eq2Profundidad = formatter.formatCellValue(dataRow.getCell(ExcelMap.SECOND_PACK_DEPTH));
					eq2Cantidad = formatter.formatCellValue(dataRow.getCell(ExcelMap.SECOND_PACK_QUANTITY));
					eq2Clasificacion = formatter.formatCellValue(dataRow.getCell(ExcelMap.SECOND_PACK_CLASIFICATION));

					eq3PesoBruto = formatter.formatCellValue(dataRow.getCell(ExcelMap.THIRD_PACK_NET_WEIGHT));
					eq3UnidadMedida = formatter.formatCellValue(dataRow.getCell(ExcelMap.THIRD_PACK_NET_WEIGHT_UNIT));
					eq3Alto = formatter.formatCellValue(dataRow.getCell(ExcelMap.THIRD_PACK_HEIGHT));
					eq3Ancho = formatter.formatCellValue(dataRow.getCell(ExcelMap.THIRD_PACK_WIDTH));
					eq3Profundidad = formatter.formatCellValue(dataRow.getCell(ExcelMap.THIRD_PACK_DEPTH));
					eq3Cantidad = formatter.formatCellValue(dataRow.getCell(ExcelMap.THIRD_PACK_QUANTITY));
					eq3Clasificacion = formatter.formatCellValue(dataRow.getCell(ExcelMap.THIRD_PACK_CLASIFICATION));

					palletAlto = formatter.formatCellValue(dataRow.getCell(ExcelMap.PALLET_HEIGHT));
					palletAncho = formatter.formatCellValue(dataRow.getCell(ExcelMap.PALLET_WIDTH));
					palletProfundidad = formatter.formatCellValue(dataRow.getCell(ExcelMap.PALLET_DEPTH));
					palletCajas = formatter.formatCellValue(dataRow.getCell(ExcelMap.PALLET_BOXES_INCLUDED));
					palletCamadas = formatter.formatCellValue(dataRow.getCell(ExcelMap.PALLET_BATCHES_INCLUDED));
					palletUnidadesVenta = formatter.formatCellValue(dataRow.getCell(ExcelMap.PALLET_UNITS_INCLUDED));

					if (palletCajas != null && !palletCajas.trim().equals("")) {
						Pallet pallet = new Pallet(palletAlto, palletAncho, palletProfundidad, palletCajas,
								palletCamadas, palletUnidadesVenta);
						producto.setPallet(pallet);
					}

					Empaque abuelo = new Empaque();
					Empaque padre = new Empaque();
					Empaque hijo = new Empaque();

					try {
						Set<Empaque> empaques = new HashSet<Empaque>();
						if (!eq1Gtin.equals("") && !eq1Cpp.equals("") || !eq1UnidadMedida.equals("")
								|| !eq1Alto.equals("") || !eq1Ancho.equals("") || !eq1Profundidad.equals("")
								|| !eq1PesoBruto.equals("") || !eq1Cantidad.equals("")
								|| !eq1Clasificacion.equals("")) {
							hijo.isValid(eq1Gtin, "1", eq1UnidadMedida, eq1Alto, eq1Ancho, eq1Profundidad, eq1PesoBruto,
									eq1Cantidad, eq1Cpp, eq1Clasificacion);
							hijo.copyForExcel(eq1Gtin, "1", eq1UnidadMedida, eq1Alto, eq1Ancho, eq1Profundidad,
									eq1PesoBruto, eq1Cantidad, eq1Cpp, eq1Clasificacion);
							hijo.setEmpresa(empresa);
							hijo.setPresentacion(null);
							if (eq1Gtin.equals(gtin)) {
								throw new PackException("GTIN duplicado");
							}
							chequearQueElGtinNoEsteRepetido(productosGtin, eq1Gtin, false);
							empaques.add(hijo);
							try {
								if (!eq2Gtin.equals("") && !eq2Cpp.equals("") || !eq2UnidadMedida.equals("")
										|| !eq2Alto.equals("") || !eq2Ancho.equals("") || !eq2Profundidad.equals("")
										|| !eq2PesoBruto.equals("") || !eq2Cantidad.equals("")
										|| !eq2Clasificacion.equals("")) {
									padre.isValid(eq2Gtin, "2", eq2UnidadMedida, eq2Alto, eq2Ancho, eq2Profundidad,
											eq2PesoBruto, eq2Cantidad, eq2Cpp, eq2Clasificacion);
									padre.copyForExcel(eq2Gtin, "2", eq2UnidadMedida, eq2Alto, eq2Ancho, eq2Profundidad,
											eq2PesoBruto, eq2Cantidad, eq2Cpp, eq2Clasificacion);
									padre.setEmpresa(empresa);

									padre.setPresentacion(null);
									if (eq2Gtin.equals(gtin) || eq2Gtin.equals(eq1Gtin)) {
										throw new PackException("GTIN duplicado");
									}
									chequearQueElGtinNoEsteRepetido(productosGtin, eq2Gtin, false);
									hijo.setPadre(padre);
									try {
										if (!eq3Gtin.equals("") && !eq3Cpp.equals("") || !eq3UnidadMedida.equals("")
												|| !eq3Alto.equals("") || !eq3Ancho.equals("")
												|| !eq3Profundidad.equals("") || !eq3PesoBruto.equals("")
												|| !eq3Cantidad.equals("") || !eq3Clasificacion.equals("")) {
											abuelo.isValid(eq3Gtin, "3", eq3UnidadMedida, eq3Alto, eq3Ancho,
													eq3Profundidad, eq3PesoBruto, eq3Cantidad, eq3Cpp,
													eq3Clasificacion);
											abuelo.copyForExcel(eq3Gtin, "3", eq3UnidadMedida, eq3Alto, eq3Ancho,
													eq3Profundidad, eq3PesoBruto, eq3Cantidad, eq3Cpp,
													eq3Clasificacion);
											abuelo.setEmpresa(empresa);

											abuelo.setPresentacion(null);

											if (eq3Gtin.equals(gtin) || eq3Gtin.equals(eq1Gtin)
													|| eq3Gtin.equals(eq1Gtin)) {
												throw new PackException("GTIN duplicado");
											}
											chequearQueElGtinNoEsteRepetido(productosGtin, eq3Gtin, false);
											padre.setPadre(abuelo);
										}
									} catch (PackException ex) {
										excelProduct.setHasErrors(true);
										excelProduct.setWasCreated(false);
										excelProduct.bddErrors += "Empaque 3: " + ex.getMessage() + ". ";
									}
								}
							} catch (PackException ex) {
								excelProduct.setHasErrors(true);
								excelProduct.setWasCreated(false);
								excelProduct.bddErrors += "Empaque 2: " + ex.getMessage() + ". ";
							}
							producto.setEmpaques(empaques);
						}
					} catch (PackException ex) {
						producto.setEmpaques(null);
						excelProduct.bddErrors += "Empaque 1: " + ex.getMessage() + ". ";
						excelProduct.setHasErrors(true);
						excelProduct.setWasCreated(false);
					}
					excelProduct.setProduct(producto);

				} catch (ModelException ex) {

					excelProduct.bddErrors += ex.getMessage() + ". ";
					excelProduct.setHasErrors(true);
					excelProduct.setWasCreated(false);

				} catch (Exception ex) {
					excelProduct.bddErrors += ex.getMessage() + ". ";

					excelProduct.setHasErrors(true);
					excelProduct.setWasCreated(false);
				}
				if (excelProduct.getWasCreated()) {
					excelProduct.getProduct().setEmpresa(empresa);
				}
				excelProduct.setRow(dataRow);
				productos.add(excelProduct);
			}

			workbook.close();

			return productos;

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

	@SuppressWarnings("unused")
	private void ExcelHelper(Sheet sheet) {
		int totalRows = sheet.getPhysicalNumberOfRows();

		Map<String, Integer> map = new HashMap<String, Integer>();
		HSSFRow row = (HSSFRow) sheet.getRow(0);

		short minColIx = row.getFirstCellNum();
		short maxColIx = row.getLastCellNum();

		for (short colIx = minColIx; colIx < maxColIx; colIx++) {
			HSSFCell cell = row.getCell(colIx);
			map.put(cell.getStringCellValue().toLowerCase(), cell.getColumnIndex());
		}

		for (int x = 1; x <= totalRows; x++) {
			HSSFRow dataRow = (HSSFRow) sheet.getRow(x);

			int idxForGtin = map.get("gtin");
			int idxForColumn2 = map.get("pais de origen");
			int idxForColumn3 = map.get("Column3");

			HSSFCell cell1 = dataRow.getCell(idxForGtin);
			HSSFCell cell2 = dataRow.getCell(idxForColumn2);
			HSSFCell cell3 = dataRow.getCell(idxForColumn3);
		}
	}

	public static List<ExcelRNCliente> procesarExcelClientesRN(InputStream stream) throws ServiceException {
		PoijiOptions options = PoijiOptionsBuilder.settings().headerStart(2).build();
		List<ExcelRNCliente> clientesACrear = Poiji.fromExcel(stream, PoijiExcelType.XLS, ExcelRNCliente.class,
				options);

		return clientesACrear;
	}

	public static List<ExcelRNProducto> procesarExcelProductosRN(InputStream stream) throws ServiceException {
		PoijiOptions options = PoijiOptionsBuilder.settings().headerStart(3).build();
		List<ExcelRNProducto> productosACrear = Poiji.fromExcel(stream, PoijiExcelType.XLS, ExcelRNProducto.class,
				options);

		return productosACrear;
	}

}
