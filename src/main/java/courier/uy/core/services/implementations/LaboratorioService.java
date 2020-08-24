package courier.uy.core.services.implementations;

import courier.uy.CourierConfiguration;
import courier.uy.core.db.LaboratorioDAO;
import courier.uy.core.db.ProductosDAO;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.services.interfaces.ILaboratorioService;
import courier.uy.core.utils.ExcelUtilityProductosLaboratorio;
import courier.uy.core.utils.S3FileManager;
import courier.uy.api.dto.ProductoLaboratorioDTO;
import courier.uy.core.resources.dto.ExcelProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.repository.IPrecioRepository;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class LaboratorioService implements ILaboratorioService {
	private CourierConfiguration configuration;

	@Autowired
	IPrecioRepository precioRepository;

	@Autowired
	ProductosDAO productosDAO;

	@Autowired
	LaboratorioDAO laboratorioDAO;

	@Autowired
	ExcelUtilityProductosLaboratorio excelUtilityProductosLaboratorio;

	private S3FileManager s3FileManager;

	public LaboratorioService() {
	}

	public HashMap<String, String> actualizarProductos(InputStream inputStream, UsuarioEmpresa usuarioEmpresa) throws ServiceException {
		List<List<ExcelProduct>> excelProducts = excelUtilityProductosLaboratorio.productosExcelLaboratorioToList(inputStream, usuarioEmpresa.getEmpresa());
		List<ExcelProduct> productosLaboratorio = excelProducts.get(0);
		List<ExcelProduct> productosLaboratorioKitPromocional = excelProducts.get(1);
		int productosInsertados = 0;
		int productosActualizados = 0;
		int productosConError = 0;
		int kitDeProductosConError = 0;
		String erroresProductos = "";
		String erroresKit = "";
		for (ExcelProduct excelProduct: productosLaboratorio) {
			if(!excelProduct.getHasErrors()){
				Producto productoLaboratorio = excelProduct.getProduct();
				Optional<Producto> optionalProducto = laboratorioDAO.findByGtinAndEmpresa(usuarioEmpresa.getEmpresa().getSId(), productoLaboratorio.getGtin());
				if(optionalProducto.isPresent()){
					Producto producto = optionalProducto.get();
					producto.laboratorio(productoLaboratorio);
					productosDAO.save(producto);
					productosActualizados ++;
				}else{
					productoLaboratorio.setEmpresa(usuarioEmpresa.getEmpresa());
					productoLaboratorio.setSempresa(usuarioEmpresa.getEmpresa().getId());
					productosDAO.save(productoLaboratorio);
					productosInsertados ++;
				}
			}else{
				erroresProductos += excelProduct.bddErrors;
				productosConError ++;
			}
		}

		for (ExcelProduct excelProduct: productosLaboratorioKitPromocional) {
			Producto productoLaboratorio = excelProduct.getProduct();
			if(productoLaboratorio.getKitPromocional() != null) {
				for (String gtin : productoLaboratorio.getKitPromocional().getSproductos()) {
					Optional<Producto> productoDelKit = productosDAO.findByGtin(productoLaboratorio.getGtin());
					if (!productoDelKit.isPresent()) {
						excelProduct.setHasErrors(true);
						excelProduct.bddErrors += "El producto que compone el kit con el gtin " + gtin + " no existe en la base de datos /n";
					}
				}
			}
			if(!excelProduct.getHasErrors()){
				Optional<Producto> optionalProducto = laboratorioDAO.findByGtinAndEmpresa(usuarioEmpresa.getEmpresa().getSId(), productoLaboratorio.getGtin());
				if(optionalProducto.isPresent()){
					Producto producto = optionalProducto.get();
					producto.laboratorio(productoLaboratorio);
					productosDAO.save(producto);
					productosActualizados ++;
				}else{
					productoLaboratorio.setEmpresa(usuarioEmpresa.getEmpresa());
					productoLaboratorio.setSempresa(usuarioEmpresa.getEmpresa().getId());
					productosDAO.save(productoLaboratorio);
					productosInsertados ++;
				}
			}else{
				erroresKit += excelProduct.bddErrors;
				kitDeProductosConError ++;
			}
		}

		HashMap<String, String> result = new HashMap<String, String>();
		result.put("productosInsertados", String.valueOf(productosInsertados));
		result.put("productosActualizados", String.valueOf(productosActualizados));
		result.put("productosConError", String.valueOf(productosConError));
		result.put("erroresProductos", erroresProductos);
		result.put("erroresKit", erroresKit);
		result.put("kitDeProductosConError", String.valueOf(kitDeProductosConError));
		result.put("totalDeErrores", String.valueOf(productosConError + kitDeProductosConError));
		return result;
	}

	public ProductoLaboratorioDTO getProductoLaboratorio(UsuarioEmpresa usuarioEmpresa, String gtin){
		return laboratorioDAO.findByGtin(usuarioEmpresa.getEmpresa().getSId(), gtin);
	}

	public List<ProductoLaboratorioDTO> getAllProductoLaboratorio(UsuarioEmpresa usuarioEmpresa){
		return laboratorioDAO.getAllProductoLaboratorioByEmpresa(usuarioEmpresa.getEmpresa().getSId());
	}


}
