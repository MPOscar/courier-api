package courier.uy.core.utils;

import courier.uy.core.db.LaboratorioDAO;
import courier.uy.core.entity.*;
import courier.uy.core.resources.dto.ExcelProduct;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.exceptions.PackException;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.repository.IPrecioRepository;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Component
public class ExcelUtilityProductosLaboratorio {

	@Autowired
	private LaboratorioDAO laboratorioDAO;

	private final IPrecioRepository precioRepository;

	public ExcelUtilityProductosLaboratorio(IPrecioRepository precioRepository){
		this.precioRepository = precioRepository;
	}

	public List<List<ExcelProduct>> productosExcelLaboratorioToList(InputStream uploadedInputStream, Company empresa)
			throws ServiceException {
		XSSFWorkbook workbook;

		try {
			workbook = new XSSFWorkbook(uploadedInputStream);
			Sheet sheet = workbook.getSheetAt(0);
			workbook.getNumberOfSheets();
			int totalRows = sheet.getPhysicalNumberOfRows();

			List<List<ExcelProduct>> productos = new ArrayList<>();

			List<ExcelProduct> productosLaboratorio = new ArrayList<>();
			List<ExcelProduct> productosLaboratorioKitPromocional = new ArrayList<>();

			Iterator<Row> i = sheet.rowIterator();

			String cpp, gtin, descripcion, marca, esKitPromocional, setPromocional, formaFarmaceutica = "", contenidoNetoUnidad = "";
			String contenidoNetoPorUsoUnidad, formaDeAdministracion, contieneAzucar, contieneLactosa;
			String contenidoNetoCantidad = "", contenidoNetoPorUsoCantidad, cantidad, enCantidad;
			String nombre, unidad, enUnidad;
			List<String> gtinProductosExcel = new ArrayList<>();
			List<String> cppProductosExcel = new ArrayList<>();
			Map<String, String> repetido = new HashMap<>();
			while (i.hasNext()) {
				Producto producto = new Producto();
				ExcelProduct excelProduct = new ExcelProduct();
				Row dataRow = i.next();
				if (dataRow.getRowNum() > 2 && (!getRowValue(dataRow, 0).equals("") || !getRowValue(dataRow, 1).equals(""))) {
					gtin = getRowValue(dataRow, 0);
					if(gtin.equals("")){
						excelProduct.setHasErrors(true);
						excelProduct.bddErrors += " Debe completar el GTIN del producto, no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
					}
					repetido = chequearQueElGtinNoEsteRepetido(gtinProductosExcel, gtin, dataRow.getRowNum() + 1, empresa);
					if(repetido.get("error").equals("true")){
						excelProduct.setHasErrors(true);
						excelProduct.bddErrors += repetido.get("message");
					}else{
						gtinProductosExcel.add(gtin);
					}

					cpp = getRowValue(dataRow, 1);
					if(cpp.equals("")){
						excelProduct.setHasErrors(true);
						excelProduct.bddErrors += " Debe completar el Codigo Interno del producto, no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
					}
					repetido = chequearQueElCppNoEsteRepetido(cppProductosExcel, gtin, dataRow.getRowNum() + 1, empresa);
					if(repetido.get("error").equals("true")){
						excelProduct.setHasErrors(true);
						excelProduct.bddErrors += repetido.get("message");
					}else{
						cppProductosExcel.add(cpp);
					}

					descripcion = getRowValue(dataRow, 2);
					if(descripcion.equals("")){
						excelProduct.setHasErrors(true);
						excelProduct.bddErrors += " Debe completar la descripcion del producto, no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
					}

					marca = getRowValue(dataRow, 3);
					if(marca.equals("")){
						excelProduct.setHasErrors(true);
						excelProduct.bddErrors += " Debe completar la marca del producto, no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
					}

					esKitPromocional = getRowValue(dataRow, 4);
					setPromocional = getRowValue(dataRow, 5);
					Set<ComponenteActivo> componentesActivos = new HashSet<>();
					if(!esKitPromocional.equals("S")){
						formaFarmaceutica = getRowValue(dataRow, 6);
						if(formaFarmaceutica.equals("")){
							excelProduct.setHasErrors(true);
							excelProduct.bddErrors += " Debe completar la Forma Farmaceutica del producto, no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
						}

						contenidoNetoCantidad = getRowValue(dataRow, 7);
						if(contenidoNetoCantidad.equals("")){
							excelProduct.setHasErrors(true);
							excelProduct.bddErrors += " Debe completar la Cantidad del Contenido Neto del producto, no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
						}

						contenidoNetoUnidad = getRowValue(dataRow, 8);
						if(contenidoNetoUnidad.equals("")){
							excelProduct.setHasErrors(true);
							excelProduct.bddErrors += " Debe completar la Unidad del Contenido Neto del producto, no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
						}

						contenidoNetoPorUsoCantidad = getRowValue(dataRow, 9);
						contenidoNetoPorUsoUnidad = getRowValue(dataRow, 10);

						formaDeAdministracion = getRowValue(dataRow, 11);
						if(contenidoNetoUnidad.equals("")){
							excelProduct.setHasErrors(true);
							excelProduct.bddErrors += " Debe completar la Forma de Administracion del producto, no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
						}

						contieneAzucar = getRowValue(dataRow, 12);
						contieneLactosa = getRowValue(dataRow, 13);

						int iterator = 14;
						int count  = 1;
						while(!getRowValue(dataRow, iterator).equals("")
								|| !getRowValue(dataRow, iterator + 1).equals("")
								|| !getRowValue(dataRow, iterator + 2).equals("")
								|| !getRowValue(dataRow, iterator + 3).equals("")
								|| !getRowValue(dataRow, iterator + 4).equals("")
						){
							nombre = getRowValue(dataRow, iterator);
							if(nombre.equals("")){
								excelProduct.setHasErrors(true);
								excelProduct.bddErrors += " El Campo Nombre del Componente Activo " + count + ", no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
							}
							iterator++;
							cantidad = getRowValue(dataRow, iterator);
							if(cantidad.equals("")){
								excelProduct.setHasErrors(true);
								excelProduct.bddErrors += " El Campo Cantidad del Componente Activo " + count + ", no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
							}
							iterator++;
							unidad = getRowValue(dataRow, iterator);
							if(unidad.equals("")){
								excelProduct.setHasErrors(true);
								excelProduct.bddErrors += " El Campo Unidad del Componente Activo " + count + ", no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
							}
							iterator++;
							enCantidad = getRowValue(dataRow, iterator);
							if(enCantidad.equals("")){
								excelProduct.setHasErrors(true);
								excelProduct.bddErrors += " El Campo -En Cantidad- del Componente Activo " + count + ", no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
							}
							iterator++;
							enUnidad = getRowValue(dataRow, iterator);
							if(enUnidad.equals("")){
								excelProduct.setHasErrors(true);
								excelProduct.bddErrors += " El Campo -En Unidad- del Componente Activo " + count + ", no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
							}
							iterator++;
							ComponenteActivo componenteActivo = new ComponenteActivo(nombre, cantidad, unidad, enCantidad, enUnidad);
							componentesActivos.add(componenteActivo);
							count ++;
						}

						if(componentesActivos == null || componentesActivos.size() < 1){
							if(contenidoNetoUnidad.equals("")){
								excelProduct.setHasErrors(true);
								excelProduct.bddErrors += " El producto debe tener al menos un Componente Activo, no puede estar en blanco, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
							}
						}


					}else{
						producto.setEskitPromocional(esKitPromocional.equals("S") ? true : false);
						if(!setPromocional.equals("")){
							String[] stringArray = setPromocional.split(",");
							List<String> listParts = Arrays.asList(stringArray);
							KitPromocional kitPromocional = new KitPromocional();
							kitPromocional.getSproductos().addAll(listParts);
							producto.setKitPromocional(kitPromocional);
							contenidoNetoCantidad = String.valueOf(producto.getKitPromocional().getSproductos().size());
							contenidoNetoUnidad = "EA";
						}else{
							excelProduct.setHasErrors(true);
							excelProduct.bddErrors += " Campo GTIN de productos que lo componen no puede ser vacio el producto fue marcado como KIT, error en la linea: " + (dataRow.getRowNum() + 1) + "  \n";
						}
						contenidoNetoPorUsoCantidad = "";
						contenidoNetoPorUsoUnidad = "";
						formaDeAdministracion = "";
						contieneAzucar = "";
						contieneLactosa = "";
					}

					AtributosLaboratorio atributosLaboratorio = new AtributosLaboratorio(
							contenidoNetoCantidad,
							contenidoNetoUnidad,
							contenidoNetoPorUsoCantidad,
							contenidoNetoPorUsoUnidad,
							formaFarmaceutica,
							formaDeAdministracion,
							contieneAzucar.equals("S") ? true : false,
							contieneLactosa.equals("S") ? true : false,
							componentesActivos);

					producto.setGtin(gtin);
					producto.setCpp(cpp);
					producto.setDescripcion(descripcion);
					producto.setMarca(marca);
					producto.setCpp(cpp);
					producto.setTipo("farmaceutico");
					producto.setAtributosLaboratorio(atributosLaboratorio);

					if(!contenidoNetoCantidad.equals("")) {
						producto.setContenidoNeto(new BigDecimal(contenidoNetoCantidad));
						producto.setUnidadMedida(contenidoNetoUnidad);
					}

					excelProduct.setProduct(producto);

					if(producto.getEskitPromocional()){
						productosLaboratorioKitPromocional.add(excelProduct);
					}else{
						productosLaboratorio.add(excelProduct);
					}


				}
			}
			workbook.close();
			productos.add(0, productosLaboratorio);
			productos.add(1, productosLaboratorioKitPromocional);
			return productos;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Ocurrió un error leyendo los archivos del excel: " + e.getMessage());
		}
	}

