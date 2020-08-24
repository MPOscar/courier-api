package courier.uy.core.excel.formats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import io.netty.util.internal.ThreadLocalRandom;

public class TataLogistics implements IExcelFormat {

	@Override
	public Workbook GetWorkbook(List<Producto> allProducts) {
		Workbook workbook = new XSSFWorkbook();
		// CreationHelper createHelper = workbook.getCreationHelper();

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

}