	public static String getRowValue(Row dataRow, int position){
		String val = "";
		try {
			CellType type = dataRow.getCell(position).getCellType();
			switch (type.name()) {
				case "NUMERIC":
					double numericCellValue = dataRow.getCell(position).getNumericCellValue();
					val = String.format ("%.0f", numericCellValue);
					break;
				case "STRING":
					val = dataRow.getCell(position).getStringCellValue();
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

	public Map<String, String> chequearQueElGtinNoEsteRepetido(List<String> gtinProductosExcel, String gtin, int lineaNumber, Company empresa) throws ModelException, PackException {
		Map<String, String> result = new HashMap<>();
		result.put("error", "false");
		if(gtinProductosExcel.indexOf(gtin) > -1){
			result.put("error", "true");
			result.put("message", " Existe un producto en el excel con este gtin, error en la linea:" + lineaNumber + "\n");
		}else {
			List<Producto> productos = laboratorioDAO.findAllByGtin(gtin);
			if(productos  != null && productos.size() > 1){
				result.put("error", "true");
				result.put("message", " Existe un producto en la base de datos con este gtin, error en la linea:" + lineaNumber + "\n");
			}
		}
		return result;
	}

	public Map<String, String> chequearQueElCppNoEsteRepetido(List<String> cppProductosExcel, String cpp, int lineaNumber, Company empresa) throws ModelException, PackException {
		Map<String, String> result = new HashMap<>();
		result.put("error", "false");
		if(cppProductosExcel.indexOf(cpp) > -1){
			result.put("error", "true");
			result.put("message", " Existe un producto en el excel con este cpp, error en la linea:" + lineaNumber + "\n");
		}else {
			List<Producto> productos = laboratorioDAO.findAllByCpp(cpp, empresa.getSId());
			if(productos  != null && productos.size() > 1){
				result.put("error", "true");
				result.put("message", " Existe un producto en la base de datos con este cpp, error en la linea:" + lineaNumber + "\n");
			}
		}
		return result;
	}

}
